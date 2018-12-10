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
public class ProjectPriority extends BasePriority {

    public static ConcurrentHashMap<String, ProjectPriority> types = new ConcurrentHashMap<String, ProjectPriority>();

    public ProjectPriority() {

    }

    public ProjectPriority(String name, String description, String labelClass, String iClass, String elementClass,
                           Organization organization) {
        this.name = name.toUpperCase();
        this.description = description;
        this.availablePublicly = false;
        this.organization = organization;
        this.labelClass = labelClass;
        this.iClass = iClass;
        this.elementClass = elementClass;
        this.save();
    }


    public static ProjectPriority of(String string) {
        return types.getOrDefault(string, null);
    }


    public static ProjectPriority add(String name, String description, String labelClass, String iClass, String elementClass,
                                      Organization organization) {
        ProjectPriority d = new ProjectPriority(name, description, labelClass, iClass, elementClass, organization);
        d.save();
        return d;
    }

    public static boolean load() {
        for (ProjectPriority d : find.all()) {
            types.put(d.name, d);
        }
        Logger.info("Reloading Project Priorities");
        if (types.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    public static Finder<Long, ProjectPriority> find = new Finder<>(ProjectPriority.class);

    public static List<ProjectPriority> all() {
        return find.all();
    }

    public static ProjectPriority findById(Long id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static ProjectPriority findByName(String name, Organization organization) {
        return find.query().where()
                .eq("name", name.toUpperCase())
                .eq("organization_id", organization.getId()).findOne();
    }

    public static List<ProjectPriority> findByOrganization(Organization organization) {
        return find.query().where().eq("organization_id", organization.getId()).orderBy("when_created asc").findList();
    }

    public static void bootstrapProjectPrioritiesForOrganization(Organization organization) {
        /*
         *  Add 5 ProjectPriority for each org that gets created.
         * */
        ProjectPriority.add("None", "None Priority", "label-default", "text-default", "default", organization);
        ProjectPriority.add("Low", "Low Priority", "label-info", "text-info", "info", organization);
        ProjectPriority.add("Medium", "Medium Priority", "label-warning", "text-warning", "warning", organization);
        ProjectPriority.add("High", "High Priority", "label-warning", "text-warning", "warning", organization);
        ProjectPriority.add("Critical", "Critical Priority", "label-danger", "text-danger", "danger", organization);
        ProjectPriority.load();

    }
}
