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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.CaseFormat;
import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.UpdatedTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class History extends Model
{
	/**
	 *  History Table
	 */

	@Id
	public String  id;
    public String  entity_id;
    public Long  entity_version;
    public String  entity_class;
    @Column(columnDefinition = "LONGTEXT")
    public String  data_before;
    @Column(columnDefinition = "LONGTEXT")
    public String  data_after;

    @CreatedTimestamp
    @JsonProperty("when_created")
    Timestamp whenCreated;

    @UpdatedTimestamp
    @JsonProperty("when_updated")
    Timestamp whenUpdated;

    public History() { }

	public History(String entity_id, Long entity_version, Class c) {
		this.id = UUID.randomUUID().toString();
		this.entity_id = entity_id;
		this.entity_class = jsonKeyNameForClass(c);
	}
	
	public static Finder<Long, History> find = new Finder<>(History.class);

    public static List<History> all(Map parameterMap) {
        if(parameterMap != null) {
            return find.query().where().allEq(parameterMap).findList();
        } else {
            return null;
        }
    }
	
	public static History findById(String ownerId) {
        return find.query().where().eq("id", ownerId).findOne();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData_before() {
        return data_before;
    }

    public void setData_before(String data_before) {
        this.data_before = data_before;
    }

    public String getData_after() {
        return data_after;
    }

    public void setData_after(String data_after) {
        this.data_after = data_after;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }

    public Long getEntity_version() {
        return entity_version;
    }

    public void setEntity_version(Long entity_version) {
        this.entity_version = entity_version;
    }

    public String getEntity_class() {
        return entity_class;
    }

    public void setEntity_class(String entity_class) {
        this.entity_class = entity_class;
    }

    public static String jsonKeyNameForClass(Class c){
        String className = c.getSimpleName();
        className = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
        return className;
    }
}

