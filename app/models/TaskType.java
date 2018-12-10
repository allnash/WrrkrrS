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
public class TaskType extends BaseTypeModel {

    public static ConcurrentHashMap<String, TaskType> types = new ConcurrentHashMap<String, TaskType>();

    public TaskType() {

    }

    public TaskType(String name, String description, String labelClass, Organization organization) {
        this.name = name.toUpperCase();
        this.description = description;
        this.visibility = Visibility.PRIVATE;
        this.organization = organization;
        this.labelClass = labelClass;
        this.save();
    }


    public static TaskType of(String string) {
        return types.getOrDefault(string, null);
    }


    public static TaskType add(String name, String description, String labelClass, Organization organization) {
        TaskType d = new TaskType(name, description, labelClass, organization);
        d.save();
        return d;
    }

    public static boolean load() {
        for (TaskType d : find.all()) {
            types.put(d.name, d);
        }
        Logger.info("Reloading Task Types");
        if (types.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    public static Finder<Long, TaskType> find = new Finder<>(TaskType.class);

    public static List<TaskType> all() {
        return find.all();
    }

    public static TaskType findById(Long id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static TaskType findByName(String name, Organization organization) {
        return find.query().where()
                .eq("name", name)
                .eq("organization_id", organization.getId()).findOne();
    }

    public static List<TaskType> findByOrganization(Organization organization) {
        return find.query().where().eq("organization_id", organization.getId()).orderBy("when_created asc").findList();
    }

    public static void bootstrapTaskTypesForOrganization(Organization organization) {
        /*
         *  Add 5 Project types for each org that gets created.
         * */
        TaskType.add("Basic", "Basic", "label-primary", organization);
        TaskType.add("Advanced", "Advanced", "label-primary", organization);
        TaskType.add("Repeating", "Repeating", "label-primary", organization);
        TaskType.add("Custom", "Custom", "label-primary", organization);
        TaskType.add("Informational", "Informational", "label-primary", organization);
        TaskType.load();

    }
}
