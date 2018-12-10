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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.ebean.Finder;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Organization extends BaseModel
{
  
	/**
	 *  Organization Table
	 */
    @JsonProperty("external_id")
    public String externalId;

    @Column(length = 255, nullable = false)
    @Constraints.Required
    public String name;

    @Column(length = 64, unique = true)
    @JsonProperty("email_domain")
    public String emailDomain;

    @Column(length = 64, unique = true)
    @JsonProperty("workspace_name")
    public String workspaceName;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @Column(name = "place_id")
    public Place  place;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "collaborator_organizations",
            joinColumns = @JoinColumn(name = "organization_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "collaborator_organization_id", referencedColumnName = "id"))
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
    public List<Organization> collaborators;

    @JsonProperty("enabled")
    public Boolean enabled; // YES MEANS 1 | NO MEANS 0

    @JsonProperty("approved")
    public Boolean approved; // YES MEANS 1 | NO MEANS 0

    @JsonIgnore
    @JsonProperty("self_service_signup")
    public Boolean selfServiceSignup; // YES MEANS 1 | NO MEANS 0

    @JsonIgnore
    @JsonProperty("slack_hook")
    @Column(length = 511)
    public String slackHook;

    /*
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "organization_products",
            joinColumns = @JoinColumn(name = "organization_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
    public List<Product> products;
    */

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    public License license;

    @Transient
    @JsonProperty("license_id")
    public String licenseId;

    @Transient
    @JsonProperty("license_name")
    public String licenseName;

    @JsonIgnore
    @ManyToOne
    public User by;

    @JsonIgnore
    @ManyToOne
    public User approvedBy;

    /*
    @JsonIgnore
    @OneToOne(mappedBy = "organization")
    public OrganizationKey key;
    */

    public Organization() {

    }

	public Organization(String name, String workspaceName) {
		this.externalId = UUID.randomUUID().toString();
		this.name = name;
        this.workspaceName = workspaceName.toLowerCase();
        this.enabled = true;
        this.approved = true;
        this.slackHook =  "";
        this.selfServiceSignup = true;
        this.license = License.basicLicense(this);
	}

	public static Finder<Long, Organization> find = new Finder<>(Organization.class);

	public static List<Organization> all() {
		return find.all();
	}

    public static Organization findById(String id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static Organization findByName(String name) {
        return find.query().where().eq("name", name).findOne();
    }

    public static Organization findByEmailDomain(String emailDomain) {
        return find.query().where().eq("email_domain", emailDomain).findOne();
    }

    public static Organization findByWorkspaceName(String workspaceName) {
        return find.query().where().eq("workspace_name", workspaceName.toLowerCase()).findOne();
    }

    public static Organization createIfNotPresentForEmail(String workspaceName){

        // Check and create org for domain name
        Organization o = findByWorkspaceName(workspaceName);
        if((o != null)){
            return o;
        }
        o =  new Organization(workspaceName, workspaceName);
        o.save();
        return o;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailDomain() {
        return emailDomain;
    }

    public void setEmailDomain(String emailDomain) {
        this.emailDomain = emailDomain;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public List<Organization> getCollaborators() {
        if (collaborators != null) {
            return collaborators;
        } else {
            return new ArrayList<>();
        }
    }

    public void setCollaborators(List<Organization> collaborators) {
        this.collaborators = collaborators;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getSlackHook() {
        return slackHook;
    }

    public void setSlackHook(String slackHook) {
        this.slackHook = slackHook;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean getSelfServiceSignup() {
        return selfServiceSignup;
    }

    public void setSelfServiceSignup(Boolean selfServiceSignup) {
        this.selfServiceSignup = selfServiceSignup;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public User getBy() {
        return by;
    }

    public void setBy(User by) {
        this.by = by;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getLicenseId() {
        if(this.license != null){
            return this.license.id;
        } else {
            return null;
        }
    }

    public String getLicenseName() {
        if(this.license != null){
            return this.license.name;
        } else {
            return null;
        }    }
}


