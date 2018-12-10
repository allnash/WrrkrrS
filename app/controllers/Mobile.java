// Copyright 2018 OmegaTrace Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License

package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import org.apache.commons.validator.routines.EmailValidator;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import utils.Mailer;
import utils.Slack;
import utils.Utils;

import java.sql.Timestamp;
import java.util.Calendar;

import static controllers.ControllerHelper.buildJsonResponse;
import static utils.GoogleAuthenticator.getRandomSecretKey;

/*
 * This controller contains Blog app common logic
 */
public class Mobile extends BaseController {


    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result index() {
        return ok();
    }

    public Result signin() {

        JsonNode json = request().body().asJson();
        String ipAddress = request().remoteAddress().toUpperCase();
        Organization organization = organization();
        Logger.info("login request coming from ip addr - " + ipAddress);

        try {

            String email = json.get("email").asText();
            String password = json.get("password").asText();
            ObjectNode errors = Json.newObject();

            if (email == null || email.isEmpty()) {
                throw new Exception("email");
            }

            if (password == null || password.isEmpty()) {
                throw new Exception("email");
            }

            if (organization == null) {
                return badRequest();
            }


            User user = User.findByEmailAndPassword(email, password, organization);

            if (!user.getEnabled()) {
                throw new Exception("account_disabled");
            }
            if (user.getDeleted()) {
                throw new Exception("account_disabled");
            }
            if (!user.getOrganization().getApproved()) {
                throw new Exception("organization_not_approved");
            }
            if (!user.getOrganization().getEnabled()) {
                throw new Exception("organization_not_enabled");
            }
            // Create our internal session
            Session s = new Session(user.email, ipAddress, user.getOrganization());
            Calendar c = Calendar.getInstance();
            c.setTime(Calendar.getInstance().getTime());
            c.add(Calendar.MONTH, 6);
            s.setExpiresAt(new Timestamp(c.getTimeInMillis()));
            // Bypass two factor for mobile auth
            s.setTwoFactored(true);
            s.save();

            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("user", Json.toJson(user));
            result.set("session", Json.toJson(s));
            return ok(result);

        } catch (NullPointerException e) {
            Logger.error("Authentication exception - " + e.getMessage());
            return badRequest(buildJsonResponse("error", "Problem signing with your credentials. If the problem persists. Please contact support@wrrkrr.com."));
        } catch (Exception e) {
            if (e.getMessage().equals("account_disabled")) {
                Logger.error("Authentication exception - " + e.getMessage());
                return badRequest(buildJsonResponse("error", "Your account has been disabled. Please contact your administrator."));
            } else if (e.getMessage().equals("email")) {
                Logger.error("Authentication exception - " + e.getMessage());
                return badRequest(buildJsonResponse("error", "Please enter valid email and password."));
            } else if (e.getMessage().equals("organization_not_enabled")) {
                return badRequest(buildJsonResponse("error", "Your Account(s) Organization is on hold by OmegaTrace. Check with your administrator or contact support@omegatrace.com."));
            } else {
                Logger.error("Authentication exception - " + e.getMessage());
                return badRequest(buildJsonResponse("error", "Problem signing with your credentials. If the problem persists. Please contact support@wrrkrr.com."));
            }
        }

    }

