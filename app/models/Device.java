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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.Finder;
import play.Logger;
import play.data.validation.Constraints.Required;
import play.libs.Json;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"organization_id", "id"})
)
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Device extends BaseTenantModel {

    /**
     * Device Table
     */

    @JsonProperty("external_id")
    public String externalId;

    @JsonProperty("mac_address")
    public String macAddress;

    @JsonIgnore
    @ManyToOne
    public User owner;

    @JsonIgnore
    @ManyToOne
    @Column(nullable = false)
    public Organization manufacturer;

    @Transient
    @JsonProperty("manufacturer_id")
    public String manufacturerId;
    @Transient
    @JsonProperty("manufacturer_name")
    public String manufacturerName;
    @Transient
    @JsonProperty("manufacturer_email_domain")
    public String manufacturerEmailDomain;

    @Transient
    @JsonProperty("owner_id")
    public String ownerId;
    @Transient
    @JsonProperty("owner_first_name")
    public String ownerFirstName;
    @Transient
    @JsonProperty("owner_last_name")
    public String ownerLastName;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.REMOVE)
    public Set<Place> places;
    /*
     *  Transient current Place
     * */
    @JsonProperty("current_place")
    @OneToOne
    public Place currentPlace;

    @Column(length = 128, nullable = false)
    public String name;

    @JsonIgnore
    public String deviceSecret;

    @ManyToOne
    @JsonProperty("device_type")
    @Column(nullable = false)
    public DeviceType deviceType;

    @Transient
    @JsonProperty("image")
    public TypeImage image;

    @Required
    @Column(nullable = false)
    public Boolean enabled;

    @JsonProperty("version_number")
    @Column(length = 64)
    public String versionNumber;

    @Column(length = 64)
    public String model;

    @ManyToOne
    public ProductRelease software;

    @JsonProperty("current_battery_level")
    public String currentbatteryLevel;
    @JsonProperty("maximum_battery_level")
    public String maximumBatteryLevel;

    @JsonIgnore
    @ManyToMany(mappedBy = "sensors")
    public Set<Floor> floors;

    @JsonIgnore
    @OneToMany(mappedBy = "sightedDevice")
    public List<Sighting> sightings;

    @JsonIgnore
    @Transient
    public Floor currentFloor;

    @JsonProperty("current_state_id")
    public String currentStateId;

    @JsonProperty("current_hearbeat_id")
    public String currentHeartbeatId;

    @JsonProperty("oui_company_name")
    public String ouiCompanyName;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "device_collaborator_organizations",
            joinColumns = @JoinColumn(name = "device_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "collaborator_organization_id", referencedColumnName = "id"))
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    public List<Organization> collaboratorOrganizations;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "device_collaborator_users",
            joinColumns = @JoinColumn(name = "device_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    public List<User> collaboratorUsers;

    public Device() {

    }

    public Device(String name, String deviceSecret, User owner, DeviceType deviceType, Organization organization, Organization manufacturer) {
        this.name = name;
        this.owner = owner;
        if (organization == null) {
            Logger.warn("Device organization not provided, using owners organization as a substitute for organization - " + name);
            this.organization = owner.getOrganization();
        } else {
            this.organization = organization;
        }
        if (manufacturer == null) {
            Logger.warn("Device manufacturer not provided, using owners organization as a substitute for manufacturer - " + name);
            this.manufacturer = owner.getOrganization();
        } else {
            this.manufacturer = manufacturer;
        }
        this.deviceType = deviceType;
        this.enabled = true;
        this.deviceSecret = deviceSecret;
    }

    public static Finder<String, Device> find = new Finder<>(Device.class);

    public static List<Device> all() {
        return find.all();
    }

    public static List<Device> findAllByParams(Map parameterMap) {
        return find.query().where().allEq(parameterMap).findList();
    }

    public static Device findByParams(Map parameterMap) {
        return (Device) find.query().where().allEq(parameterMap).findOne();
    }

    public static List<Device> findAllByOwner(User owner) {
        return find.query().where().eq("owner_id", owner.getId()).findList();
    }

    public static Device findByDeviceId(String deviceId) {
        return find.query().where().eq("id", deviceId).findOne();
    }

    public static Device findById(String id) {
        Device d = findByDeviceId(id);
        if (d == null) {
            d = findByExternalDeviceId(id);
        }
        return d;
    }

    public static Device findByNameAndOrganization(String name, Organization organization) {
        return find.query().where().eq("name", name)
                .eq("organization_id", organization.getId())
                .findOne();
    }


    public static Device findByExternalDeviceId(String externalDeviceId) {
        return find.query().where().eq("external_id", externalDeviceId).findOne();
    }

    public static List<Device> findAllByOrganization(Organization organization) {
        return find.query().where()
                .eq("organization_id", organization.getId()).findList();
    }

    public static List<Device> findAllByDeviceTypeOrganization(DeviceType deviceType, Organization organization) {
        return find.query().where().eq("organization_id", organization.getId())
                .eq("device_type_id", deviceType.getId())
                .findList();
    }

    public static List<Device> findAllByOwner(User owner, DeviceType of) {
        return find.query().where()
                .eq("owner_id", owner.getId())
                .eq("device_type_id", of.getId()).findList();
    }

    public static Device cloneFromJson(JsonNode deviceJson) {
        try {
            // Fetch Device name
            String deviceName = deviceJson.get("name").asText();
            String deviceId = deviceJson.get("id").asText();
            String deviceExternalId = deviceJson.get("external_id").asText();

            // Fetch Device Organization
            String deviceOrganizationId = deviceJson.get("organization_id").asText();
            String deviceOrganizationName = deviceJson.get("organization_name").asText();
            String deviceOrganizationEmailDomain = deviceJson.get("organization_email_domain").asText();
            Organization deviceOrganization = Organization.findById(deviceOrganizationId);
            if (deviceOrganization == null) {
                deviceOrganization = new Organization();
                deviceOrganization.setId(deviceOrganizationId);
                deviceOrganization.setName(deviceOrganizationName);
                deviceOrganization.setEmailDomain(deviceOrganizationEmailDomain);
                deviceOrganization.save();
            }

            // Fetch DeviceType
            DeviceType deviceType = Json.fromJson(deviceJson.get("device_type"), models.DeviceType.class);
            if (DeviceType.find.query().where().eq("id", deviceType.getId()).findOne() == null) {
                deviceType.setOrganization(deviceOrganization);
                deviceType.setImage(null);
                deviceType.save();
            } else {
                deviceType = DeviceType.find.query().where().eq("id", deviceType.getId()).findOne();
            }


            // Fetch Device Place
            Place place = null;
            if (deviceJson.has("current_place")) {
                place = Json.fromJson(deviceJson.get("current_place"), models.Place.class);
                if (Place.find.query().where().eq("id", place.getId()).findOne() == null) {
                    place.setOrganization(deviceOrganization);
                    place.save();
                }
            }

            // Fetch Device Owner
            User deviceOwner = null;
            if (deviceJson.has("owner_id")) {
                String deviceOwnerId = deviceJson.get("owner_id").asText();
                String deviceOwnerFirstName = deviceJson.get("owner_first_name").asText();
                String deviceOwnerLastName = deviceJson.get("owner_last_name").asText();
                deviceOwner = User.findById(deviceOwnerId);
                if (deviceOwner == null) {
                    deviceOwner = new User();
                    deviceOwner.setId(deviceOwnerId);
                    deviceOwner.setFirstName(deviceOwnerFirstName);
                    deviceOwner.setLastName(deviceOwnerLastName);
                    deviceOwner.setOrganization(deviceOrganization);
                    deviceOwner.save();
                }
            }
            // Fetch Device Manufacturer
            String deviceManufacturerId = deviceJson.get("manufacturer_id").asText();
            String deviceManufacturerName = deviceJson.get("manufacturer_name").asText();
            String deviceManufacturerEmailDomain = deviceJson.get("manufacturer_email_domain").asText();
            Organization deviceManufacturer = Organization.findById(deviceManufacturerId);
            if (deviceManufacturer == null) {
                deviceManufacturer = new Organization();
                deviceManufacturer.setId(deviceManufacturerId);
                deviceManufacturer.setName(deviceManufacturerName);
                deviceManufacturer.setEmailDomain(deviceManufacturerEmailDomain);
                deviceManufacturer.save();
            }

            Device d = new Device(deviceName, null, deviceOwner, deviceType, deviceOrganization, deviceManufacturer);
            d.setId(deviceId);
            d.setCurrentPlace(place);
            d.setExternalId(deviceExternalId);

            // Mac address
            if (deviceJson.has("oui_company_name")) {
                d.setMacAddress(deviceJson.get("oui_company_name").asText());
            }
            // Async check
            if (Device.findByExternalDeviceId(deviceExternalId) == null) {
                d.save();
            }
            return d;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("error importing device - " + e.getMessage());
            return null;
        }
    }

    public static Device updateFromJson(Device myDevice, JsonNode deviceJson) {
        myDevice.setName(deviceJson.get("name").asText());
        // Fetch Device Model
        if (deviceJson.has("model")) {
            myDevice.setModel(deviceJson.get("model").asText());
        }
        // Fetch Device Organization
        String deviceOrganizationId = deviceJson.get("organization_id").asText();
        String deviceOrganizationName = deviceJson.get("organization_name").asText();
        String deviceOrganizationEmailDomain = deviceJson.get("organization_email_domain").asText();
        Organization deviceOrganization = Organization.findById(deviceOrganizationId);
        if (deviceOrganization == null) {
            deviceOrganization = new Organization();
            deviceOrganization.setId(deviceOrganizationId);
            deviceOrganization.setName(deviceOrganizationName);
            deviceOrganization.setEmailDomain(deviceOrganizationEmailDomain);
            deviceOrganization.save();
        }
        myDevice.setOrganization(deviceOrganization);
        // Fetch Device Owner
        User deviceOwner = null;
        if (deviceJson.has("owner_id")) {
            String deviceOwnerId = deviceJson.get("owner_id").asText();
            String deviceOwnerFirstName = deviceJson.get("owner_first_name").asText();
            String deviceOwnerLastName = deviceJson.get("owner_last_name").asText();
            deviceOwner = User.findById(deviceOwnerId);
            if (deviceOwner == null) {
                deviceOwner = new User();
                deviceOwner.setId(deviceOwnerId);
                deviceOwner.setFirstName(deviceOwnerFirstName);
                deviceOwner.setLastName(deviceOwnerLastName);
                deviceOwner.setOrganization(deviceOrganization);
                deviceOwner.save();
            }
            myDevice.setOwner(deviceOwner);
        }
        return myDevice;
    }

    public static Device findByMacAddress(String deviceMacAddress) {
        // Special handling of null or empty values due to legacy records.
        if(deviceMacAddress == null || deviceMacAddress.isEmpty()){
            return null;
        }
        return find.query().where().eq("mac_address", deviceMacAddress).findOne();
    }

    public boolean validateMe(String device_secret) {
        boolean ret = true;
        if (!device_secret.equals(getDeviceSecret())) {
            ret = false;
        }
        return ret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    private String getDeviceSecret() {
        return deviceSecret;
    }

    public void setDeviceSecret(String deviceSecret) {
        this.deviceSecret = deviceSecret;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Organization getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Organization manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Organization> getCollaboratorOrganizations() {
        return collaboratorOrganizations;
    }

    public void setCollaboratorOrganizations(List<Organization> collaboratorOrganizations) {
        this.collaboratorOrganizations = collaboratorOrganizations;
    }

    public List<User> getCollaboratorUsers() {
        return collaboratorUsers;
    }

    public void setCollaboratorUsers(List<User> collaboratorUsers) {
        this.collaboratorUsers = collaboratorUsers;
    }

    public String getOrganizationName() {
        return this.organization.getName();
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationId() {
        return this.getOrganization().getId();
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getManufacturerId() {
        return this.getManufacturer().getId();
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getManufacturerName() {
        return this.getManufacturer().getName();
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getCurrentbatteryLevel() {
        return currentbatteryLevel;
    }

    public void setCurrentbatteryLevel(String currentbatteryLevel) {
        this.currentbatteryLevel = currentbatteryLevel;
    }

    public String getMaximumBatteryLevel() {
        return maximumBatteryLevel;
    }

    public void setMaximumBatteryLevel(String maximumBatteryLevel) {
        this.maximumBatteryLevel = maximumBatteryLevel;
    }

    public Set<Place> getPlaces() {
        return places;
    }

    public void setPlaces(Set<Place> places) {
        this.places = places;
    }

    public Place getCurrentPlace() {
        return currentPlace;
    }

    public void setCurrentPlace(Place currentPlace) {
        this.currentPlace = currentPlace;
    }

    public Set<Floor> getFloors() {
        return floors;
    }

    public void setFloors(Set<Floor> floors) {
        this.floors = floors;
    }

    public void updateCurrentPlace(Place place) {
        this.setCurrentPlace(place);
        this.places.add(place);
    }

    public TypeImage getImage() {
        if (this.deviceType != null) {
            return this.deviceType.getImage();
        } else {
            return null;
        }
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getManufacturerEmailDomain() {
        return this.manufacturer.getEmailDomain();
    }

    public void setManufacturerEmailDomain(String manufacturerEmailDomain) {
        this.manufacturerEmailDomain = manufacturerEmailDomain;
    }

    public String getOwnerId() {
        if (this.owner != null) {
            return this.owner.getId();
        } else {
            return null;
        }
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerFirstName() {
        if (this.owner.firstName != null) {
            return this.owner.getFirstName();
        } else {
            return "";
        }
    }

    public void setOwnerFirstName(String ownerFirstName) {
        this.ownerFirstName = ownerFirstName;
    }

    public String getOwnerLastName() {
        if (this.owner.lastName != null) {
            return this.owner.getLastName();
        } else {
            return "";
        }
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    public ProductRelease getSoftware() {
        return software;
    }

    public void setSoftware(ProductRelease software) {
        this.software = software;
    }

    public Floor getCurrentFloor() {
        if (this.floors.size() > 0) {
            return this.floors.iterator().next();
        } else {
            return null;
        }
    }

    public String getCurrentStateId() {
        return currentStateId;
    }

    public void setCurrentStateId(String currentStateId) {
        this.currentStateId = currentStateId;
    }

    public String getCurrentHeartbeatId() {
        return currentHeartbeatId;
    }

    public void setCurrentHeartbeatId(String currentheartbeatId) {
        this.currentHeartbeatId = currentheartbeatId;
    }

    public String getOuiCompanyName() {
        return ouiCompanyName;
    }

    public void setOuiCompanyName(String ouiCompanyName) {
        this.ouiCompanyName = ouiCompanyName;
    }
}

