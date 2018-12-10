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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

import static controllers.ControllerHelper.buildJsonResponse;

/*
 * This controller contains Organizations app common logic
 */

public class DeviceTypes extends BaseController {


    @Security.Authenticated(Secured.class)
    public Result post(String organizationId) {

        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Organization deviceTypeOrganization;

        if (myUser.getSuperadmin()) {
            deviceTypeOrganization = Organization.findById(organizationId);
        } else {
            deviceTypeOrganization = myUser.getOrganization();
        }

        JsonNode json = request().body().asJson();
        String name = json.get("name").asText();
        String description = "";
        if(json.has("description")){
            json.get("description").asText("");
        }
        String visibility = json.get("visibility").asText();

        if(DeviceType.findByNameAndOrganization(name, deviceTypeOrganization) != null){
            return badRequest(buildJsonResponse("error", "`Name` should be unique. It seems like there is another entry with `" + name + "`."));
        }

        DeviceType deviceType = new DeviceType(name, description, deviceTypeOrganization);
        deviceType.setBy(myUser);
        deviceType.setVisibility(BaseTypeModel.Visibility.valueOf(visibility));
        // Device Type Save
        deviceType.save();
        // IMPORTANT STEP
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("device_type", Json.toJson(deviceType));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result get(String deviceTypeId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        DeviceType myDeviceType = DeviceType.findById(deviceTypeId);
        if (myUser.getSuperadmin()) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("device_type", Json.toJson(myDeviceType));
            return ok(result);
        } else if (myDeviceType.getOrganization().equals(myUser.getOrganization())) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("device_type", Json.toJson(myDeviceType));
            return ok(result);
        } else {
            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result put(String id) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        JsonNode json = request().body().asJson();
        if (myUser.getSuperadmin()) {
            DeviceType submittedDeviceType = Json.fromJson(json, DeviceType.class);
            DeviceType deviceType = DeviceType.findById(id);
            deviceType.setName(submittedDeviceType.name);
            deviceType.setDescription(submittedDeviceType.description);
            deviceType.setVisibility(submittedDeviceType.visibility);
            deviceType.save();
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("device_type", Json.toJson(deviceType));
            return ok(result);
        } else {
            return badRequest();
        }
    }


    @Security.Authenticated(Secured.class)
    public Result putDeviceTypeHtml(String id) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        DeviceType deviceType = DeviceType.findById(id);
        JsonNode json = request().body().asJson();
        if (myUser.getSuperadmin() || myUser.getOrganization().equals(deviceType.getOrganization())){
            DeviceType submittedDeviceType = Json.fromJson(json, DeviceType.class);
            Html deviceTypeHtml = deviceType.getHtml();
            deviceTypeHtml.setData(submittedDeviceType.deviceTypeHtml);
            deviceTypeHtml.save();
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("device_type", Json.toJson(deviceType));
            return ok(result);
        } else {
            return badRequest();
        }
    }


    @Security.Authenticated(Secured.class)
    public Result getDeviceTypeProperties(String deviceTypeId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        DeviceType myDeviceType = DeviceType.findById(deviceTypeId);
        if (myUser.getSuperadmin()) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("device_type_properties", Json.toJson(myDeviceType.getProperties()));
            return ok(result);
        } else if (myDeviceType.getOrganization().equals(myUser.getOrganization())) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("device_type_properties", Json.toJson(myDeviceType.getProperties()));
            return ok(result);
        } else {
            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result getOrganizationDeviceTypes(String id) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Organization organization = Organization.findById(id);
        JsonNode json = request().body().asJson();
        if (myUser.getSuperadmin()) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("device_types", Json.toJson(DeviceType.findAllByOrganization(organization)));
            return ok(result);
        } else if (myUser.getOrganization().equals(organization)) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("device_types", Json.toJson(DeviceType.findAllByOrganization(organization)));
            return ok(result);
        } else {
            return badRequest();
        }
    }

}