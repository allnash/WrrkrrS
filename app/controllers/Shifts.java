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
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gadre on 05/12/2018.
 */

public class Shifts extends BaseController {

    /**
     * JSON API to log a Shift for session
     *
     * @return Result with JSON String ->
     */
    public Result event() {
        ObjectNode result = Json.newObject();

        JsonNode json = request().body().asJson();
        String sessionId = json.findPath("session_id").textValue();
        String deviceId = json.findPath("device_id").textValue();
        JsonNode shiftData = json.findPath("shift_data");
        String eventType = shiftData.findPath("event_type").textValue();

        if (sessionId == null || eventType == null || shiftData == null) {
            return badRequest();
        }

        Session mySession = Session.findBySessionId(sessionId);
        Organization organization = mySession.getOrganization();
        User myUser = User.findByEmail(mySession.getEmail(), organization);
        Device myDevice = null;
        if (deviceId != null) {
            myDevice = Device.findByDeviceId(deviceId);
        }

        /*
         * Get the User associated with visit
         */

        Shift shift = new Shift(myUser, Shift.ShiftEvent.valueOf(eventType), Utils.getCurrentTimestamp());
        shift.setDevice(myDevice);
        shift.setUser(myUser);
        String lat = shiftData.findPath("lat").textValue();
        if (lat != null) {
            shift.setLat(lat);
        }
        String lon = shiftData.findPath("lon").textValue();
        if (lon != null) {
            shift.setLon(lon);
        }
        shift.save();

        result.put("success", "ok");
        result.put("status", "ok");
        result.set("shift", Json.toJson(shift));
        Logger.info("Shift event logged for user  - " + myUser.getId());
        return ok(result);
    }

    public Result getShiftReport() {
        Organization myOrg = organization();
        ObjectNode result = Json.newObject();
        List<Object> data = new ArrayList<>();
        List<ShiftReport> reportList = ShiftReport.findAll(myOrg);
        for (ShiftReport report : reportList) {
            Map<String, Object> element =  new HashMap<>();
            element.put("key", report.getForDate().toString());
            element.put("value", (float) report.getClockedHours() + ((float) report.getClockedMinutes() / 60));
            data.add(element);
        }
        result.put("success", "ok");
        result.put("status", "ok");
        result.set("shift_report", Json.toJson(data));
        return ok(result);
    }
}
