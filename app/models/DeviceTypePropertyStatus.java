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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.List;

@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"organization_id", "name"})
)
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceTypePropertyStatus extends BaseStatusModel {

    public DeviceTypePropertyStatus() {

    }

    public DeviceTypePropertyStatus(String name, String description, String labelClass, Organization organization) {
        this.name = name.toUpperCase();
        this.description = description;
        this.visibility = Visibility.PRIVATE;
        this.organization = organization;
        this.labelClass = labelClass;
        this.save();
    }

    public static DeviceTypePropertyStatus add(String name, String description, String labelClass, Organization organization) {
        DeviceTypePropertyStatus d = DeviceTypePropertyStatus.findByName(name, organization);
        if(d == null){
            d = new DeviceTypePropertyStatus(name, description, labelClass, organization);
            d.save();
        }
        return d;
    }

    public static Finder<Long, DeviceTypePropertyStatus> find = new Finder<>(DeviceTypePropertyStatus.class);

    public static List<DeviceTypePropertyStatus> all() {
        return find.all();
    }

    public static List<DeviceTypePropertyStatus> findByOrganization(Organization organization) {
        return find.query().where().eq("organization_id", organization.getId()).orderBy("when_created asc").findList();
    }

    public static DeviceTypePropertyStatus findById(String id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static DeviceTypePropertyStatus findByName(String name, Organization organization) {
        return find.query().where()
                .eq("name", name.toUpperCase())
                .eq("organization_id", organization.getId()).findOne();
    }

    public static void bootstrapDevicePropertyStatusesForOrganization(Organization organization){
        /*
        *  Add 5 Project types for each org that gets created.
        * */
        DeviceTypePropertyStatus.add("Todo", "To Do", "label-primary", organization);
        DeviceTypePropertyStatus.add("Test", "In Review","label-warning", organization);
        DeviceTypePropertyStatus.add("Suspended", "Closed", "label-danger", organization);
        DeviceTypePropertyStatus.add("Live", "Live", "label-success", organization);
        DeviceTypePropertyStatus.add("Archived", "Archived", "label-default",organization);
    }
}
