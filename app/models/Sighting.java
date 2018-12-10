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
import utils.Utils;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sighting extends BaseTenantModel {

    /**
     * Reader Sightings Table
     */
    @JsonIgnore
    @ManyToOne
    public Device readerDevice;

    @JsonProperty("reader_device_id")
    @Transient
    public String readerDeviceId;

    @JsonIgnore
    @ManyToOne
    public Device sightedDevice;

    @JsonProperty("sighted_device_id")
    @Transient
    public String sightedDeviceId;

    @ManyToOne
    public User sightedUser;

    @JsonProperty("sighted_user_id")
    @Transient
    public String sightedUserId;

    @JsonProperty("rssi")
    public String RSSI;

    @JsonProperty("k_rssi")
    public String kRssi;

    public String distance;

    @JsonProperty("k_distance")
    public String kDistance;

    public String temperature;

    @JsonProperty("battery_level")
    public String batteryLevel;

    @JsonProperty("when_seen")
    public Timestamp whenSeen;

    @JsonIgnore
    @ManyToOne
    public Floor floor;

    @JsonIgnore
    @ManyToOne
    public Visit visit;

    public boolean processed;

    public Sighting() {

    }

    public static Finder<String, Sighting> find = new Finder<>(Sighting.class);

    public static List<Sighting> dailySightings(User myUser) {
        LocalDateTime now = LocalDateTime.now(); // current date and time
        LocalDateTime midnight = now.toLocalDate().atStartOfDay();
        Timestamp today = Utils.getTimestamp(midnight);
        List<User> users = User.findAllByOrganization(myUser.getOrganization());
        Map<String, Object> parameterMap = new HashMap<>();
        List<Sighting> sightings = new ArrayList<>();
        for (User user : users) {
            parameterMap.put("user_id", user.getId());
            sightings.addAll(find.query().where().gt("when_created", today)
                    .allEq(parameterMap).findList());
        }
        return sightings;
    }

    public static List<Sighting> findAll() {
        return find.all();
    }


    public static List<Sighting> findProcessedSightingsByUser(User user) {
        return find.query().where().eq("sighted_user_id", user.getId())
                .eq("processed", true).findList();
    }

    public static List<Sighting> findUnprocessedBySightedDevice(Device sightedDevice) {
        return find.query().where().eq("sighted_device_id", sightedDevice.getId())
                .eq("processed", false).findList();
    }

    public static Sighting findById(String id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static Finder<String, Sighting> getFind() {
        return find;
    }

    public static void setFind(Finder<String, Sighting> find) {
        Sighting.find = find;
    }

    public static List<Sighting> findProcessedByUserAndDeviceWithoutVisit(Device device, Timestamp startTime, Timestamp endTime) {
        return find.query().where()
                .eq("sighted_device_id", device.getId())
                .eq("processed", true)
                .ge("when_seen", startTime.toString())
                .lt("when_seen", endTime.toString())
                .isNull("visit_id")
                .orderBy("when_seen asc").findList();
    }

    public static List<Sighting> findDistinctDevices(Timestamp whenSeenStart, Timestamp whenSeenEnd) {
        return find.query().where()
                .ge("when_seen", whenSeenStart.toString())
                .lt("when_seen", whenSeenEnd.toString())
                .setDistinct(true).select("sightedDevice").findList();
    }

    public Device getReaderDevice() {
        return readerDevice;
    }

    public void setReaderDevice(Device readerDevice) {
        this.readerDevice = readerDevice;
    }

    public Device getSightedDevice() {
        return sightedDevice;
    }

    public void setSightedDevice(Device sightedDevice) {
        this.sightedDevice = sightedDevice;
    }

    public User getSightedUser() {
        return sightedUser;
    }

    public void setSightedUser(User sightedUser) {
        this.sightedUser = sightedUser;
    }

    public Timestamp getWhenSeen() {
        return whenSeen;
    }

    public void setWhenSeen(Timestamp whenSeen) {
        this.whenSeen = whenSeen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRSSI() {
        return RSSI;
    }

    public void setRSSI(String RSSI) {
        this.RSSI = RSSI;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getReaderDeviceId() {
        if (this.readerDevice != null) {
            return this.readerDevice.id;
        } else {
            return null;
        }
    }

    public String getSightedDeviceId() {
        if (this.sightedDevice != null) {
            return sightedDevice.id;
        } else {
            return null;
        }
    }

    public String getSightedUserId() {
        if (this.sightedUser != null) {
            return sightedUser.id;
        } else {
            return null;
        }
    }

    public String getkRssi() {
        return kRssi;
    }

    public void setkRssi(String kRssi) {
        this.kRssi = kRssi;
    }

    public String getkDistance() {
        return kDistance;
    }

    public void setkDistance(String kDistance) {
        this.kDistance = kDistance;
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public void setReaderDeviceId(String readerDeviceId) {
        this.readerDeviceId = readerDeviceId;
    }

    public void setSightedDeviceId(String sightedDeviceId) {
        this.sightedDeviceId = sightedDeviceId;
    }

    public void setSightedUserId(String sightedUserId) {
        this.sightedUserId = sightedUserId;
    }
}
