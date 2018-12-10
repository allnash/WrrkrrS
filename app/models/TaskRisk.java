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
public class TaskRisk extends BasePriority {

    public static ConcurrentHashMap<String, TaskRisk> types = new ConcurrentHashMap<String, TaskRisk>();

    public TaskRisk() {

    }

    public TaskRisk(String name, String description, String labelClass, String iClass, Organization organization) {
        this.name = name.toUpperCase();
        this.description = description;
        this.availablePublicly = false;
        this.organization = organization;
        this.labelClass = labelClass;
        this.iClass = iClass;
        this.save();
    }


    public static TaskRisk of(String string) {
        return types.getOrDefault(string, null);
    }


    public static TaskRisk add(String name, String description, String labelClass, String iClass, Organization organization) {
        TaskRisk d = new TaskRisk(name, description, labelClass, iClass, organization);
        d.save();
        return d;
    }

    public static boolean load() {
        for (TaskRisk d : find.all()) {
            types.put(d.name, d);
        }
        Logger.info("Reloading Task Risks");
        if (types.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    public static Finder<Long, TaskRisk> find = new Finder<>(TaskRisk.class);

    public static List<TaskRisk> all() {
        return find.all();
    }

    public static TaskRisk findById(Long id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static TaskRisk findByName(String name, Organization organization) {
        return find.query().where()
                .eq("name", name)
                .eq("organization_id", organization.getId()).findOne();
    }

    public static List<TaskRisk> findByOrganization(Organization organization) {
        return find.query().where().eq("organization_id", organization.getId()).orderBy("when_created asc").findList();
    }

    public static void bootstrapTaskRisksForOrganization(Organization organization) {
        /*
         *  Add 4 Tasks Risks for each org that gets created.
         * */
        TaskRisk.add("None", "No Risk", "label-primary", "text-primary", organization);
        TaskRisk.add("Low", "Low Risk", "label-primary", "text-primary", organization);
        TaskRisk.add("Medium", "Medium Risk", "label-primary", "text-primary", organization);
        TaskRisk.add("High", "High Risk", "label-primary", "text-primary", organization);
        TaskRisk.load();

    }
}
