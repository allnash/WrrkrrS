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
import java.util.List;
import java.util.Map;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Place extends BaseTenantModel {
    /**
     * Place Table
     */

    public String name;
    public String address;
    public String city;
    public String state;
    public String telephone_number;
    public String zip;
    @JsonIgnore
    @Column(name = "place_type_id")
    @ManyToOne
    public PlaceType placeType;

    public String lat;
    public String lon;

    @JsonProperty("cartesian_x")
    @Column(nullable = false)
    public Integer cartesianX;

    @JsonProperty("cartesian_y")
    @Column(nullable = false)
    public Integer cartesianY;

    @ManyToOne
    public Floor floor;

    @ManyToOne
    public User user;

    public String notes;

    public boolean verified;

    public Place() {
    }

    public Place(String name, PlaceType placeType, Organization o) {
        this.name = name;
        this.verified = true;
        this.organization = o;
        this.placeType = placeType;
        this.cartesianX = 0;
        this.cartesianY = 0;
    }

    public static Finder<Long, Place> find = new Finder<>(Place.class);

    public static List<Place> all(Map parameterMap) {
        if (parameterMap != null)
            return find.query().where().allEq(parameterMap).findList();
        else
            return null;
    }

    public static Place findById(String ownerId) {
        return find.query().where().eq("id", ownerId).findOne();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTelephone_number() {
        return telephone_number;
    }

    public void setTelephone_number(String telephone_number) {
        this.telephone_number = telephone_number;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Integer getCartesianX() {
        return cartesianX;
    }

    public void setCartesianX(Integer cartesianX) {
        this.cartesianX = cartesianX;
    }

    public Integer getCartesianY() {
        return cartesianY;
    }

    public void setCartesianY(Integer cartesianY) {
        this.cartesianY = cartesianY;
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

