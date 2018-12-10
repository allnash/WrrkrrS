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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.List;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Visit extends BaseTenantModel {

    /**
     * Visit Table
     */
    public enum VisitType {
        NOTYPE, ZONE, PLACE, FLOOR
    }

    @JsonIgnore
    @ManyToOne
    public Device endpointDevice;

    @JsonIgnore
    @ManyToOne
    public Device userDevice;

    @JsonIgnore
    @ManyToOne
    @Column(nullable = false)
    public User user;

    @JsonProperty("zone_id")
    public String zoneId;
    @ManyToOne
    public Place place;
    @ManyToOne
    public Floor floor;

    @ManyToOne
    @JsonProperty("visit_type")
    @Column(nullable = false)
    public VisitType visitType;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    @JsonProperty("when_started")
    public Timestamp whenStarted;
    @JsonProperty("when_ended")
    public Timestamp whenEnded;

    public Visit(String name, User user, Visit.VisitType visitType, Timestamp whenStarted) {
        this.user = user;
        this.organization = user.getOrganization();
        this.name = name;
        this.visitType = visitType;
        this.whenStarted = whenStarted;
    }

    public static Finder<String, Visit> find = new Finder<>(Visit.class);


    public static List<Visit> all(Organization organization) {
        return find.query().where().
                eq("organization_id", organization.getId()).findList();
    }


    public static List<Visit> findByUser(User user) {
        return find.query().where().
                eq("organization_id", user.getOrganizationId())
                .eq("user_id", user.getId()).findList();
    }

    public static Visit findById(String id) {
        return find.query().where().eq("id", id).findOne();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Device getEndpointDevice() {
        return endpointDevice;
    }

    public void setEndpointDevice(Device endpointDevice) {
        this.endpointDevice = endpointDevice;
    }

    public Device getUserDevice() {
        return userDevice;
    }

    public void setUserDevice(Device userDevice) {
        this.userDevice = userDevice;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public VisitType getVisitType() {
        return visitType;
    }

    public void setVisitType(VisitType visitType) {
        this.visitType = visitType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getWhenStarted() {
        return whenStarted;
    }

    public void setWhenStarted(Timestamp whenStarted) {
        this.whenStarted = whenStarted;
    }

    public Timestamp getWhenEnded() {
        return whenEnded;
    }

    public void setWhenEnded(Timestamp whenEnded) {
        this.whenEnded = whenEnded;
    }

}