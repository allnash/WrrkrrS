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

import javax.persistence.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"organization_id", "name"})
)
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectType extends BaseTypeModel {

    public static ConcurrentHashMap<String, ProjectType> types = new ConcurrentHashMap<String, ProjectType>();

    public ProjectType() {

    }

    public ProjectType(String name, String description, String labelClass, Organization organization) {
        this.name = name.toUpperCase();
        this.description = description;
        this.visibility = Visibility.PRIVATE;
        this.organization = organization;
        this.labelClass = labelClass;
        this.save();
    }


    public static ProjectType of(String string) {
        return types.getOrDefault(string, null);
    }


    public static ProjectType add(String name, String description, String labelClass, Organization organization) {
        ProjectType d = new ProjectType(name, description, labelClass, organization);
        d.save();
        return d;
    }

    public static boolean load() {
        for (ProjectType d : find.all()) {
            types.put(d.name, d);
        }
        Logger.info("Reloading Project Types");
        if (types.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    public static Finder<Long, ProjectType> find = new Finder<>(ProjectType.class);

    public static List<ProjectType> all() {
        return find.all();
    }

    public static ProjectType findById(Long id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static ProjectType findByName(String name, Organization organization) {
        return find.query().where()
                .eq("name", name)
                .eq("organization_id", organization.getId()).findOne();
    }

    public static List<ProjectType> findByOrganization(Organization organization) {
        return find.query().where().eq("organization_id", organization.getId()).orderBy("when_created asc").findList();
    }

    public static void bootstrapProjectTypesForOrganization(Organization organization){
        /*
        *  Add 5 Project types for each org that gets created.
        * */
        ProjectType.add("Basic", "Basic", "label-primary", organization);
        ProjectType.add("Advanced", "Advanced", "label-primary", organization);
        ProjectType.add("Repeating", "Repeating", "label-primary", organization);
        ProjectType.add("Custom", "Custom", "label-primary", organization);
        ProjectType.add("Informational", "Informational", "label-primary", organization);
        ProjectType.load();

    }
}
