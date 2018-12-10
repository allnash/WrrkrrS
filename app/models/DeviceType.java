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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.Finder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"organization_id", "name"})
)
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceType extends BaseTypeModel {

    @JsonIgnore
    private static String TEMPLATE_OLD = "<div><span style=\"font-weight: bold;\">ID</span>:&nbsp; [[device.id]]</div>" +
            "<span style=\"font-weight: bold;\">Name</span>:&nbsp; [[device.name]]<div>" +
            "<span style=\"font-weight: bold;\">Manufacturer</span> <span style=\"font-weight: bold;\">name</span>" +
            ":&nbsp;[[device.manufacturer_name]]</div><div><span style=\"font-weight: bold;\">Model</span>" +
            ":&nbsp;[[device.model]]<br></div><div><span style=\"font-weight: bold;\">Enabled</span>" +
            ":&nbsp;[[device.enabled]]</div>\n<div>\n</div>\n<hr>\n" +
            "<div><span style=\"font-weight: bold;\">Owner: </span>[[device.owner_full_name]]</div>";

    @JsonIgnore
    private static String TEMPLATE = "<div class=\"row\"> <div class=\"col-lg-9\"> <div class=\"wrapper wrapper-content " +
            "animated fadeInRight\"> <div class=\"ibox\"> <div class=\"ibox-content\"> <div class=\"row\"> " +
            "<div class=\"col-lg-12\"> <div class=\"m-b-md\"> <h2>[[device.name]]</h2> </div> " +
            "<dl class=\"dl-horizontal\"> <dt>Status:</dt> <dd> <span class=\"label label-primary\" " +
            "ng-if=\"device.enabled\">Active</span> <span class=\"label label-danger\" ng-if=\"!device.enabled\">Disabled</span>" +
            " </dd> </dl> </div> </div> <div class=\"row\"> <div class=\"col-lg-5\"> <dl class=\"dl-horizontal\"> " +
            "<dt>Created by:</dt> <dd>[[device.by.full_name || '(not available)']]</dd> <dt>Messages:</dt> <dd> 162</dd> " +
            "<dt>Manufacturer:</dt> <dd><a href=\"\" class=\"text-navy\"> [[device.manufacturer_name]]</a></dd> <dt>Model:</dt> " +
            "<dd> [[device.model]]</dd> </dl> </div> <div class=\"col-lg-7\" id=\"cluster_info\"> <dl class=\"dl-horizontal\"> " +
            "<dt>Last Updated:</dt> <dd> [[device.when_created | amUtc | amLocal | amDateFormat:'MM.DD.YYYY HH:mm:ss']]</dd> " +
            "<dt>Created:</dt> <dd> [[device.when_updated | amUtc | amLocal | amDateFormat:'MM.DD.YYYY HH:mm:ss']]</dd> " +
            "<dt>Collaborators (sample):</dt> <dd class=\"project-people\"> <a href=\"\">" +
            "<img alt=\"image\" class=\"img-circle\" src=\"img/a3.jpg\"></a> <a href=\"\">" +
            "<img alt=\"image\" class=\"img-circle\" src=\"img/a1.jpg\"></a> <a href=\"\">" +
            "<img alt=\"image\" class=\"img-circle\" src=\"img/a2.jpg\"></a> <a href=\"\">" +
            "<img alt=\"image\" class=\"img-circle\" src=\"img/a4.jpg\"></a> <a href=\"\">" +
            "<img alt=\"image\" class=\"img-circle\" src=\"img/a5.jpg\"></a> </dd> </dl> " +
            "</div> </div> </div> </div> </div> </div></div>";

    @ManyToOne
    @JsonIgnore
    @Column(nullable = false)
    public Html html;

    @Transient
    @JsonProperty("device_type_html")
    public String deviceTypeHtml;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "device_type_properties",
            joinColumns = @JoinColumn(name = "device_type_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "device_type_property_id", referencedColumnName = "id"))
    public List<DeviceTypeProperty> properties;

    @Transient
    @JsonProperty("properties_count")
    public Integer propertiesCount;


    public DeviceType() {

    }

    public DeviceType(String name, String description, Organization organization) {
        this.name = name.toUpperCase();
        this.description = description;
        this.visibility = Visibility.PRIVATE;
        this.organization = organization;
        Html html = new Html(TEMPLATE, organization);
        html.save();
        this.html = html;
    }

    public static DeviceType add(String name, String description, Organization organization) {
        DeviceType d = DeviceType.findByNameAndOrganization(name, organization);
        if (d == null) {
            d = new DeviceType(name, description, organization);
            d.save();
        }
        return d;
    }

    public static Finder<String, DeviceType> find = new Finder<>(DeviceType.class);

    public static List<DeviceType> all() {
        return find.all();
    }

    public static DeviceType findById(String id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static DeviceType findByNameAndOrganization(String name, Organization organization) {
        return find.query().where()
                .eq("name", name)
                .eq("organization_id", organization.getId()).findOne();
    }

    public static List<DeviceType> findAllByOrganization(Organization organization) {
        return find.query().where()
                .eq("organization_id", organization.getId()).findList();
    }

    public List<DeviceTypeProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<DeviceTypeProperty> properties) {
        this.properties = properties;
    }

    public Html getHtml() {
        return html;
    }

    public void setHtml(Html html) {
        this.html = html;
    }

    public void setDeviceTypeHtml(String deviceTypeHtml) {
        this.deviceTypeHtml = deviceTypeHtml;
    }

    public String getDeviceTypeHtml() {
        if (this.html != null) {
            return this.html.getData();
        } else {
            return null;
        }
    }

    public static void bootstrapDeviceTypesForOrganization(Organization organization) {
        /*
         *  Add 4 device types for each org that gets created.
         * */
        DeviceType genericDeviceType = DeviceType.add("GENERIC", "Generic type", organization);
        DeviceType phoneDeviceType = DeviceType.add("PHONE", "Phone Device", organization);
        DeviceType hardwareDeviceType = DeviceType.add("HARDWARE", "Abstract Hardware Device", organization);
        DeviceType wrrKrrReader = DeviceType.add("WRRKRR_READER", "WrrKrr Reader", organization);
        DeviceType beaconDeviceType = DeviceType.add("BEACON", "Beacon device type", organization);


        // Add properties to 3 device types

        List<DeviceTypeProperty> properties;
        properties = new ArrayList<>();
        properties.add(DeviceTypeProperty.findByName("TURN_ON", organization));
        properties.add(DeviceTypeProperty.findByName("TURN_OFF", organization));
        TypeImage image = new TypeImage(organization, "");
        image.setUrl("img/bluetooth_logo.png");
        image.setW(32);
        image.setH(32);
        image.save();
        genericDeviceType.setProperties(properties);
        genericDeviceType.setImage(image);
        genericDeviceType.update();

        properties = new ArrayList<>();
        properties.add(DeviceTypeProperty.findByName("GPS_ON", organization));
        properties.add(DeviceTypeProperty.findByName("GPS_OFF", organization));
        properties.add(DeviceTypeProperty.findByName("PUSH_NOTIFICATION_ON", organization));
        properties.add(DeviceTypeProperty.findByName("PUSH_NOTIFICATION_OFF", organization));
        properties.add(DeviceTypeProperty.findByName("SEND_PUSH_NOTIFICATION", organization));
        properties.add(DeviceTypeProperty.findByName("SEND_TEXT", organization));
        properties.add(DeviceTypeProperty.findByName("SENSE_BEACON", organization));
        image = new TypeImage(organization, "");
        image.setUrl("img/bluetooth_logo.png");
        image.setW(32);
        image.setH(32);
        image.save();
        phoneDeviceType.setProperties(properties);
        phoneDeviceType.setImage(image);
        phoneDeviceType.update();

        properties = new ArrayList<>();
        properties.add(DeviceTypeProperty.findByName("TURN_ON", organization));
        properties.add(DeviceTypeProperty.findByName("TURN_OFF", organization));
        properties.add(DeviceTypeProperty.findByName("SENSE_BEACON", organization));
        image = new TypeImage(organization, "");
        image.setUrl("img/bluetooth_logo.png");
        image.setW(32);
        image.setH(32);
        image.save();
        hardwareDeviceType.setProperties(properties);
        hardwareDeviceType.setImage(image);
        hardwareDeviceType.update();


        properties = new ArrayList<>();
        properties.add(DeviceTypeProperty.findByName("TURN_ON", organization));
        properties.add(DeviceTypeProperty.findByName("TURN_OFF", organization));
        properties.add(DeviceTypeProperty.findByName("SENSE_BEACON", organization));
        image = new TypeImage(organization, "");
        image.setUrl("img/bluetooth_logo.png");
        image.setW(32);
        image.setH(32);
        image.save();
        wrrKrrReader.setProperties(properties);
        wrrKrrReader.setImage(image);
        wrrKrrReader.update();


        // Add properties to 3 device types

        properties = new ArrayList<>();
        properties.add(DeviceTypeProperty.findByName("TURN_ON", organization));
        properties.add(DeviceTypeProperty.findByName("TURN_OFF", organization));
        image = new TypeImage(organization, "");
        image.setUrl("img/bluetooth_logo.png");
        image.setW(32);
        image.setH(32);
        image.save();
        beaconDeviceType.setProperties(properties);
        beaconDeviceType.setImage(image);
        beaconDeviceType.update();

    }

    public Integer getPropertiesCount() {
        List<DeviceTypeProperty> filteredProperties = new ArrayList<>();
        for (DeviceTypeProperty p : this.properties) {
            if (!p.getDeleted()) {
                filteredProperties.add(p);
            }
        }
        return filteredProperties.size();
    }

    public void setPropertiesCount(Integer propertiesCount) {
        this.propertiesCount = propertiesCount;
    }
}
