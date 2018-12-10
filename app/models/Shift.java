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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Shift extends BaseTenantModel {

    public enum ShiftEvent {
        START, END
    }

    /**
     * Shift Table
     */
    @ManyToOne
    @JsonIgnore
    public Device device;
    @ManyToOne
    @JsonIgnore
    public User user;
    public String lat;
    public String lon;
    @JsonProperty("event")
    public ShiftEvent event;
    @Column(nullable = false)
    @JsonProperty("when_received")
    Timestamp whenReceived;
    public boolean processed;

    public static Finder<String, Shift> find = new Finder<>(Shift.class);


    public Shift(User user, Shift.ShiftEvent event, Timestamp whenReceived) {
        this.user = user;
        this.organization = user.getOrganization();
        this.event = event;
        if(whenReceived == null){
            this.whenReceived = Utils.getCurrentTimestamp();
        } else  {
            this.whenReceived = whenReceived;
        }
    }

    public static List<Shift> monthlyShiftEvents(Organization organization) {
        LocalDateTime now = LocalDateTime.now(); // current date and time
        LocalDateTime firstDay = now.minusDays(now.getDayOfMonth() - 1);
        LocalDateTime midnight = firstDay.toLocalDate().atStartOfDay();
        Timestamp today = Utils.getTimestamp(midnight);
        List<User> users = User.findAllByOrganization(organization);
        Map<String, Object> parameterMap = new HashMap<>();
        List<Shift> shifts = new ArrayList<>();
        for (User user : users) {
            parameterMap.put("user_id", user.getId());
            shifts.addAll(find.query().where().gt("when_created", today)
                    .allEq(parameterMap).findList());
        }
        return shifts;
    }

    public static List<Shift> findAll() {
        return find.all();
    }


    public static List<Shift> findByUser(User user) {
        return find.query().where().eq("user_id", user.getId()).findList();
    }

    public static List<Shift> findUnprocessedByUser(User user, Timestamp startTime, Timestamp endTime) {
        return find.query().where().eq("user_id", user.getId())
                .eq("processed", false)
                .ge("when_received", startTime.toString())
                .lt("when_received", endTime.toString())
                .orderBy("when_received asc").findList();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ShiftEvent getEvent() {
        return event;
    }

    public void setEvent(ShiftEvent event) {
        this.event = event;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public Timestamp getWhenReceived() {
        return whenReceived;
    }

    public void setWhenReceived(Timestamp whenReceived) {
        this.whenReceived = whenReceived;
    }


}
