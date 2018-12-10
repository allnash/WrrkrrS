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

package db;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import models.*;
import play.Logger;
import play.libs.Json;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class SeedDevices {

    public static DeviceType washerDeviceType;
    public static DeviceType refrigeratorDeviceType;
    public static DeviceType beconReader;
    public static DeviceType gimbalBeacon;
    public static DeviceType iphoneDeviceType;

    public static  DeviceTypeProperty turnOn;
    public static  DeviceTypeProperty turnOff;
    public static  DeviceTypeProperty turnOffTwoTimes;
    public static  DeviceTypeProperty gpsOn;
    public static  DeviceTypeProperty gpsOff;
    public static  DeviceTypeProperty beaconSenseOn;
    public static  DeviceTypeProperty beaconSenseOff;
    public static  DeviceTypeProperty beaconSense;

    public static Device gimbalBeaconApartmentA;
    public static Device gimbalBeaconApartmentB;
    public static Device gimbalBeaconFloorOne;

    public static void setDeviceTypeProperties(Organization o) {

        DeviceTypePropertyType action = DeviceTypePropertyType.findByName("Action", o);
        DeviceTypePropertyType behavior = DeviceTypePropertyType.findByName("Behavior", o);
        DeviceTypePropertyType event = DeviceTypePropertyType.findByName("Event", o);

        DeviceTypePropertyStatus live = DeviceTypePropertyStatus.findByName("Live", o);
        DeviceTypePropertyStatus test = DeviceTypePropertyStatus.findByName("Test", o);
        DeviceTypePropertyStatus suspended = DeviceTypePropertyStatus.findByName("Suspended", o);


        turnOn = DeviceTypeProperty.add("TURN_ON", "", action, live, o);
        turnOff = DeviceTypeProperty.add("TURN_OFF", "", action, live, o);
        turnOffTwoTimes = DeviceTypeProperty.add("TURN_OFF_TWO_TIMES", "", action, suspended, o);
        gpsOn = DeviceTypeProperty.add("GPS_ON", "", action, live, o);
        gpsOff = DeviceTypeProperty.add("GPS_OFF", "", action, live, o);
        beaconSenseOn = DeviceTypeProperty.add("BEACON_SENSE_ON", "", behavior, live, o);
        beaconSenseOff = DeviceTypeProperty.add("BEACON_SENSE_OFF", "", behavior, live, o);
        beaconSense = DeviceTypeProperty.add("BEACON_SENSE", "", event, test, o);
    }

    public static void setDeviceTypes(Organization o) {

        refrigeratorDeviceType = DeviceType.add("REFRIGERATOR", "This is a refrigerator with 900 Watt Compressor", o);
        List<DeviceTypeProperty> properties = new ArrayList<>();
        properties.add(turnOn);
        properties.add(turnOff);
        refrigeratorDeviceType.setProperties(properties);
        refrigeratorDeviceType.update();

        washerDeviceType = DeviceType.add("WASHER", "This device type represents a Washing Machine, Front loading, 500 Watt", o);
        properties = new ArrayList<>();
        properties.add(turnOn);
        properties.add(turnOff);
        washerDeviceType.setProperties(properties);
        washerDeviceType.update();


        beconReader = DeviceType.add("BEACON_READER", "Generic Linux Compute with Bluetooth LE that can read beacons.", o);
        properties = new ArrayList<>();
        properties.add(beaconSenseOn);
        properties.add(beaconSenseOff);
        properties.add(beaconSense);
        beconReader.setProperties(properties);
        beconReader.update();

    }

    public static void setTestDevices(Organization organization) {

        setDeviceTypeProperties(organization);
        setDeviceTypes(organization);

        for(User owner : User.findAllByOrganization(organization)){


            /*
             *   DEVICE 1 SET UP
             *
             */
            Device d1 = new Device("Refrigerator (Sample)", "sample_secret", owner, refrigeratorDeviceType, organization, null);

            if(owner.getEmail().equals("ops@omegatrace.com")) {
                d1.id = "29d42e14-5e70-44ad-aade-7b4992614885";
            }
            d1.setEnabled(false);
            d1.save();

            /*
             *   DEVICE 2 S ET UP
             *
             */
            Device d2 = new Device("Raspberry Pi Reader (Sample)", "sample_secret", owner, refrigeratorDeviceType, organization, null);
            if(owner.getEmail().equals("ops@omegatrace.com")) {
                d2.id = "1e9810e0-8a2d-4353-90eb-55c03fc84d98";
            }
            d2.setEnabled(true);
            d2.save();
        }

    }


    public static void importGimbalBeaconDevices(User adminUser, Organization organization, Organization manufacturer) {
        if(!adminUser.getOrganizationId().equals(organization.getId())){
            Logger.error("Gimbal beacons cannot be imported from a user that is not part of the organization that is provided.");
            return;
        }

        gimbalBeacon = DeviceType.add("GIMBAL_BEACON", "Gimbal Bluetooth LE beacons.", adminUser.getOrganization());

        if(gimbalBeacon.getPropertiesCount() == 0){
            List<DeviceTypeProperty> properties;
            properties = new ArrayList<>();
            properties.add(beaconSenseOn);
            properties.add(beaconSenseOff);
            properties.add(beaconSense);
            TypeImage image = new TypeImage(organization, "");
            image.setUrl("img/bluetooth_logo.png");
            image.setW(32);
            image.setH(32);
            image.save();
            gimbalBeacon.setProperties(properties);
            gimbalBeacon.setImage(image);
            gimbalBeacon.update();
        }

        HttpResponse<com.mashape.unirest.http.JsonNode> response = null;
        try {
            response = Unirest.get(Utils.getGimbalUrl()).header("Authorization", "Token " + Utils.getGimbalToken()).asJson();
        } catch (UnirestException e) {
            Logger.error("Error Fetching beacons - " + e.getLocalizedMessage());
        }

        Logger.info("Updating devices from Gimbal");
        Beacon.loadBeacons(Json.parse(response.getBody().toString()), adminUser, manufacturer);
        gimbalBeaconApartmentA = Device.findByExternalDeviceId("1HX7-TZ92W");
        gimbalBeaconApartmentB = Device.findByExternalDeviceId("M6ZX-FKXQN");
        gimbalBeaconFloorOne = Device.findByExternalDeviceId("BC66-RR4A2");
    }

}
