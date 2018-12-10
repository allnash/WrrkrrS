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
public class ProjectStatus extends BaseStatusModel {

    public static ConcurrentHashMap<String, ProjectStatus> types = new ConcurrentHashMap<String, ProjectStatus>();

    public ProjectStatus() {

    }

    public ProjectStatus(String name, String description, String labelClass, Organization organization) {
        this.name = name.toUpperCase();
        this.description = description;
        this.visibility = Visibility.PRIVATE;
        this.organization = organization;
        this.labelClass = labelClass;
        this.save();
    }


    public static ProjectStatus of(String string) {
        return types.getOrDefault(string, null);
    }


    public static ProjectStatus add(String name, String description, String labelClass, Organization organization) {
        ProjectStatus d = new ProjectStatus(name, description, labelClass, organization);
        d.save();
        return d;
    }

    public static boolean load() {
        for (ProjectStatus d : find.all()) {
            types.put(d.name, d);
        }
        Logger.info("Reloading Project Statuses");
        if (types.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    public static Finder<Long, ProjectStatus> find = new Finder<>(ProjectStatus.class);

    public static List<ProjectStatus> all() {
        return find.all();
    }

    public static List<ProjectStatus> findByOrganization(Organization organization) {
        return find.query().where().eq("organization_id", organization.getId()).orderBy("when_created asc").findList();
    }

    public static ProjectStatus findById(String id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static ProjectStatus findByName(String name, Organization organization) {
        return find.query().where()
                .eq("name", name)
                .eq("organization_id", organization.getId()).findOne();
    }

    public static void bootstrapProjectStatusesForOrganization(Organization organization) {
        /*
         *  Add 5 Project types for each org that gets created.
         * */
        ProjectStatus.add("Todo", "To Do", "label-primary", organization);
        ProjectStatus.add("Open", "Open", "label-primary", organization);
        ProjectStatus.add("In_Progress", "In Progress", "label-warning", organization);
        ProjectStatus.add("In_Review", "In Review", "label-info", organization);
        ProjectStatus.add("Closed", "Closed", "label-success", organization);
        ProjectStatus.add("Archived", "Archived", "label-default", organization);
        ProjectStatus.load();

    }
}
