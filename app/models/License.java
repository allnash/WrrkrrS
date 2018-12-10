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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.Finder;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
public class License extends BaseModel {

    public enum LicenseStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }

    /**
     * License Model
     */

    @JsonProperty("max_messages")
    public Integer maxMessages;
    @JsonProperty("name")
    @Column(length = 64, nullable = false)
    public String name;
    @JsonProperty("license_status")
    public LicenseStatus status;
    @JsonProperty("is_client")
    public Boolean isClient;

    @JsonProperty("renewed_time")
    public Date renewedTime;
    @JsonProperty("expires_time")
    public Date expiresTime;
    // All Modules

    @JsonProperty("dashboard_module_access")
    public Boolean dashboardModuleAccess;
    @JsonProperty("devices_module_access")
    public Boolean devicesModuleAccess;
    @Constraints.Required
    @JsonProperty("projects_module_access")
    public Boolean projectsModuleAccess;
    @Constraints.Required
    @JsonProperty("workflow_module_access")
    public Boolean workflowModuleAccess;
    @Constraints.Required
    @JsonProperty("issues_module_access")
    public Boolean issuesModuleAccess;
    @Constraints.Required
    @JsonProperty("collaborator_module_access")
    public Boolean collaboratorModuleAccess;
    @JsonProperty("teams_module_access")
    public Boolean teamsModuleAccess;
    @Constraints.Required
    @JsonProperty("analytics_module_access")
    public Boolean analyticsModuleAccess;
    @Constraints.Required
    @JsonProperty("members_module_access")
    public Boolean membersModuleAccess;
    @Constraints.Required
    @JsonProperty("messages_module_access")
    public Boolean messagesModuleAccess;
    @Constraints.Required
    @JsonProperty("marketplace_module_access")
    public Boolean marketplaceModuleAccess;
    @Constraints.Required
    @JsonProperty("developer_module_access")
    public Boolean developerModuleAccess;

    public License(String name) {
        /*
         * Set 365 Day expiry
         *
         **/
        Calendar c = Calendar.getInstance();
        c.setTime(Calendar.getInstance().getTime());
        c.add(Calendar.DATE, 365);
        this.expiresTime = c.getTime();
        this.name = name;
        // Default # of messages
        this.maxMessages = 10000;
        this.status = LicenseStatus.ACTIVE;
        this.isClient = false;
        this.dashboardModuleAccess = true;
        this.devicesModuleAccess = true;
        this.projectsModuleAccess = false;
        this.workflowModuleAccess = false;
        this.issuesModuleAccess = false;
        this.collaboratorModuleAccess = false;
        this.teamsModuleAccess = false;
        this.membersModuleAccess = true;
        this.messagesModuleAccess = false;
        this.analyticsModuleAccess = false;
        this.marketplaceModuleAccess = false;
        this.developerModuleAccess = false;
    }

    public static License basicLicense(Organization o) {
        String licenseName = "Basic License, domain:";
        if(o.getEmailDomain() == null){
            licenseName = licenseName +  " not provided";
        } else {
            licenseName = licenseName +  o.getEmailDomain();
        }
        License l = new License(licenseName);
        l.save();
        return l;
    }

    public static License basicBetaLicense(Organization o) {
        License l =  new License("Full License, domain:" + o.getWorkspaceName() +
                ", workspace:" + o.getWorkspaceName());
        l.dashboardModuleAccess = true;
        l.devicesModuleAccess = true;
        l.projectsModuleAccess = true;
        l.workflowModuleAccess = true;
        l.issuesModuleAccess = false;
        l.collaboratorModuleAccess = false;
        l.teamsModuleAccess = true;
        l.membersModuleAccess = true;
        l.messagesModuleAccess = false;
        l.analyticsModuleAccess = true;
        l.marketplaceModuleAccess = false;
        l.developerModuleAccess = false;
        l.save();
        return l;
    }

    public static License fullBetaLicense(Organization o) {
        License l =  new License("Full with Beta features License, domain:" + o.getEmailDomain());
        l.dashboardModuleAccess = true;
        l.devicesModuleAccess = true;
        l.projectsModuleAccess = true;
        l.workflowModuleAccess = true;
        l.issuesModuleAccess = true;
        l.collaboratorModuleAccess = true;
        l.teamsModuleAccess = true;
        l.membersModuleAccess = true;
        l.messagesModuleAccess = true;
        l.analyticsModuleAccess = true;
        l.marketplaceModuleAccess = true;
        l.developerModuleAccess = true;
        l.save();
        return l;
    }

    public static Finder<String, License> find = new Finder<>(License.class);

    public static List<License> all() {
        return find.all();
    }

    public Integer getMaxMessages() {
        return maxMessages;
    }

    public void setMaxMessages(Integer maxMessages) {
        this.maxMessages = maxMessages;
    }

    public LicenseStatus getStatus() {
        return status;
    }

    public void setStatus(LicenseStatus status) {
        this.status = status;
    }

    public Date getRenewedTime() {
        return renewedTime;
    }

    public void setRenewedTime(Date renewedTime) {
        this.renewedTime = renewedTime;
    }

    public Date getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(Date expiresTime) {
        this.expiresTime = expiresTime;
    }

    public Boolean getClient() {
        return isClient;
    }

    public void setClient(Boolean client) {
        isClient = client;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDashboardModuleAccess() {
        return dashboardModuleAccess;
    }

    public void setDashboardModuleAccess(Boolean dashboardModuleAccess) {
        this.dashboardModuleAccess = dashboardModuleAccess;
    }

    public Boolean getDevicesModuleAccess() {
        return devicesModuleAccess;
    }

    public void setDevicesModuleAccess(Boolean devicesModuleAccess) {
        this.devicesModuleAccess = devicesModuleAccess;
    }

    public Boolean getProjectsModuleAccess() {
        return projectsModuleAccess;
    }

    public void setProjectsModuleAccess(Boolean projectsModuleAccess) {
        this.projectsModuleAccess = projectsModuleAccess;
    }

    public Boolean getWorkflowModuleAccess() {
        return workflowModuleAccess;
    }

    public void setWorkflowModuleAccess(Boolean workflowModuleAccess) {
        this.workflowModuleAccess = workflowModuleAccess;
    }

    public Boolean getIssuesModuleAccess() {
        return issuesModuleAccess;
    }

    public void setIssuesModuleAccess(Boolean issuesModuleAccess) {
        this.issuesModuleAccess = issuesModuleAccess;
    }

    public Boolean getCollaboratorModuleAccess() {
        return collaboratorModuleAccess;
    }

    public void setCollaboratorModuleAccess(Boolean collaboratorModuleAccess) {
        this.collaboratorModuleAccess = collaboratorModuleAccess;
    }

    public Boolean getTeamsModuleAccess() {
        return teamsModuleAccess;
    }

    public void setTeamsModuleAccess(Boolean teamsModuleAccess) {
        this.teamsModuleAccess = teamsModuleAccess;
    }

    public Boolean getAnalyticsModuleAccess() {
        return analyticsModuleAccess;
    }

    public void setAnalyticsModuleAccess(Boolean analyticsModuleAccess) {
        this.analyticsModuleAccess = analyticsModuleAccess;
    }

    public Boolean getMembersModuleAccess() {
        return membersModuleAccess;
    }

    public void setMembersModuleAccess(Boolean membersModuleAccess) {
        this.membersModuleAccess = membersModuleAccess;
    }

    public Boolean getMessagesModuleAccess() {
        return messagesModuleAccess;
    }

    public void setMessagesModuleAccess(Boolean messagesModuleAccess) {
        this.messagesModuleAccess = messagesModuleAccess;
    }

    public Boolean getMarketplaceModuleAccess() {
        return marketplaceModuleAccess;
    }

    public void setMarketplaceModuleAccess(Boolean marketplaceModuleAccess) {
        this.marketplaceModuleAccess = marketplaceModuleAccess;
    }

    public Boolean getDeveloperModuleAccess() {
        return developerModuleAccess;
    }

    public void setDeveloperModuleAccess(Boolean developerModuleAccess) {
        this.developerModuleAccess = developerModuleAccess;
    }

}

