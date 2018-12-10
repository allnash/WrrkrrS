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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.UpdatedTimestamp;
import play.Logger;
import play.data.validation.Constraints;
import utils.Utils;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Entity
public class OrganizationKey extends Model
{

	/**
	 *  Organization Table
	 */

    @Id
    public String id;

    @Version
    Long version;

    @CreatedTimestamp
    @JsonProperty("when_created")
    Timestamp whenCreated;

    @UpdatedTimestamp
    @JsonProperty("when_updated")
    Timestamp whenUpdated;

    @JsonIgnore
    @OneToOne
    public Organization organization;

    @Column(length = 255, nullable = false)
    @Constraints.Required
    public String aesKey;

    @Column(nullable = false, columnDefinition="tinyint(1) default 0")
    public Boolean deleted;

    public OrganizationKey(){
        id = UUID.randomUUID().toString();
        deleted = false;
    }

	public static Finder<Long, OrganizationKey> find = new Finder<>(OrganizationKey.class);

	public static List<OrganizationKey> all() {
		return find.all();
	}

    public static OrganizationKey findById(String id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static OrganizationKey findByOrganizationId(String id) {
        return find.query().where().eq("organization_id", id).findOne();
    }

    public static OrganizationKey findByName(String name) {
        return find.query().where().eq("name", name).findOne();
    }

    public void save(){
        super.save();
        Logger.info(this.getClass().getCanonicalName() + " -  saved at " + Calendar.getInstance().getTime().toString());
    }

    public void update(){
        super.update();
        Logger.info(this.getClass().getCanonicalName() + " -  updated at " + Calendar.getInstance().getTime().toString());
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Timestamp getWhenCreated() {
        return whenCreated;
    }

    public void setWhenCreated(Timestamp whenCreated) {
        this.whenCreated = whenCreated;
    }

    public Timestamp getWhenUpdated() {
        return whenUpdated;
    }

    public void setWhenUpdated(Timestamp whenUpdated) {
        this.whenUpdated = whenUpdated;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public static void createKeyForOrganization(Organization o) {
	    OrganizationKey k = new OrganizationKey();
	    k.setOrganization(o);
	    k.setAesKey(Utils.nextSessionId());
	    k.save();
    }
}


