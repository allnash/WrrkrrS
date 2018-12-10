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


/**
 * Created by ngadre on 10/28/15.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.UpdatedTimestamp;
import play.Logger;
import play.libs.Json;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

/**
 * Base domain object with Id, version, whenCreated and whenUpdated.
 *
 * <p>
 * Extending Model to enable the 'active record' style.
 *
 * <p>
 * whenCreated and whenUpdated are generally useful for maintaining external search services (like
 * elasticsearch) and audit.
 */
@MappedSuperclass
public abstract class BaseModel extends Model {
    @Id
    @Column(length = 36)
    public String id;

    @Version
    Long version;

    @Transient
    History currentHistory;

    @CreatedTimestamp
    @JsonProperty("when_created")
    Timestamp whenCreated;

    @UpdatedTimestamp
    @JsonProperty("when_updated")
    Timestamp whenUpdated;

    @Column(nullable = false, columnDefinition="tinyint(1) default 0")
    public Boolean deleted;

    public BaseModel(){
        id = UUID.randomUUID().toString();
        deleted = false;
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

    @PreUpdate
    void onPreUpdate() {
        currentHistory =  new History(this.id, this.version, this.getClass());
        JsonNode jsonData =   Json.toJson(this);
        currentHistory.setData_before(jsonData.toString());
    }

    @PostUpdate
    void onPostUpdate() {
        JsonNode jsonData =   Json.toJson(this);
        currentHistory.setData_after(jsonData.toString());
        currentHistory.save();
    }

    @PrePersist
    public void prePersist() {
        currentHistory =  new History(this.id, this.version, this.getClass());
        JsonNode jsonData =   Json.toJson(this);
        currentHistory.setData_before(jsonData.toString());
    }


    @PostPersist
    public void postPersist() {
        JsonNode jsonData =   Json.toJson(this);
        currentHistory.setData_after(jsonData.toString());
        currentHistory.save();
    }

    public void save(){
        super.save();
        Logger.info(this.getClass().getCanonicalName() + "(id:" + this.id +")" + " -  saved at " + Calendar.getInstance().getTime().toString());
    }

    public void update(){
        super.update();
        Logger.info(this.getClass().getCanonicalName() + "(id:" + this.id +")" + " -  updated at " + Calendar.getInstance().getTime().toString());
    }

    public void setId(String id) {
        this.id = id;
    }
}
