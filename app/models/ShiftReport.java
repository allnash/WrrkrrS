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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShiftReport extends BaseTenantModel {

    /**
     * Shift Table
     */
    @ManyToOne
    @JsonIgnore
    public User user;
    @JsonProperty("for_date")
    public Timestamp forDate;
    @JsonProperty("clocked_hours")
    public int clockedHours;
    @JsonProperty("clocked_minutes")
    public int clockedMinutes;

    public static Finder<String, models.ShiftReport> find = new Finder<>(models.ShiftReport.class);


    public ShiftReport(User user, Timestamp forDate, int hours, int mins) {
        this.user = user;
        this.organization = user.getOrganization();
        this.forDate = forDate;
        this.clockedHours = hours;
        this.clockedMinutes = mins;
    }

    public static List<models.ShiftReport> monthlyShiftEvents(Organization organization) {
        LocalDateTime now = LocalDateTime.now(); // current date and time
        LocalDateTime firstDay = now.minusDays(now.getDayOfMonth() - 1);
        LocalDateTime midnight = firstDay.toLocalDate().atStartOfDay();
        Timestamp today = Utils.getTimestamp(midnight);
        List<User> users = User.findAllByOrganization(organization);
        Map<String, Object> parameterMap = new HashMap<>();
        List<models.ShiftReport> shifts = new ArrayList<>();
        for (User user : users) {
            parameterMap.put("user_id", user.getId());
            shifts.addAll(find.query().where().gt("when_created", today)
                    .allEq(parameterMap).findList());
        }
        return shifts;
    }

    public static List<models.ShiftReport> findAll(Organization organization) {
        return find.query().where().eq("organization_id", organization.getId()).findList();
    }


    public static List<models.ShiftReport> findByUser(User user) {
        return find.query().where().eq("user_id", user.getId()).findList();
    }

    public static ShiftReport findByUserForDate(User user, Timestamp forDate) {
        return find.query().where().eq("user_id", user.getId())
                .eq("for_date", forDate.toString()).findOne();
    }

    public static List<ShiftReport> findInBetweenDates(User user, Timestamp startDate, Timestamp endDate) {
        return find.query().where().eq("user_id", user.getId())
                .ge("for_date", startDate.toString())
                .lt("for_date", endDate.toString()).findList();
    }

    public static List<ShiftReport> findClockedHours(User user) {
        return find.query().select("clockedHours").where().eq("user_id", user.getId()).findList();
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

    public Timestamp getForDate() {
        return forDate;
    }

    public void setForDate(Timestamp forDate) {
        this.forDate = forDate;
    }

    public int getClockedHours() {
        return clockedHours;
    }

    public void setClockedHours(int clockedHours) {
        this.clockedHours = clockedHours;
    }

    public int getClockedMinutes() {
        return clockedMinutes;
    }

    public void setClockedMinutes(int clockedMinutes) {
        this.clockedMinutes = clockedMinutes;
    }
}
