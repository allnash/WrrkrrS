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
import play.Logger;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"organization_id", "name"})
)
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskStatus extends BaseStatusModel {

    public static ConcurrentHashMap<String, TaskStatus> types = new ConcurrentHashMap<String, TaskStatus>();

    public TaskStatus() {

    }

    public TaskStatus(String name, String description, String labelClass, Organization organization) {
        this.name = name.toUpperCase();
        this.description = description;
        this.visibility = Visibility.PRIVATE;
        this.organization = organization;
        this.labelClass = labelClass;
        this.save();
    }


    public static TaskStatus of(String string) {
        return types.getOrDefault(string, null);
    }


    public static TaskStatus add(String name, String description, String labelClass, Organization organization) {
        TaskStatus d = new TaskStatus(name, description, labelClass, organization);
        d.save();
        return d;
    }

    public static boolean load() {
        for (TaskStatus d : find.all()) {
            types.put(d.name, d);
        }
        Logger.info("Reloading Task Statuses");
        if (types.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    public static Finder<Long, TaskStatus> find = new Finder<>(TaskStatus.class);

    public static List<TaskStatus> all() {
        return find.all();
    }

    public static List<TaskStatus> findByOrganization(Organization organization) {
        return find.query().where().eq("organization_id", organization.getId()).orderBy("when_created asc").findList();
    }

    public static TaskStatus findById(String id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static TaskStatus findByName(String name, Organization organization) {
        return find.query().where()
                .eq("name", name)
                .eq("organization_id", organization.getId()).findOne();
    }

    public static void bootstrapTaskStatusesForOrganization(Organization organization) {
        /*
         *  Add 5 Task types for each org that gets created.
         * */
        TaskStatus.add("Todo", "To Do", "label-primary", organization);
        TaskStatus.add("Open", "Open", "label-primary", organization);
        TaskStatus.add("In_Progress", "In Progress", "label-warning", organization);
        TaskStatus.add("In_Review", "In Review", "label-info", organization);
        TaskStatus.add("Closed", "Closed", "label-success", organization);
        TaskStatus.add("Archived", "Archived", "label-default", organization);
        TaskStatus.load();

    }
}
