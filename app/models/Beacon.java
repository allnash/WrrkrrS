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

import cmodels.DeviceState;
import cmodels.DeviceStateRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import db.CouchDB;
import org.ektorp.DbAccessException;
import play.Logger;
import utils.RandomString;
import utils.SecretMaker;

import java.io.IOException;
import java.util.*;

/**
 * Created by nashgadre on 11/4/16.
 */
public class Beacon extends Device {

    public Beacon(){

    }

    public static List<Beacon> findAllBeaconsByOwnerId(User owner) {
        List<Device> beaconDevices = find.query().where().eq("owner_id", owner.getId()).findList();
        List<Beacon> beacons = new ArrayList<>();
        for (Device b : beaconDevices) {
            beacons.add((Beacon) b);
        }
        return beacons;
    }

    public static Beacon findByBeaconId(String beaconId) {
        return (Beacon) findByDeviceId(beaconId);
    }

    public static Boolean isPresent(String beaconId) {
        if (findByExternalDeviceId(beaconId) != null)
            return true;
        else
            return false;
    }

    public static List<Beacon> loadBeacons(JsonNode beaconsJsonArray, User owner, Organization manufacturer) {
        if (beaconsJsonArray.isArray()) {
            ArrayNode jsonArray = (ArrayNode) beaconsJsonArray;
            for (JsonNode beaconJson : jsonArray) {

                String gimbalBeaconId = beaconJson.get("factory_id").asText();
                String beaconName = beaconJson.get("name").asText();

                if (!Beacon.isPresent(gimbalBeaconId)) {

                    Device b = null;
                    Place p = null;

                    if (PlaceType.findByName("GIMBAL_BEACON_PLACE", owner.getOrganization()) == null)
                        PlaceType.add("GIMBAL_BEACON_PLACE", "GIMBAL_BEACON_PLACE", owner.getOrganization());


                    String lat = beaconJson.get("gimbal_latitude").asText();
                    String lon = beaconJson.get("gimbal_longitude").asText();
                    String name = beaconJson.get("name").asText();
                    String hardware = beaconJson.get("hardware").asText();
                    String batteryLevel = beaconJson.get("battery_level").asText();
                    String maxBatteryLevel = "HIGH";

                    p = new Place(beaconName, PlaceType.findByName("GIMBAL_BEACON_PLACE", owner.getOrganization()), owner.getOrganization());
                    p.setLat(lat);
                    p.setLon(lon);
                    p.save();

                    String becaonDeviceSecret = SecretMaker.hashMac(UUID.randomUUID().toString(), new RandomString(12).nextString());

                    b = new Device(name, becaonDeviceSecret, owner, DeviceType.findByNameAndOrganization("GIMBAL_BEACON", owner.getOrganization()), null, manufacturer);

                    b.setExternalId(gimbalBeaconId);
                    b.setModel(hardware);
                    b.setMaximumBatteryLevel(maxBatteryLevel);
                    b.setCurrentbatteryLevel(batteryLevel);
                    b.setCurrentPlace(p);
                    Set<Place> placesList =  new HashSet<>();
                    placesList.add(p);
                    b.setPlaces(placesList);
                    b.save();

                    // Add Device state
                    DeviceStateRepository stateRepository = new DeviceStateRepository(CouchDB.db);
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        // Deserialize and save a Map
                        String textValue = objectMapper.writeValueAsString(beaconJson);
                        Map<String, Object> data = objectMapper.readValue(textValue, new TypeReference<Map<String, Object>>(){});
                        DeviceState beaconState = new DeviceState();
                        beaconState.setDeviceId(b.getId());
                        beaconState.setData(data);
                        stateRepository.add(beaconState);
                        b.setCurrentStateId(beaconState.getId());
                        b.save();

                    } catch (NullPointerException e){
                        e.printStackTrace();
                    } catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DbAccessException e) {
                        Logger.error("Is your couch db server down?? ensure it is up and that the credentials in conf file are correct.");
                        e.printStackTrace();
                    }


                    Logger.info("Beacon Added with Internal ID - " + b.getId() + " and " + "External Id - " + b.getExternalId());

                }
            }
        }
        return null;
    }

}