    public Result signup() {

        JsonNode json = request().body().asJson();
        String ipAddress = request().remoteAddress().toUpperCase();
        Logger.info("signup request coming from ip addr - " + ipAddress);

        try {

            String email = json.get("email").asText();
            String firstName = json.get("first_name").asText();
            String lastName = json.get("last_name").asText();
            String workspaceName = json.get("workspace_name").asText();
            ObjectNode errors = Json.newObject();

            if (email == null || email.isEmpty()) {
                errors.put("email", "Email is required");
            }

            EmailValidator validator = EmailValidator.getInstance();
            if (!validator.isValid(email)) {
                Logger.info("Invalid email provided for signup request. ");
                errors.put("email", "Email is invalid.");
            }

            if (firstName == null || firstName.isEmpty()) {
                errors.put("first_name", "First Name is required");
            }

            if (lastName == null || lastName.isEmpty()) {
                errors.put("last_name", "Last Name is required");
            }

            if (workspaceName == null || workspaceName.isEmpty()) {
                errors.put("workspace_name", "Workspace Name is required");
            }

            Organization o = Organization.findByWorkspaceName(workspaceName);
            boolean isNewOrganization = false;

            if (o == null) {

                Logger.info("attempted to create user with non-existing organization - " + Utils.getEmailDomain(email));
                o = Organization.createIfNotPresentForEmail(workspaceName);
                o.save();
                // IMPORTANT STEP:
                Bootstrap.organization(o);
                Slack.postMessage(o, "New Organization - " + o.getName() + " has been created. Please review organization, and approve.", null);
                isNewOrganization = true;
                Logger.info("creating organization - " + Utils.getEmailDomain(email));

            } else {

                User u = User.findByEmailAndWorkspace(email, workspaceName);

                if (u != null) {
                    Logger.info("attempted to create another user with existing user - " + u.getId());
                    throw new Exception("email");
                }
            }

            if(!isNewOrganization){
                if(!o.getEnabled()){
                    throw new Exception("organization_not_enabled");
                }

                if(!o.getSelfServiceSignup()){
                    Logger.info("Organization self service signup disabled Failure.");
                    throw new Exception("self_service_signup");
                }
            }

            if (errors.size() > 0) {
                throw new Exception("");
            }

            Place p = new Place("home", PlaceType.findByName("HOME", o), o);
            p.save();
            User newUser = new User(firstName, lastName, email, getRandomSecretKey(), p, o);
            newUser.setRole(Role.findByName("Vendor", o));

            if (newUser != null) {
                newUser.setConfirmationHash(Utils.nextSessionId());
                newUser.save();
                Mailer.MailNewAccountEmail(newUser);
                Slack.postMessage(o, "New User - " + newUser.getEmail() + " has registered. Please review his access, and role.", null);
                if (!o.getApproved()) {
                    for (User user : User.findAllByOrganization(Organization.findByEmailDomain("omegatrace.com"))) {
                        if (user.getSuperadmin()) {
                            // TODO: Sent email here.
                        }
                    }
                }
            }

            session().clear();
            ObjectNode wrapper = Json.newObject();
            wrapper.put("status", "ok");
            wrapper.put("success", "ok");
            return ok(wrapper);

        } catch (NullPointerException e) {
            Logger.error("Signup - Null pointer Exception - " + e.getMessage());
            return badRequest(buildJsonResponse("error", "Incorrect email or password"));
        } catch (Exception e) {
            if (e.getMessage().equals("self_service_signup")) {
                Logger.error("Authentication exception - " + e.getMessage());
                return badRequest(buildJsonResponse("error", "Self service sign up has been disabled for this organization."));
            } else if (e.getMessage().equals("email")) {
                Logger.error("Authentication exception - " + e.getMessage());
                return badRequest(buildJsonResponse("error", "Another user exists with your email and workspace."));
            } else if (e.getMessage().equals("organization_not_enabled")) {
                return badRequest(buildJsonResponse("error", "Your Account(s) Organization is on hold by OmegaTrace. Check with your administrator or contact support@omegatrace.com."));
            } else {
                Logger.error("Authentication exception - " + e.getMessage());
                return badRequest(buildJsonResponse("error", "Problem signing up with your information."));
            }
        }

    }


    public Result invalidateSession(String sessionId) {
        Session mySession = Session.findBySessionId(sessionId);
        ObjectNode result = Json.newObject();
        if(mySession == null){
            return badRequest();
        }
        if(mySession.isExpired()){
            result.put("status", "fail");
            result.put("error", "Session has expired.");
        } else {
            mySession.invalidate();
            mySession.save();
            result.put("status", "ok");
            result.put("success", "ok");
        }
        return ok(result);
    }

    public Result validateSession(String sessionId) {
        Session mySession = Session.findBySessionId(sessionId);
        if (mySession.isExpired()) {
            return badRequest();
        } else {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("session", Json.toJson(mySession));
            return ok(result);
        }
    }

}