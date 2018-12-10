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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.Finder;

import javax.persistence.*;
import java.util.*;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Floor extends BaseTenantModel {

    /**
     * Floor Table
     */

    public String name;
    public Integer localId;
    @Transient
    @JsonProperty("local_name")
    public String localName;
    @OneToOne
    public TypeImage image;
    @Transient
    @JsonProperty("access_url")
    public String accessUrl;
    @Transient
    @JsonIgnore
    private String route = "/index.html#/index/devices/floors/";
    @JsonIgnore
    @ManyToMany(cascade=CascadeType.PERSIST)
    public Set<Device> sensors;
    @ManyToMany(cascade=CascadeType.PERSIST)
    public Set<FloorTag> tags;

    public Floor() {

    }

    public Floor(String name, Organization organization, User by) {
        this.name = name;
        this.organization = organization;
        this.by = by;
        List<Floor> projects = Floor.all(this.organization);
        if(projects.size() > 0){
            this.localId = projects.get(0).localId + 1;
        } else {
            this.localId = 1;
        }
    }

    public static Finder<Long, Floor> find = new Finder<>(Floor.class);

    public static List<Floor> all(Map parameterMap) {
        if (parameterMap != null)
            return find.query().where().allEq(parameterMap).findList();
        else
            return find.all();
    }

    public static List<Floor> all() {
        return find.all();
    }

    public static List<Floor> all(Organization organization) {
        return find.query().where()
                    .eq("organization_id", organization.getId())
                    .orderBy("when_created desc").findList();
    }

    public static List<Floor> all(String text, Organization organization) {
        if(text != null) {
            return find.query().where()
                    .eq("organization_id", organization.getId())
                    .like("name", "%" + text + "%")
                    .orderBy("when_created desc").findList();
        } else {
            return new ArrayList<>();
        }
    }

    public static Floor findById(String projectId) {
        return find.query().where().eq("id", projectId).findOne();
    }

    public static List<Floor> findAllByOrganization(Organization organization) {
        return find.query().where().eq("organization_id", organization.getId()).findList();
    }

    public Integer getLocalId() {
        return localId;
    }

    public void setLocalId(Integer localId) {
       this.localId = localId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizationId() {
        return this.organization.id;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return this.organization.getName();
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getLocalName() {
        return "FLOOR-" + this.localId;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public TypeImage getImage() {
        return image;
    }

    public void setImage(TypeImage image) {
        this.image = image;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public Set<Device> getSensors() {
        return sensors;
    }

    public void setSensors(Set<Device> sensors) {
        this.sensors = sensors;
    }

    public Set<FloorTag> getTags() {
        return tags;
    }

    public void setTags(Set<FloorTag> tags) {
        this.tags = tags;
    }
}

