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
import de.triology.recaptchav2java.ReCaptcha;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import utils.Mailer;
import utils.Slack;
import utils.Utils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static controllers.ControllerHelper.*;
import static utils.GoogleAuthenticator.getRandomSecretKey;
import static utils.Mailer.MailPasswordResetNotificationEmail;

/*
 * This controller contains Blog app common logic
 */
public class Application extends BaseController {


    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result index() {
        return redirect("/index.html");
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

            if (firstName == null || firstName.isEmpty()) {
                errors.put("first_name", "First Name is required");
            }

            if (lastName == null || lastName.isEmpty()) {
                errors.put("last_name", "Last Name is required");
            }

            if (workspaceName == null || workspaceName.isEmpty()) {
                errors.put("workspace_name", "Workspace Name is required");
            }

            String recaptchaResponse = null;
            if (json.get("recaptcha_response") != null) {
                recaptchaResponse = json.get("recaptcha_response").asText();
            }
            boolean isRecaptchaValid = false;
            if (recaptchaResponse == null) {
                return badRequest();
            }

            isRecaptchaValid = new ReCaptcha(Utils.getRecaptchaSecret()).isValid(recaptchaResponse);

            if (!isRecaptchaValid) {
                Logger.info("Recaptcha Validation Failure.");
                throw new Exception("recaptcha");
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

            Place  p = new Place("home", PlaceType.findByName("HOME", o), o);
            p.save();
            User newUser = new User(firstName, lastName, email, getRandomSecretKey(), p, o);
            newUser.setRole(Role.findByName("Admin", o));

            if (newUser != null) {
                newUser.setConfirmationHash(Utils.nextSessionId());
                if(isNewOrganization){
                    newUser.setAdmin(true);
                }
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
            } else if (e.getMessage().equals("recaptcha")) {
                Logger.error("Authentication exception - " + e.getMessage());
                return badRequest(buildJsonResponse("error", "Invalid Recaptcha, Are you a bot?"));
            } else if (e.getMessage().equals("email")) {
                Logger.error("Authentication exception - " + e.getMessage());
                return badRequest(buildJsonResponse("error", "Another user exists with your email"));
            } else if (e.getMessage().equals("organization_not_enabled")) {
                return badRequest(buildJsonResponse("error", "Your Account(s) Organization is on hold by OmegaTrace. Check with your administrator or contact support@omegatrace.com."));
            } else {
                Logger.error("Authentication exception - " + e.getMessage());
                return badRequest(buildJsonResponse("error", "Problem signing up with your information."));
            }
        }

    }

    public Result login() {

        JsonNode json = request().body().asJson();
        String ipAddress = request().remoteAddress().toUpperCase();

        try {

            String email = json.get("email").asText();
            String password = json.get("password").asText();
            ObjectNode errors = Json.newObject();

            if (email == null || email.equals("")) {
                errors.put("email", "Invalid Email Entry");
            }

            if (password == null || password.equals("")) {
                errors.put("password", "Invalid Password Entry");
            }

            if (errors.size() > 0) {
                return badRequest(buildJsonResponse("error", errors.asText()));
            }

            User user = User.findByEmailAndPassword(email, password, organization());

            if (user == null) {
                return badRequest(buildJsonResponse("error", "Incorrect email or password"));
            } else {
                // Clear session
                session().clear();
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
                ObjectNode wrapper = Json.newObject();
                wrapper.put("status", "ok");
                wrapper.put("success", "ok");
                wrapper.set("user", Json.toJson(user));
                // Create our internal session
                Session s = new Session(user.email, ipAddress, user.getOrganization());
                s.save();
                // set email and session id in play session
                session("email", user.email);
                session("session_id", s.sessionId);
                session("organization_id", s.getOrganization().getId());
                response().setCookie(getSessionCookie(s));
                response().setCookie(getOrganizationCookie(s));
                wrapper.set("session", Json.toJson(s));
                return ok(wrapper);
            }
        } catch (NullPointerException e) {
            Logger.error("Authentication exception - " + e.getMessage());
            return badRequest(buildJsonResponse("error", "Incorrect email or password"));
        } catch (Exception e) {
            if (e.getMessage().equals("account_disabled")) {
                Logger.error("Authentication exception - " + e.getMessage());
                return badRequest(buildJsonResponse("error", "Your Account has been disabled"));
            } else if (e.getMessage().equals("organization_not_approved")) {
                Logger.error("Authentication exception - " + e.getMessage());
                return badRequest(buildJsonResponse("error", "Your Account is going through the approval. Check back after sometime."));
            } else if (e.getMessage().equals("organization_not_enabled")) {
                Logger.error("Authentication exception - " + e.getMessage());
                return badRequest(buildJsonResponse("error", "Your Account(s) Organization has been disabled by OmegaTrace. Check with your administrator or contact support@omegatrace.com."));
            } else {
                e.printStackTrace();
                Logger.error("Authentication exception - " + e.getMessage());
                return badRequest(buildJsonResponse("error", "Problem Authenticating with your credentials"));
            }
        }

    }

