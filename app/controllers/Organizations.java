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
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import utils.Mailer;
import utils.Slack;
import utils.Utils;

import java.util.ArrayList;

import static controllers.ControllerHelper.buildJsonResponse;
import static utils.GoogleAuthenticator.getRandomSecretKey;

/*
 * This controller contains Organizations app common logic
 */

public class Organizations extends BaseController {


    @Security.Authenticated(Secured.class)
    public Result post() {

        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        if (!myUser.getSuperadmin()) {
            return badRequest();
        }

        JsonNode json = request().body().asJson();
        String name = json.get("name").asText();
        String domain = json.get("email_domain").asText();
        String workspaceName = json.get("workspace_name").asText();
        Boolean approved = json.get("approved").asBoolean();
        Boolean enabled = json.get("enabled").asBoolean();
        String externalId = "";
        if(json.has("external_id")){
            externalId = json.get("external_id").asText("");
        }
        Organization o = new Organization(name, workspaceName);
        o.setEmailDomain(domain);
        o.setExternalId(externalId);
        Organization c = Organization.findByEmailDomain("omegatrace.com");
        ArrayList<Organization> defaultCollaborators = new ArrayList<>();
        defaultCollaborators.add(c);
        o.setCollaborators(defaultCollaborators);
        o.setApproved(approved);
        if(approved){
            o.setApprovedBy(myUser);
        }
        o.setEnabled(enabled);
        o.setBy(myUser);
        // Organization Save
        o.save();
        // IMPORTANT STEP
        // Organization Boot
        Bootstrap.organization(o);

        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("organization", Json.toJson(o));
        return ok(result);
    }


    @Security.Authenticated(Secured.class)
    public Result postInviteUser(String orgnizationId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);

        JsonNode json = request().body().asJson();
        String firstName = json.get("first_name").asText();
        String lastName = json.get("last_name").asText();
        String email = json.get("email").asText();

        Organization organization = Organization.findById(orgnizationId);

        if(organization == null){
            return badRequest();
        }

        if (myUser.getOrganization().equals(organization) || myUser.getSuperadmin()) {

            if(User.findByEmail(email, myOrg) !=null){
                return badRequest(buildJsonResponse("error", "Another user exists with this email"));
            }

            // Create user
            Place  p = new Place("home", PlaceType.findByName("HOME", organization), organization);
            p.save();
            User newUser = new User(firstName, lastName, email, getRandomSecretKey(), p, organization);
            newUser.save();
            // Email user invite. Post any other notifications.
            newUser.setConfirmationHash(Utils.nextSessionId());
            newUser.setRole(Role.findByName("Default", organization));
            newUser.save();
            Mailer.MailNewAccountInviteEmail(newUser, myUser);
            Slack.postMessage(organization, "A new user record was created with `email` - " + newUser.getEmail() + " . Please review his access, and role.", null);
            // Reply but do not send
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            return ok(result);

        } else {
            return badRequest(buildJsonResponse("error", "You are not authorized to send invitations"));
        }
    }

    @Security.Authenticated(Secured.class)
    public Result get(String organizationId) {
        Organization organization = Organization.findById(organizationId);
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        if (myUser.getSuperadmin()) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.set("organization", Json.toJson(organization));
            return ok(result);
        } else if(myUser.getOrganization().equals(organization)){
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.set("organization", Json.toJson(organization));
            return ok(result);
        } else {
            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result getAllOrganizations() {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        if (!myUser.getSuperadmin()) {
            return badRequest();
        } else {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("organizations", Json.toJson(Organization.all()));
            return ok(result);
        }
    }

    @Security.Authenticated(Secured.class)
    public Result putApprove(String id) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        JsonNode json = request().body().asJson();
        if (myUser.getSuperadmin()) {
            Organization organization = Organization.findById(id);
            if(!organization.approved){
                organization.setApproved(true);
                organization.setEnabled(true);
                for(User user: User.findAllByOrganization(organization)){
                    // Mailer.MailOrganizationApprovalEmail(user);
                    Logger.info("Mailing user - " + user.id + " of org approval.");

                }
            }
            organization.save();
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            return ok(result);
        } else {
            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result put(String id) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        JsonNode json = request().body().asJson();
        if (myUser.getSuperadmin()) {
            Organization submittedOrganization = Json.fromJson(json, Organization.class);
            Organization organization = Organization.findById(id);
            organization.setEmailDomain(submittedOrganization.emailDomain);
            organization.setName(submittedOrganization.name);
            organization.setExternalId(submittedOrganization.externalId);
            organization.setSlackHook(submittedOrganization.slackHook);
            organization.setSelfServiceSignup(submittedOrganization.selfServiceSignup);
            if(submittedOrganization.approved){
                organization.setApproved(submittedOrganization.approved);
                organization.setApprovedBy(myUser);
            }
            organization.setEnabled(submittedOrganization.enabled);
            organization.save();
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("organization", Json.toJson(organization));
            return ok(result);
        } else {
            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result postTestSlackMessage(String id) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        JsonNode json = request().body().asJson();
        if (myUser.getSuperadmin()) {
            Organization organization = Organization.findById(id);
            String slackHook = json.get("slack_hook").asText();
            // Send simple slack test message
            SlackApi api = new SlackApi(slackHook);
            api.call(new SlackMessage("Hello, World!"));
            Logger.info("Sending Slack Message to org: - " + organization.name + " | Hello, World!.");
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            return ok(result);
        } else {
            return badRequest();
        }
    }

}