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

import io.ebean.Finder;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class Settings extends BaseTenantModel {
	
	/**
	 *  For All settings 1 means ON, 0 means OFF
	 */

	public Integer email_notification;
	public Integer daily_activity_email;
	public Integer in_app_notification;
	public Integer flow_notification;
	public Integer device_notification;

	public Settings(Organization o) {
		this.email_notification = 1;
		this.in_app_notification = 1;
		this.flow_notification = 1;
		this.device_notification = 1;
		this.daily_activity_email = 1;
		this.organization = o;
	}

	public static Finder<Long, Settings> find = new Finder<>(Settings.class);

	public static List<Settings> all() {

		return find.all();
	}
	
	public static Settings findById(String id) {
        return find.query().where().eq("id", id).findOne();
    }

	public Integer getEmail_notification() {
		return email_notification;
	}

	public void setEmail_notification(Integer email_notification) {
		this.email_notification = email_notification;
	}

	public Integer getDaily_activity_email() {
		return daily_activity_email;
	}

	public void setDaily_activity_email(Integer daily_activity_email) {
		this.daily_activity_email = daily_activity_email;
	}

	public Integer getIn_app_notification() {
		return in_app_notification;
	}

	public void setIn_app_notification(Integer in_app_notification) {
		this.in_app_notification = in_app_notification;
	}

	public Integer getFlow_notification() {
		return flow_notification;
	}

	public void setFlow_notification(Integer flow_notification) {
		this.flow_notification = flow_notification;
	}

	public Integer getDevice_notification() {
		return device_notification;
	}

	public void setDevice_notification(Integer device_notification) {
		this.device_notification = device_notification;
	}
}