    public Result confirmAccount() {

        JsonNode json = request().body().asJson();
        String ipAddress = request().remoteAddress().toUpperCase();

        try {
            String email = json.get("email").asText();
            String confirmationHash = json.get("confirmation_hash").asText();
            String password = json.get("password").asText();
            String confirmPassword = json.get("confirm_password").asText();
            ObjectNode errors = Json.newObject();

            if (email == null || email.equals("")) {
                errors.put("email", "Invalid email entry");
            }

            if (confirmationHash == null || confirmationHash.equals("")) {
                errors.put("reset_token", "Invalid confirmation hash");
            }

            if (password == null || password.equals("")) {
                errors.put("password", "Invalid password entry");
            }

            if (confirmPassword == null || confirmPassword.equals("")) {
                errors.put("confirm_password", "Invalid password entry");
            }

            if (!(password.length() >= 9)) {
                errors.put("password", "Password entries are less than 9 characters");
            }

            if (!confirmPassword.equals(password)) {
                errors.put("confirm_password", "Password entries do not match");
            }

            if (errors.size() > 0) {
                return badRequest(buildJsonResponse("error", errors.asText()));
            }

            User user = User.findByConfirmationHash(confirmationHash);
            Logger.warn("IP address for post confirm account for user - " + user.getId() + " is - " + ipAddress);

            if (user == null) {
                return badRequest(buildJsonResponse("success", "We were unable to confirm your account. If you have an account with us then please contact support@omegatrace.com"));
            } else {
                session().clear();
                ObjectNode result = Json.newObject();
                result.put("status", "ok");
                result.put("success", "ok");
                user.setStrongPassword(password);
                user.setConfirmationHash("");
                user.setConfirmed(true);
                user.save();
                return ok(result);
            }
        } catch (NullPointerException e) {
            Logger.error("Authentication exception - " + e.getMessage());
            return badRequest(buildJsonResponse("error", "Error Changing your password"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Logger.error(" Error while running authentication algorithm - " + e.getMessage());
            return badRequest(buildJsonResponse("error", "Error Changing your password"));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            Logger.error(" Error while running authentication algorithm - " + e.getMessage());
            return badRequest(buildJsonResponse("error", "Error Changing your password"));
        }

    }

    public Result requestResetPassword() {

        JsonNode json = request().body().asJson();
        String ipAddress = request().remoteAddress().toUpperCase();

        try {

            String email = json.get("email").asText();
            String workspaceName = json.get("workspace_name").asText();
            ObjectNode errors = Json.newObject();

            if (email == null || email.isEmpty()) {
                errors.put("email", "Email is required.");
            }

            if (workspaceName == null || workspaceName.isEmpty()) {
                errors.put("workspace_name", "Workspace name is Required.");
            }

            if (errors.size() > 0) {
                return badRequest(buildJsonResponse("error", errors.asText()));
            }

            String recaptchaResponse = null;
            if (json.get("recaptcha_response") != null) {
                recaptchaResponse = json.get("recaptcha_response").asText();
            }

            boolean isRecaptchaValid = false;

            if (recaptchaResponse == null) {
                return badRequest(buildJsonResponse("error", "Server error. Recaptcha failure."));
            }

            isRecaptchaValid = new ReCaptcha(Utils.getRecaptchaSecret()).isValid(recaptchaResponse);

            if (!isRecaptchaValid) {
                Logger.info("Recaptcha Validation Failure.");
                return badRequest(buildJsonResponse("error", "Please prove that ypu are not a robot."));
            }

            Organization organization = Organization.findByWorkspaceName(workspaceName);
            User user = User.findByEmail(email, organization);

            if (user != null) {
                session().clear();
                user.setResetToken(Utils.nextSessionId());
                Mailer.MailPasswordResetEmail(user);
                user.save();
            } else {
                return badRequest(buildJsonResponse("error", "Bad email or workspace name."));
            }

            ObjectNode wrapper = Json.newObject();
            wrapper.put("status", "ok");
            wrapper.put("success", "ok");
            return ok(wrapper);

        } catch (NullPointerException e) {
            Logger.error("Reset Password Exception - " + e.getMessage());
            e.printStackTrace();
            return badRequest(buildJsonResponse("error", "Server error. Bad email or workspace name."));
        }

    }

    public Result postResetPassword() {

        JsonNode json = request().body().asJson();
        String ipAddress = request().remoteAddress().toUpperCase();

        try {
            String email = json.get("email").asText();
            String resetToken = json.get("reset_token").asText();
            String newPassword = json.get("new_password").asText();
            String confirmNewPassword = json.get("confirm_new_password").asText();
            ObjectNode errors = Json.newObject();

            if (email == null || email.equals("")) {
                errors.put("email", "Invalid Email Entry");
            }

            if (resetToken == null || resetToken.equals("")) {
                errors.put("reset_token", "Invalid Reset Token Entry");
            }

            if (newPassword == null || resetToken.equals("")) {
                errors.put("new_password", "Invalid Password Entry");
            }

            if (confirmNewPassword == null || confirmNewPassword.equals("")) {
                errors.put("confirm_new_password", "Invalid Password Entry");
            }

            if (!(newPassword.length() >= 8)) {
                errors.put("new_password", "Password Entries are less than 8 characters");
            }

            if (!confirmNewPassword.equals(newPassword)) {
                errors.put("confirm_new_password", "Password Entries do not match");
            }

            if (errors.size() > 0) {
                return badRequest(buildJsonResponse("error", errors.asText()));
            }

            User user = User.findByResetToken(resetToken);
            Logger.warn("IP address for post reset password for user - " + user.getId() + " is - " + ipAddress);

            if (user == null) {
                return badRequest(buildJsonResponse("success", "We were unable to reset your password. If you have an account with us. Please contact support@omegatrace.com"));
            } else {
                session().clear();
                ObjectNode wrapper = Json.newObject();
                wrapper.put("status", "ok");
                wrapper.put("success", "ok");
                user.setStrongPassword(newPassword);
                user.setResetToken("");
                user.save();
                MailPasswordResetNotificationEmail(user);
                return ok(wrapper);
            }
        } catch (NullPointerException e) {
            Logger.error("Authentication exception - " + e.getMessage());
            return badRequest(buildJsonResponse("error", "Error Changing your password"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Logger.error(" Error while running authentication algorithm - " + e.getMessage());
            return badRequest(buildJsonResponse("error", "Error Changing your password"));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            Logger.error(" Error while running authentication algorithm - " + e.getMessage());
            return badRequest(buildJsonResponse("error", "Error Changing your password"));
        }

    }

    public Result logout() {
        Session mySession = Session.findBySessionId(session().get("session_id"));
        mySession.invalidate();
        mySession.save();
        response().discardCookie("session_id");
        response().discardCookie("session_id", "/", Utils.getDomain());
        session().clear();
        return ok(buildJsonResponse("success", "Logged out successfully"));
    }

    public Result isAuthenticated() {
        if (session().get("email") == null) {
            return unauthorized();
        } else {
            return ok(buildJsonResponse("success", "AUTHENTICATED"));
        }
    }

}