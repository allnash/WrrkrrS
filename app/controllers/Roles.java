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
import models.Organization;
import models.Role;
import models.User;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

import java.util.List;

/*
 * This controller contains Roles app common logic
 */

public class Roles extends BaseController {

    @Security.Authenticated(Secured.class)
    public Result get(String organizationId, String roleId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        ObjectNode result = Json.newObject();
        Role role = Role.findById(roleId);
        if(myUser.getSuperadmin()){
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("role", Json.toJson(role));
        } else if(myUser.getOrganization().equals(Role.findById(roleId).getOrganization())) {
            if(!role.getOrganization().equals(Organization.findById(organizationId))){
                return badRequest();
            } else {
                result.put("status", "ok");
                result.put("success", "ok");
                result.set("role", Json.toJson(role));
            }
        } else {
            return badRequest();
        }
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result getAll(String organizationId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);

        // Null check for organizationId
        if(organizationId == null){
            organizationId = myUser.getOrganizationId();
        }

        // Fetch Data (Roles)
        List<Role> roles;
        if(myUser.getSuperadmin()){
            roles = Role.findAllByOrganization(Organization.findById(organizationId));
        } else {
            roles = Role.findAllByOrganization(myUser.getOrganization());
        }

        // Create Response
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("roles", Json.toJson(roles));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result post(String organizationId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        if(!myUser.getSuperadmin()) {
            return badRequest();
        }
        try {
            JsonNode json = request().body().asJson();
            Role submittedRole = Json.fromJson(json, Role.class);
            submittedRole.setOrganization(Organization.findById(organizationId));
            submittedRole.save();
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("role", Json.toJson(submittedRole));
            return ok(result);
        } catch (Exception e){
            return badRequest();
        }

    }

    @Security.Authenticated(Secured.class)
    public Result put(String organizationId, String roleId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Role role = Role.findById(roleId);
        if(!myUser.getSuperadmin()) {
            return badRequest();
        }
        if(!myUser.getOrganization().equals(role.getOrganization())){
            return badRequest();
        }
        if(!role.getOrganization().equals(Organization.findById(organizationId))){
            return badRequest();
        }
        try {
            JsonNode json = request().body().asJson();
            Role submittedRole = Json.fromJson(json, Role.class);
            role.setAvailablePublicly(submittedRole.availablePublicly);
            role.setName(submittedRole.name);
            role.setDescription(submittedRole.description);
            role.setDashboardModuleAccess(submittedRole.dashboardModuleAccess);
            role.setDevicesModuleAccess(submittedRole.devicesModuleAccess);
            role.setProjectsModuleAccess(submittedRole.projectsModuleAccess);
            role.setWorkflowModuleAccess(submittedRole.workflowModuleAccess);
            role.setIssuesModuleAccess(submittedRole.issuesModuleAccess);
            role.setCollaboratorModuleAccess(submittedRole.collaboratorModuleAccess);
            role.setTeamsModuleAccess(submittedRole.teamsModuleAccess);
            role.setMembersModuleAccess(submittedRole.membersModuleAccess);
            role.setMessagesModuleAccess(submittedRole.messagesModuleAccess);
            role.setAnalyticsModuleAccess(submittedRole.analyticsModuleAccess);
            role.setMarketplaceModuleAccess(submittedRole.marketplaceModuleAccess);
            role.setDeveloperModuleAccess(submittedRole.developerModuleAccess);
            role.setDefaultRoute(submittedRole.defaultRoute);
            role.save();
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("role", Json.toJson(role));
            return ok(result);
        }
        catch (Exception e){
            return badRequest();
        }
    }

}