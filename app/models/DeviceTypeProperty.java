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
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.Finder;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"organization_id", "name"})
)
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceTypeProperty extends BaseTypeModel {

    @JsonProperty("device_type_property_type")
    @ManyToOne
    public DeviceTypePropertyType deviceTypePropertyType;

    @JsonProperty("device_type_property_status")
    @ManyToOne
    public DeviceTypePropertyStatus deviceTypePropertyStatus;

    public DeviceTypeProperty() {

    }

    public DeviceTypeProperty(String name, String description, DeviceTypePropertyType type, DeviceTypePropertyStatus status, Organization o) {
        this.name = name;
        this.description = description;
        this.deviceTypePropertyType = type;
        this.visibility = Visibility.PRIVATE;
        this.deviceTypePropertyStatus = status;
        this.organization = o;
    }

    public static DeviceTypeProperty add(String name, String description, DeviceTypePropertyType type, DeviceTypePropertyStatus status, Organization organization) {
        DeviceTypeProperty d = DeviceTypeProperty.findByName(name, organization);
        if(d == null){
            d = new DeviceTypeProperty(name, description,  type, status, organization);
            d.save();
        }
        return d;
    }

    public static Finder<Long, DeviceTypeProperty> find = new Finder<>(DeviceTypeProperty.class);

    public static List<DeviceTypeProperty> all(Map parameterMap) {
        if (parameterMap != null)
            return find.query().where().allEq(parameterMap).findList();
        else
            return null;
    }

    public static List<DeviceTypeProperty> all() {
        return find.all();
    }

    public static DeviceTypeProperty findById(String ownerId) {
        return find.query().where().eq("id", ownerId).findOne();
    }

    public static DeviceTypeProperty findByName(String name, Organization organization) {
        return find.query().where()
                .eq("name", name)
                .eq("organization_id", organization.getId()).findOne();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DeviceTypePropertyStatus getDeviceTypePropertyStatus() {
        return deviceTypePropertyStatus;
    }

    public void setDeviceTypePropertyStatus(DeviceTypePropertyStatus deviceTypePropertyStatus) {
        this.deviceTypePropertyStatus = deviceTypePropertyStatus;
    }

    public DeviceTypePropertyType getDeviceTypePropertyType() {
        return deviceTypePropertyType;
    }

    public void setDeviceTypePropertyType(DeviceTypePropertyType deviceTypePropertyType) {
        this.deviceTypePropertyType = deviceTypePropertyType;
    }

    public static void bootstrapDevicePropertiesForOrganization(Organization organization){
        /*
         *  Add 8 device properties for each org that gets created.
         * */

        DeviceTypePropertyType alert = DeviceTypePropertyType.findByName("Alert", organization);
        DeviceTypePropertyType action = DeviceTypePropertyType.findByName("Action", organization);
        DeviceTypePropertyType behavior = DeviceTypePropertyType.findByName("Behavior", organization);
        DeviceTypePropertyType event = DeviceTypePropertyType.findByName("Event", organization);

        DeviceTypePropertyStatus live = DeviceTypePropertyStatus.findByName("Live", organization);
        DeviceTypePropertyStatus test = DeviceTypePropertyStatus.findByName("Test", organization);
        DeviceTypePropertyStatus suspended = DeviceTypePropertyStatus.findByName("Suspended", organization);

        DeviceTypeProperty.add("TURN_ON", "", action, live, organization);
        DeviceTypeProperty.add("TURN_OFF", "", action, suspended, organization);
        DeviceTypeProperty.add("PUSH_NOTIFICATION_ON", "", behavior, live, organization);
        DeviceTypeProperty.add("PUSH_NOTIFICATION_OFF", "", behavior, live, organization);
        DeviceTypeProperty.add("SEND_PUSH_NOTIFICATION", "", alert, live, organization);
        DeviceTypeProperty.add("SEND_TEXT", "", alert, test, organization);
        DeviceTypeProperty.add("SENSE_BEACON", "", event, live, organization);
        DeviceTypeProperty.add("GPS_ON", "", behavior, live, organization);
        DeviceTypeProperty.add("GPS_OFF", "", behavior, live, organization);

    }
}

