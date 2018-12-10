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
import io.ebean.Finder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.List;
import java.util.Map;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Activity extends BaseModel
{
	/**
	 *  Activity Table
	 */

	public ActivityType type;
	@ManyToOne
    public User by;

    public Activity() { }

	public Activity(ActivityType type, User by) {
		this.type = type;
		this.by = by;
	}
	
	public static Finder<Long, Activity> find = new Finder<>(Activity.class);

    public static List<Activity> all(Map parameterMap) {
        if(parameterMap != null)
            return find.query().where().allEq(parameterMap).findList();
        else
            return null;
    }
	
	public static Activity findById(String ownerId) {
        return find.query().where().eq("id", ownerId).findOne();
    }

}

