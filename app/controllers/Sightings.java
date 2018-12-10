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
import models.Device;
import models.Sighting;
import models.User;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

import java.sql.Timestamp;

/*
 * This controller contains Organizations app common logic
 */

public class Sightings extends BaseController {

    @Security.Authenticated(SecuredApi.class)
    public Result post() {

        ObjectNode result = Json.newObject();
        JsonNode json = request().body().asJson();
        JsonNode sightingsJson = json.get("sightings");

        if(!sightingsJson.isArray()){
            return badRequest();
        } else {
            Logger.info(sightingsJson.size() + " Reader sightings received.");
        }

        for(JsonNode sightingJson: sightingsJson){

            String id = sightingJson.get("id").asText();

            String sightedDeviceId = sightingJson.get("sighted_device_id").asText();

            String readerDeviceId = sightingJson.get("reader_device_id").asText();
            Device readerDevice = Device.findByDeviceId(readerDeviceId);

            String sightedUserId = null;
            if(sightingJson.hasNonNull("sighted_user_id")){
                sightedUserId = sightingJson.get("sighted_user_id").asText();
            }

            String rssi = sightingJson.get("rssi").asText();
            String distance = sightingJson.get("distance").asText();
            String temperature = sightingJson.get("temperature").asText();

            String whenSeen = sightingJson.get("when_seen").asText();
            Timestamp whenSeenTimestamp = new Timestamp(Long.valueOf(whenSeen));

            if(Sighting.findById(id) != null){
                continue;
            }

            // Create sighting record.
            Sighting sighting = new Sighting();
            sighting.setId(id);
            sighting.setSightedDevice(Device.findByDeviceId(sightedDeviceId));
            sighting.setFloor(readerDevice.getCurrentFloor());
            sighting.setReaderDevice(readerDevice);
            sighting.setSightedUser(User.findById(sightedUserId));
            sighting.setRSSI(rssi);
            sighting.setTemperature(temperature);
            sighting.setDistance(distance);
            sighting.setWhenSeen(whenSeenTimestamp);
            sighting.setProcessed(false);
            sighting.setOrganization(readerDevice.getOrganization());
            sighting.save();
        }

        Logger.info(sightingsJson.size() + " Reader sightings loaded.");

        result.put("status", "ok");
        result.put("success", "ok");
        return ok(result);

    }

}