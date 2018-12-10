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

package controllers;

import cmodels.DeviceHeartbeat;
import cmodels.DeviceHeartbeatRepository;
import cmodels.DeviceState;
import cmodels.DeviceStateRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javafaker.Faker;
import db.CouchDB;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import utils.OUI;
import utils.RandomString;
import utils.SecretMaker;
import utils.Utils;

import java.io.IOException;
import java.util.*;

/*
 * This controller contains Organizations app common logic
 */

public class Devices extends BaseController {

    @Security.Authenticated(SecuredApi.class)
    public Result post() {

        ObjectNode result = Json.newObject();
        JsonNode json = request().body().asJson();

        String deviceName = json.get("device_name").asText();
        String deviceExternalId = json.get("device_external_id").asText();
        String deviceMacAddress = json.get("device_mac_address").asText(null);
        String deviceVersionNumber = json.get("device_version_number").asText();
        String deviceTypeName = json.get("device_type_name").asText();


        String deviceOrganizationId = null, deviceManufacturerId = null;

        if (json.has("device_organization_id")) {
            deviceOrganizationId = json.get("device_organization_id").asText();
        }
        if (json.has("device_manufacturer_id")) {
            deviceManufacturerId = json.get("device_manufacturer_id").asText();
        }

        // Fetch existing device with Mac address field
        Device myDevice = Device.findByMacAddress(deviceMacAddress);
        // Fetch existing device with serial number as External Id
        if (myDevice == null) {
            if (!deviceExternalId.isEmpty()) {
                myDevice = Device.findByExternalDeviceId(deviceExternalId);
            }
        }

        // If record exists then  sent it back.
        if (myDevice != null) {
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("device", Json.toJson(myDevice));
            return ok(result);
        }

        // Organization is Omegatrace if not provided
        Organization organization = Organization.findByEmailDomain(DataMigration.OMEGATRACE_DOMAIN);
        if (deviceOrganizationId != null) {
            organization = Organization.findById(deviceOrganizationId);
        }
        // Manufacturer is Omegatrace if not provided
        Organization manufacturer = Organization.findByEmailDomain(DataMigration.OMEGATRACE_DOMAIN);
        ;
        if (deviceManufacturerId != null) {
            manufacturer = Organization.findById(deviceManufacturerId);
        }

        // Create a place
        Place devicePlace = new Place("Unknown Home", PlaceType.findByName("Home", organization), organization);
        devicePlace.save();

        // Create device
        // NOTE: Fake owner is associated here.
        String myDeviceSecret = SecretMaker.hashMac(UUID.randomUUID().toString(), new RandomString(12).nextString());
        User user = new User(deviceName, null, null, null, devicePlace, organization);
        user.save();
        DeviceType deviceType = DeviceType.findByNameAndOrganization(deviceTypeName, organization);
        if(deviceType == null){
            Logger.error("Device type with name - " + deviceTypeName + " is not found for organization id - " + organization.getId());
            return badRequest();
        }
        myDevice = new Device(deviceName, myDeviceSecret, user, deviceType,
                organization, manufacturer);
        myDevice.setVersionNumber(deviceVersionNumber);
        myDevice.setMacAddress(deviceMacAddress);

        if(deviceMacAddress != null){
            String ouiCompanyName = OUI.getCompanyNameForOUI(myDevice.getMacAddress());
            myDevice.setOuiCompanyName(ouiCompanyName);
        }

        myDevice.setExternalId(deviceExternalId);
        myDevice.setCurrentPlace(devicePlace);
        myDevice.save();

        result.put("status", "ok");
        result.put("success", "ok");
        result.set("device", Json.toJson(myDevice));
        return ok(result);

    }


