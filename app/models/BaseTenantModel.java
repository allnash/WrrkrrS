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

/*
 * Created by ngadre on 10/28/15.
 */


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.annotation.History;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Base domain object with Id, version, whenCreated and whenUpdated.
 * <p>
 * <p>
 * Extending Model to enable the 'active record' style.
 * <p>
 * <p>
 * whenCreated and whenUpdated are generally useful for maintaining external search services (like
 * elasticsearch) and audit.
 */
@MappedSuperclass

public abstract class BaseTenantModel extends BaseModel {

    @JsonIgnore
    @ManyToOne
    public Organization organization;
    @Transient
    @JsonProperty("organization_id")
    public String organizationId;
    @Transient
    @JsonProperty("organization_name")
    public String organizationName;
    @Transient
    @JsonProperty("organization_email_domain")
    public String organizationEmailDomain;

    @JsonIgnore
    @ManyToOne
    public User by;

    @Transient
    public String by_id;

    @Transient
    public String by_full_name;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getOrganizationId() {
        if (this.organization != null) {
            return this.organization.getId();
        } else {
            return null;
        }
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        if (this.organization != null) {
            return this.organization.getName();
        } else {
            return null;
        }
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationEmailDomain() {
        if (this.organization != null) {
            return this.organization.getEmailDomain();
        } else {
            return null;
        }
    }

    public void setOrganizationEmailDomain(String organizationEmailDomain) {
        this.organizationEmailDomain = organizationEmailDomain;
    }

    public User getBy() {
        return by;
    }

    public void setBy(User by) {
        this.by = by;
    }

    public String getBy_id() {
        if (this.getBy() != null) {
            return this.getBy().getId();
        } else {
            return "";
        }
    }

    public String getBy_full_name() {
        if (this.getBy() != null) {
            return this.getBy().getFullName();
        } else {
            return "";
        }
    }
}
