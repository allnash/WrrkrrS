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

package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.Finder;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;

@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"organization_id", "name"})
)
@Entity

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role extends BaseTenantModel {

    public enum RoleAccess {
        NONE, READ, READ_WRITE
    }

    @Constraints.Required
    public String name;
    @Constraints.Required
    public String description;
    @Column(name = "role_image_id")
    @ManyToOne
    @JsonProperty("role_image")
    public TypeImage roleImage;
    @Constraints.Required
    @JsonProperty("available_publicly")
    public Boolean availablePublicly; // YES MEANS 1 | NO MEANS 0
    @JsonProperty("dashboard_module_access")
    public Role.RoleAccess dashboardModuleAccess;
    @JsonProperty("devices_module_access")
    public Role.RoleAccess devicesModuleAccess;
    @Constraints.Required
    @JsonProperty("projects_module_access")
    public Role.RoleAccess projectsModuleAccess;
    @Constraints.Required
    @JsonProperty("workflow_module_access")
    public Role.RoleAccess workflowModuleAccess;
    @Constraints.Required
    @JsonProperty("issues_module_access")
    public Role.RoleAccess issuesModuleAccess;
    @Constraints.Required
    @JsonProperty("collaborator_module_access")
    public Role.RoleAccess collaboratorModuleAccess;
    @JsonProperty("teams_module_access")
    public Role.RoleAccess teamsModuleAccess;
    @Constraints.Required
    @JsonProperty("analytics_module_access")
    public Role.RoleAccess analyticsModuleAccess;
    @Constraints.Required
    @JsonProperty("members_module_access")
    public Role.RoleAccess membersModuleAccess;
    @Constraints.Required
    @JsonProperty("messages_module_access")
    public Role.RoleAccess messagesModuleAccess;
    @Constraints.Required
    @JsonProperty("marketplace_module_access")
    public Role.RoleAccess marketplaceModuleAccess;
    @Constraints.Required
    @JsonProperty("developer_module_access")
    public Role.RoleAccess developerModuleAccess;
    @JsonProperty("default_route")
    public String defaultRoute;

    public Role() {

    }

    public Role(String name, String description, Organization organization,
                Role.RoleAccess dashboardModuleAccess,
                Role.RoleAccess devicesModuleAccess,
                Role.RoleAccess projectsModuleAccess,
                Role.RoleAccess workflowModuleAccess,
                Role.RoleAccess issuesModuleAccess,
                Role.RoleAccess collaboratorModuleAccess,
                Role.RoleAccess teamsModuleAccess,
                Role.RoleAccess membersModuleAccess,
                Role.RoleAccess messagesModuleAccess,
                Role.RoleAccess analyticsModuleAccess,
                Role.RoleAccess marketplaceModuleAccess,
                Role.RoleAccess developerModuleAccess) {
        this.name = name;
        this.description = description;
        this.organization = organization;
        // All module access options.
        this.dashboardModuleAccess = dashboardModuleAccess;
        this.devicesModuleAccess = devicesModuleAccess;
        this.projectsModuleAccess = projectsModuleAccess;
        this.workflowModuleAccess = workflowModuleAccess;
        this.issuesModuleAccess = issuesModuleAccess;
        this.collaboratorModuleAccess = collaboratorModuleAccess;
        this.teamsModuleAccess = teamsModuleAccess;
        this.membersModuleAccess = membersModuleAccess;
        this.messagesModuleAccess = messagesModuleAccess;
        this.analyticsModuleAccess = analyticsModuleAccess;
        this.marketplaceModuleAccess = marketplaceModuleAccess;
        this.developerModuleAccess = developerModuleAccess;
        // This will ensure if this role is available for use by everyone in the organization. Purely for Display.
        this.availablePublicly = true;
        this.defaultRoute = "index.dashboard";
        this.save();
    }

    public static Role add(String name, String description, Organization organization,
                           Role.RoleAccess dashboardModuleAccess,
                           Role.RoleAccess devicesModuleAccess,
                           Role.RoleAccess projectsModuleAccess,
                           Role.RoleAccess workflowModuleAccess,
                           Role.RoleAccess issuesModuleAccess,
                           Role.RoleAccess collaboratorModuleAccess,
                           Role.RoleAccess teamsModuleAccess,
                           Role.RoleAccess membersModuleAccess,
                           Role.RoleAccess messagesModuleAccess,
                           Role.RoleAccess analyticsModuleAccess,
                           Role.RoleAccess marketplaceModuleAccess,
                           Role.RoleAccess developerModuleAccess, String defaultRoute) {
        Role d = new Role(name, description, organization, dashboardModuleAccess, devicesModuleAccess,
                projectsModuleAccess, workflowModuleAccess, issuesModuleAccess, collaboratorModuleAccess,
                teamsModuleAccess, membersModuleAccess, messagesModuleAccess, analyticsModuleAccess,
                marketplaceModuleAccess, developerModuleAccess);
        d.setDefaultRoute(defaultRoute);
        d.save();
        return d;
    }

    public static Finder<Long, Role> find = new Finder<>(Role.class);

    public static List<Role> all() {
        return find.all();
    }

    public static Role findById(String id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static List<Role> findAllByOrganization(Organization organization) {
        return find.query().where().eq("organization_id", organization.getId()).findList();
    }

    public static Role findByName(String name, Organization organization) {
        return find.query().where()
                .eq("name", name)
                .eq("organization_id", organization.getId())
                .findOne();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeImage getRoleImage() {
        return roleImage;
    }

    public void setRoleImage(TypeImage roleImage) {
        this.roleImage = roleImage;
    }

    public Boolean getAvailablePublicly() {
        return availablePublicly;
    }

    public void setAvailablePublicly(Boolean availablePublicly) {
        this.availablePublicly = availablePublicly;
    }

    public Role.RoleAccess getDashboardModuleAccess() {
        if (this.organization.getLicense().getDashboardModuleAccess()) {
            return dashboardModuleAccess;
        } else {
            return RoleAccess.NONE;
        }
    }

    public void setDashboardModuleAccess(Role.RoleAccess dashboardModuleAccess) {
        this.dashboardModuleAccess = dashboardModuleAccess;
    }

    public Role.RoleAccess getDevicesModuleAccess() {
        if (this.organization.getLicense().getDevicesModuleAccess()) {
            return devicesModuleAccess;
        } else {
            return RoleAccess.NONE;
        }
    }

    public void setDevicesModuleAccess(Role.RoleAccess devicesModuleAccess) {
        this.devicesModuleAccess = devicesModuleAccess;
    }

    public Role.RoleAccess getProjectsModuleAccess() {
        if (this.organization.getLicense().getProjectsModuleAccess()) {
            return projectsModuleAccess;
        } else {
            return RoleAccess.NONE;
        }
    }

    public void setProjectsModuleAccess(Role.RoleAccess projectsModuleAccess) {
        this.projectsModuleAccess = projectsModuleAccess;
    }

    public Role.RoleAccess getIssuesModuleAccess() {
        if (this.organization.getLicense().getIssuesModuleAccess()) {
            return issuesModuleAccess;
        } else {
            return RoleAccess.NONE;
        }
    }

    public void setIssuesModuleAccess(Role.RoleAccess issuesModuleAccess) {
        this.issuesModuleAccess = issuesModuleAccess;
    }

    public Role.RoleAccess getCollaboratorModuleAccess() {
        if (this.organization.getLicense().getCollaboratorModuleAccess()) {
            return collaboratorModuleAccess;
        } else {
            return RoleAccess.NONE;
        }
    }

    public void setCollaboratorModuleAccess(Role.RoleAccess collaboratorModuleAccess) {
        this.collaboratorModuleAccess = collaboratorModuleAccess;
    }

    public Role.RoleAccess getTeamsModuleAccess() {
        if (this.organization.getLicense().getTeamsModuleAccess()) {
            return teamsModuleAccess;
        } else {
            return RoleAccess.NONE;
        }
    }

    public void setTeamsModuleAccess(Role.RoleAccess teamsModuleAccess) {
        this.teamsModuleAccess = teamsModuleAccess;
    }

    public Role.RoleAccess getAnalyticsModuleAccess() {
        if (this.organization.getLicense().getAnalyticsModuleAccess()) {
            return analyticsModuleAccess;
        } else {
            return RoleAccess.NONE;
        }
    }

    public void setAnalyticsModuleAccess(Role.RoleAccess analyticsModuleAccess) {
        this.analyticsModuleAccess = analyticsModuleAccess;
    }

    public Role.RoleAccess getMembersModuleAccess() {
        if (this.organization.getLicense().getMembersModuleAccess()) {
            return membersModuleAccess;
        } else {
            return RoleAccess.NONE;
        }
    }

    public void setMembersModuleAccess(Role.RoleAccess membersModuleAccess) {
        this.membersModuleAccess = membersModuleAccess;
    }

    public Role.RoleAccess getMessagesModuleAccess() {
        if (this.organization.getLicense().getMessagesModuleAccess()) {
            return messagesModuleAccess;
        } else {
            return RoleAccess.NONE;
        }
    }

    public void setMessagesModuleAccess(Role.RoleAccess messagesModuleAccess) {
        this.messagesModuleAccess = messagesModuleAccess;
    }

    public Role.RoleAccess getMarketplaceModuleAccess() {
        if (this.organization.getLicense().getMarketplaceModuleAccess()) {
            return marketplaceModuleAccess;
        } else {
            return RoleAccess.NONE;
        }
    }

    public void setMarketplaceModuleAccess(Role.RoleAccess marketplaceModuleAccess) {
        this.marketplaceModuleAccess = marketplaceModuleAccess;
    }

    public String getDefaultRoute() {
        return defaultRoute;
    }

    public void setDefaultRoute(String defaultRoute) {
        this.defaultRoute = defaultRoute;
    }

    public Role.RoleAccess getWorkflowModuleAccess() {
        if (this.organization.getLicense().getWorkflowModuleAccess()) {
            return workflowModuleAccess;
        } else {
            return RoleAccess.NONE;
        }
    }

    public void setWorkflowModuleAccess(Role.RoleAccess workflowModuleAccess) {
        this.workflowModuleAccess = workflowModuleAccess;
    }

    public Role.RoleAccess getDeveloperModuleAccess() {
        if (this.organization.getLicense().getDeveloperModuleAccess()) {
            return developerModuleAccess;
        } else {
            return RoleAccess.NONE;
        }
    }

    public void setDeveloperModuleAccess(Role.RoleAccess developerModuleAccess) {
        this.developerModuleAccess = developerModuleAccess;
    }

    public static void bootstrapRolesForOrganization(Organization o) {
        Role.add("Default", "Default Role (NO ACCESS)", o,
                Role.RoleAccess.NONE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.NONE, "index.profile");

        Role.add("Admin", "Organizational Administrators", o,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.READ_WRITE, "index.dashboard");

        /*
           String name,
           String description,
           Organization organization,
           Role.RoleAccess dashboardModuleAccess,
           Role.RoleAccess devicesModuleAccess,
           Role.RoleAccess projectsModuleAccess,
           Role.RoleAccess workflowModuleAccess,
           Role.RoleAccess issuesModuleAccess,
           Role.RoleAccess collaboratorModuleAccess,
           Role.RoleAccess teamsModuleAccess,
           Role.RoleAccess membersModuleAccess,
           Role.RoleAccess messagesModuleAccess,
           Role.RoleAccess analyticsModuleAccess,
           Role.RoleAccess marketplaceModuleAccess,
           Role.RoleAccess developerModuleAccess,
           String defaultRoute
        * */
        Role.add("Vendor", "Organizational Vendors", o,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.READ_WRITE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.NONE,
                Role.RoleAccess.READ_WRITE, "index.devices");
    }

}