    @Security.Authenticated(Secured.class)
    public Result addDevice() {

        ObjectNode result = Json.newObject();
        JsonNode json = request().body().asJson();

        String deviceName = json.get("name").asText();
        String deviceExternalId = null;
        String deviceMacAddress = json.get("mac_address").asText(null);
        String deviceVersionNumber = json.get("version_number").asText();
        String deviceTypeName = json.get("device_type").get("name").asText();
        String deviceManufacturerId = json.get("manufacturer").get("id").asText();

        if (json.has("external_id")) {
            deviceExternalId = json.get("external_id").asText(null);
        }

        // Fetch existing device with Mac address field
        Device myDevice = Device.findByMacAddress(deviceMacAddress);
        // Fetch existing device with serial number as External Id
        if (myDevice == null) {
            if (!deviceExternalId.isEmpty()) {
                myDevice = Device.findByExternalDeviceId(deviceExternalId);
            }
        }

        // If record exists then  sent it back.
        if (myDevice != null) {
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("device", Json.toJson(myDevice));
            return ok(result);
        }

        // Organization is Omegatrace if not provided
        Organization organization = organization();
        // Manufacturer is Omegatrace if not provided
        Organization manufacturer = organization;
        if (deviceManufacturerId != null) {
            manufacturer = Organization.findById(deviceManufacturerId);
        }

        // Create a place
        Place devicePlace = new Place("Unknown Home", PlaceType.findByName("Home", organization), organization);
        devicePlace.save();

        // Create device
        // NOTE: Fake owner is associated here.
        String myDeviceSecret = SecretMaker.hashMac(UUID.randomUUID().toString(), new RandomString(12).nextString());
        User user = new User(deviceName, null, null, null, devicePlace, organization);
        user.save();
        DeviceType deviceType = DeviceType.findByNameAndOrganization(deviceTypeName, organization);
        if(deviceType == null){
            Logger.error("Device type with name - " + deviceTypeName + " is not found for organization id - " + organization.getId());
            return badRequest();
        }
        myDevice = new Device(deviceName, myDeviceSecret, user, deviceType,
                organization, manufacturer);
        myDevice.setVersionNumber(deviceVersionNumber);
        myDevice.setMacAddress(deviceMacAddress);

        if(deviceMacAddress != null){
            String ouiCompanyName = OUI.getCompanyNameForOUI(myDevice.getMacAddress());
            myDevice.setOuiCompanyName(ouiCompanyName);
        }

        if(deviceExternalId != null){
            myDevice.setExternalId(deviceExternalId);
        }
        myDevice.setCurrentPlace(devicePlace);
        myDevice.save();

        result.put("status", "ok");
        result.put("success", "ok");
        result.set("device", Json.toJson(myDevice));
        return ok(result);

    }

    @Security.Authenticated(Secured.class)
    public Result getHeartbeat(String deviceId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Device myDevice = Device.findById(deviceId);
        if (myDevice == null) {
            myDevice = Device.findByExternalDeviceId(deviceId);
        }
        if (myDevice.getOrganization().equals(myUser.getOrganization())) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            DeviceHeartbeatRepository deviceHeartbeatRepository = new DeviceHeartbeatRepository(CouchDB.db);
            DeviceHeartbeat deviceHeartbeat = deviceHeartbeatRepository.get(myDevice.getCurrentHeartbeatId());
            result.set("device_heartbeat", Json.toJson(deviceHeartbeat));
            return ok(result);
        } else {
            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result getHeartbeats(String deviceId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Device myDevice = Device.findById(deviceId);
        if (myDevice == null) {
            myDevice = Device.findByExternalDeviceId(deviceId);
        }
        if (myDevice.getOrganization().equals(myUser.getOrganization())) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            DeviceHeartbeatRepository deviceHeartbeatRepository = new DeviceHeartbeatRepository(CouchDB.db);
            List<DeviceHeartbeat> deviceHeartbeats = deviceHeartbeatRepository.findByDeviceId(myDevice.getId());
            result.set("device_heartbeats", Json.toJson(deviceHeartbeats));
            return ok(result);
        } else {
            return badRequest();
        }
    }

    @Security.Authenticated(SecuredApi.class)
    public Result postHeartbeat(String deviceId) {

        ObjectNode result = Json.newObject();
        JsonNode json = request().body().asJson();

        DeviceHeartbeatRepository deviceHeartbeatRepository = new DeviceHeartbeatRepository(CouchDB.db);
        ObjectMapper objectMapper = new ObjectMapper();

        // Get Device
        Device myDevice = Device.findByDeviceId(deviceId);
        // Get Device State
        DeviceHeartbeat deviceHeartbeat;
        // If not empty Create new state and re-fetch
        deviceHeartbeat = new DeviceHeartbeat();
        deviceHeartbeat.setWhenReceived(Long.valueOf(Utils.getCurrentTimestamp().getTime()));
        deviceHeartbeat.setDeviceId(myDevice.getId());
        deviceHeartbeat.setData(new HashMap<>());
        deviceHeartbeatRepository.add(deviceHeartbeat);
        myDevice.setCurrentHeartbeatId(deviceHeartbeat.getId());
        myDevice.save();
        deviceHeartbeat = deviceHeartbeatRepository.get(myDevice.getCurrentHeartbeatId());

        // Get Data
        Map<String, Object> data = deviceHeartbeat.getData();
        // Deserialize and save a Map
        try {
            // Add heartbeat data to Device State
            String textValue = objectMapper.writeValueAsString(json);
            Map<String, Object> heartbeatData = objectMapper.readValue(textValue, new TypeReference<Map<String, Object>>() {
            });

            // Update device heartbeat state
            deviceHeartbeat.setData(heartbeatData);
            deviceHeartbeatRepository.update(deviceHeartbeat);

        } catch (IOException e) {
           Logger.error("error storing heartbeat for device - " + myDevice.getId() + " , reason -> " + e.getMessage());
        }

        result.put("status", "ok");
        result.put("success", "ok");
        return ok(result);

    }

