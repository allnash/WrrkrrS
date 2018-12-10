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
import java.util.concurrent.ConcurrentHashMap;

@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"organization_id", "name"})
)
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceTypePropertyType extends BaseTypeModel {

    public static ConcurrentHashMap<String, DeviceTypePropertyType> types = new ConcurrentHashMap<String, DeviceTypePropertyType>();

    public DeviceTypePropertyType() {

    }

    public DeviceTypePropertyType(String name, String description, Organization organization) {
        this.name = name.toUpperCase();
        this.description = description;
        this.visibility = Visibility.PRIVATE;
        this.organization = organization;
        this.save();
    }


    public static DeviceTypePropertyType of(String string) {
        return types.getOrDefault(string, null);
    }


    public static DeviceTypePropertyType add(String name, String description, Organization organization) {
        DeviceTypePropertyType d = DeviceTypePropertyType.findByName(name, organization);
        if(d == null){
            d = new DeviceTypePropertyType(name, description, organization);
            d.save();
        }
        return d;
    }

    public static Finder<Long, DeviceTypePropertyType> find = new Finder<>(DeviceTypePropertyType.class);

    public static List<DeviceTypePropertyType> all() {
        return find.all();
    }

    public static DeviceTypePropertyType findById(Long id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static DeviceTypePropertyType findByName(String name, Organization organization) {
        return find.query().where()
                .eq("name", name.toUpperCase())
                .eq("organization_id", organization.getId()).findOne();
    }

    public static void bootstrapDevicePropertyTypesForOrganization(Organization organization){
        /*
        *  Add 5 product types for each org that gets created.
        * */
        DeviceTypePropertyType.add("Action", "Action type", organization);
        DeviceTypePropertyType.add("Event", "Event type", organization);
        DeviceTypePropertyType.add("Behavior", "Behavioral type", organization);
        DeviceTypePropertyType.add("Alert", "Alert type", organization);

    }


}
