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
import utils.Utils;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Entity
public class Session extends BaseTenantModel {

    /**
     * Sessions Table
     */

    public String email;
    @JsonProperty("ip_address")
    public String ipAddress;
    @JsonProperty("session_id")
    public String sessionId;
    @JsonProperty("is_two_factored")
    public Boolean isTwoFactored;
    @Transient
    @JsonProperty("next_page")
    public String nextPage;
    @JsonProperty("app")
    public String app;
    @JsonProperty("expires_at")
    public Timestamp expiresAt;

    public Session() {
        this.sessionId = UUID.randomUUID().toString();
    }

    public Session(String email, String ipAddress, Organization organization) {
        this.sessionId = UUID.randomUUID().toString();
        this.email = email;
        this.ipAddress = ipAddress;
        this.isTwoFactored = false;
        this.app = "engage";
        this.organization = organization;
        Calendar c = Calendar.getInstance();
        c.setTime(Calendar.getInstance().getTime());
        c.add(Calendar.HOUR, 12);
        this.expiresAt = new Timestamp(c.getTimeInMillis());
    }

    public static Finder<Long, Session> find = new Finder<>(Session.class);

    public static List<Session> all() {
        return find.all();
    }

    public static Session findById(String id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static Session findBySessionId(String sessionId) {
        return find.query().where().eq("session_id", sessionId).findOne();
    }

    public static Session findByEmailSessionIdIpAddress(String email, String sessionId, String ipAddress) {
        return find.query().where()
                .eq("session_id", sessionId)
                .eq("email", email)
                .eq("ip_address", ipAddress).findOne();
    }


    public static List<Session> findAllUnexpiredByEmail(String email) {
        return find.query().where().eq("email", email).findList();
    }

    public boolean isExpired() {
        Calendar c = Calendar.getInstance();
        c.setTime(Calendar.getInstance().getTime());
        Timestamp currentTimestamp = new Timestamp(c.getTimeInMillis());
        return this.expiresAt.getTime() < currentTimestamp.getTime();
    }

    public void invalidate() {
        this.expiresAt = Utils.getCurrentTimestamp();
        this.setApp("");
        save();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getTwoFactored() {
        return isTwoFactored;
    }

    public void setTwoFactored(Boolean twoFactored) {
        isTwoFactored = twoFactored;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getNextPage() {
        if (isTwoFactored) {
            return User.findByEmail(this.email, this.organization).getRole().getDefaultRoute();
        } else if (!User.findByEmail(this.email, this.organization).getTOTPConfigured()) {
            return "setuptwofactor";
        } else {
            return "verifytwofactor";
        }
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }
}