    @Security.Authenticated(SecuredApi.class)
    public Result get(String deviceId) {
        Device myDevice = Device.findByDeviceId(deviceId);
        if (myDevice == null) {
            myDevice = Device.findByExternalDeviceId(deviceId);
        }
        ObjectNode result = Json.newObject();
        if(myDevice != null){
            result.set("device", Json.toJson(myDevice));
        }
        result.put("status", "ok");
        result.put("success", "ok");
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result getDeviceInfo(String deviceId) {
        Device myDevice = Device.findByDeviceId(deviceId);
        if (myDevice == null) {
            myDevice = Device.findByExternalDeviceId(deviceId);
        }
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("device", Json.toJson(myDevice));
        Logger.info("Fetching device info - " + myDevice.getId());
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result getDeviceStates(String deviceId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Device myDevice = Device.findById(deviceId);
        if (myDevice == null) {
            myDevice = Device.findByExternalDeviceId(deviceId);
        }
        if (myDevice.getOwner().equals(myUser) || myDevice.getOrganization().equals(myUser.getOrganization())) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            DeviceStateRepository deviceStateRepository = new DeviceStateRepository(CouchDB.db);
            List<DeviceState> deviceStates = deviceStateRepository.findByDeviceId(myDevice.getId());
            result.set("device_states", Json.toJson(deviceStates));
            return ok(result);
        } else {
            return badRequest();
        }
    }


    @Security.Authenticated(Secured.class)
    public Result getDeviceState(String deviceId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Device myDevice = Device.findById(deviceId);
        if (myDevice == null) {
            myDevice = Device.findByExternalDeviceId(deviceId);
        }
        if (myDevice.getOrganization().equals(myUser.getOrganization())) {
            ObjectNode result = Json.newObject();
            DeviceStateRepository deviceStateRepository = new DeviceStateRepository(CouchDB.db);
            if(myDevice.getCurrentStateId() != null){
                DeviceState deviceState = deviceStateRepository.get(myDevice.getCurrentStateId());
                result.set("device_state", Json.toJson(deviceState));
            } else {
                result.set("device_state", Json.newObject());
            }
            result.put("status", "ok");
            result.put("success", "ok");
            return ok(result);
        } else {
            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result getDeviceType(String deviceId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Device myDevice = Device.findById(deviceId);
        if (myDevice.getOrganization().equals(myUser.getOrganization())) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("device_type", Json.toJson(myDevice.getDeviceType()));
            return ok(result);
        } else {
            return badRequest();
        }
    }


    @Security.Authenticated(Secured.class)
    public Result getCurrentUserDevices() {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("devices", Json.toJson(Device.findAllByOwner(myUser)));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result getOrganizationDevices(String id) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Organization organization = Organization.findById(id);
        JsonNode json = request().body().asJson();
        if (myUser.getSuperadmin() || myUser.getOrganization().equals(organization)) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("devices", Json.toJson(Device.findAllByOrganization(organization)));
            return ok(result);
        } else {
            return badRequest();
        }
    }

    public Result getDevices() {
        if (!Utils.isDebugMode()) {
            return badRequest();
        } else {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("devices", Json.toJson(Device.all()));
            return ok(result);
        }
    }

    @Security.Authenticated(Secured.class)
    public Result getOrganizationReaderDevices(String id) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Organization organization = Organization.findById(id);
        if (myUser.getOrganization().equals(organization)) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            if(myUser.getSuperadmin()){
                List<Device> devices =  new ArrayList<>();
                for(Organization o : Organization.find.all()){
                    DeviceType deviceType =  DeviceType.findByNameAndOrganization("WRRKRR_READER", o);
                    if(deviceType != null){
                        List<Device> readers = Device.findAllByDeviceTypeOrganization(deviceType, o);
                        devices.addAll(readers);
                    }
                }
                result.set("devices", Json.toJson(devices));
            } else if(myUser.getOrganizationName().equals("DistrictHall")){
                List<Device> devices =  new ArrayList<>();
                devices.add(Device.findByDeviceId("9984e95f-5ea3-4e3f-9a5c-de689c253ef4"));
                result.set("devices", Json.toJson(devices));
            } else {
                DeviceType deviceType =  DeviceType.findByNameAndOrganization("WRRKRR_READER", organization);
                result.set("devices", Json.toJson(Device.findAllByDeviceTypeOrganization(deviceType, organization)));
            }
            return ok(result);
        } else {
            return badRequest();
        }
    }

}