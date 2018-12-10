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
import db.SeedDevices;
import models.DataMigration;
import models.Organization;
import models.User;
import play.libs.Json;
import play.mvc.Result;

/*
 * This controller contains Blog app common logic
 */
public class API extends BaseController {

    public Result index() {
        ObjectNode wrapper = Json.newObject();
        Organization organization = organization();
        Organization omegatrace = Organization.findByEmailDomain(DataMigration.OMEGATRACE_DOMAIN);
        Organization manufacturer = Organization.findByEmailDomain(DataMigration.GIMBAL_DOMAIN);
        User adminUser = User.findByEmail("ops@omegatrace.com", omegatrace);
        if(adminUser != null){
            SeedDevices.importGimbalBeaconDevices(adminUser, omegatrace, manufacturer);
        }
        wrapper.put("status", "ok");
        wrapper.put("success","ok");
        if(organization != null){
            wrapper.put("organization_id", organization.getId());
        }
        return ok(wrapper);
    }

    public Result getWorkspace() {
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success","ok");
        if(organization() != null){
            result.put("workspace", organization().getWorkspaceName());
            return ok(result);
        } else {
            return badRequest();
        }
    }

    public Result verifyWorkspace() {
        JsonNode json = request().body().asJson();
        String workspaceName = json.get("name").asText();
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        if(Organization.findByWorkspaceName(workspaceName) != null){
            return ok(result);
        } else {
            return badRequest();
        }
    }

}