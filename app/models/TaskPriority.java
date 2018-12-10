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
public class TaskPriority extends BasePriority {

    public static ConcurrentHashMap<String, TaskPriority> types = new ConcurrentHashMap<String, TaskPriority>();

    public TaskPriority() {

    }

    public TaskPriority(String name, String description, String labelClass, String iClass, String elementClass, Organization organization) {
        this.name = name.toUpperCase();
        this.description = description;
        this.labelClass = labelClass;
        this.iClass = iClass;
        this.elementClass = elementClass;
        this.organization = organization;
        this.save();
    }


    public static TaskPriority of(String string) {
        return types.getOrDefault(string, null);
    }


    public static TaskPriority add(String name, String description, String labelClass, String iClass, String elementClass,
                                   Organization organization) {
        TaskPriority d = new TaskPriority(name, description, labelClass, iClass, elementClass, organization);
        d.save();
        return d;
    }

    public static boolean load() {
        for (TaskPriority d : find.all()) {
            types.put(d.name, d);
        }
        Logger.info("Reloading Task Priorities");
        if (types.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    public static Finder<Long, TaskPriority> find = new Finder<>(TaskPriority.class);

    public static List<TaskPriority> all() {
        return find.all();
    }

    public static TaskPriority findById(Long id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static TaskPriority findByName(String name, Organization organization) {
        return find.query().where()
                .eq("name", name)
                .eq("organization_id", organization.getId()).findOne();
    }

    public static void bootstrapTaskPrioritiesForOrganization(Organization organization) {
        /*
         *  Add 5 Task Priority for each org that gets created.
         * */
        TaskPriority.add("None", "None Priority", "label-default", "text-default", "default", organization);
        TaskPriority.add("Low", "Low Priority", "label-info", "text-info", "info", organization);
        TaskPriority.add("Medium", "Medium Priority", "label-warning", "text-warning", "warning", organization);
        TaskPriority.add("High", "High Priority", "label-warning", "text-warning", "warning", organization);
        TaskPriority.add("Critical", "Critical Priority", "label-danger", "text-danger", "danger", organization);
        TaskPriority.load();

    }


    public static List<TaskPriority> findByOrganization(Organization organization) {
        return find.query().where()
                .eq("organization_id", organization.getId()).findList();
    }
}
