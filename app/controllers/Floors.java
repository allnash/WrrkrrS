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

import cmodels.Zone;
import cmodels.ZoneRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;
import db.CouchDB;
import models.*;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import utils.Utils;

import java.util.*;

/*
 * This controller contains Organizations app common logic
 */

public class Floors extends BaseController {


    @Security.Authenticated(Secured.class)
    public Result post(String organizationId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);

        // Only allow users with Role READ_WRITE to access post method.
        if (!myUser.getRole().getDevicesModuleAccess().equals(Role.RoleAccess.READ_WRITE)) {
            return badRequest();
        }

        JsonNode json = request().body().asJson();
        String name = json.get("name").asText();

        Floor newFloor = new Floor(name, myUser.getOrganization(), myUser);
        newFloor.save();
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("floor", Json.toJson(newFloor));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result get(String floorId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Floor myFloor = Floor.findById(floorId);
        if (myFloor.getOrganizationId().equals(myOrg.getId())) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("floor", Json.toJson(myFloor));
            return ok(result);
        } else if (myFloor.getOrganization().equals(myUser.getOrganization())) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("floor", Json.toJson(myFloor));
            return ok(result);
        } else {
            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result getFloorZones(String floorId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);

        // Only allow users with Role READ READ_WRITE to access post method.
        if (myUser.getRole().getDevicesModuleAccess().equals(Role.RoleAccess.NONE)) {
            return badRequest();
        }

        Floor myFloor = Floor.findById(floorId);
        if (myFloor.getBy().equals(myUser)) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            ZoneRepository floorZoneRepository = new ZoneRepository(CouchDB.db);
            List<Zone> zones = floorZoneRepository.findByFloorId(myFloor.getId());
            result.set("floor_zones", Json.toJson(zones));
            return ok(result);
        } else if (myFloor.getOrganization().equals(myUser.getOrganization())) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            ZoneRepository floorZoneRepository = new ZoneRepository(CouchDB.db);
            List<Zone> zones = floorZoneRepository.findByFloorId(myFloor.getId());
            result.set("floor_zones", Json.toJson(zones));
            return ok(result);
        } else {
            return badRequest();
        }
    }


    @Security.Authenticated(Secured.class)
    public Result getFloorSensors(String floorId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);

        // Only allow users with Role READ READ_WRITE to access post method.
        if (myUser.getRole().getDevicesModuleAccess().equals(Role.RoleAccess.NONE)) {
            return badRequest();
        }

        Floor myFloor = Floor.findById(floorId);
        if (myFloor.getBy().equals(myUser)) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("floor_sensors", Json.toJson(myFloor.getSensors()));
            return ok(result);
        } else if (myFloor.getOrganization().equals(myUser.getOrganization())) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("floor_sensors", Json.toJson(myFloor.getSensors()));
            return ok(result);
        } else {
            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result putFloorZones(String floorId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        JsonNode json = request().body().asJson();

        // Only allow users with Role READ_WRITE to access post method.
        if (!myUser.getRole().getDevicesModuleAccess().equals(Role.RoleAccess.READ_WRITE)) {
            return badRequest();
        }

        if (!json.isArray()) {
            return badRequest();
        }

        // Get Floor and its current list of zones
        Floor myFloor = Floor.findById(floorId);
        ZoneRepository floorZoneRepository = new ZoneRepository(CouchDB.db);
        List<Zone> zones = floorZoneRepository.findByFloorId(myFloor.getId());
        // Map them into a HashMap: <ID, ZONE>
        // Add to submitted Zone Maps
        Map<String, Zone> submittedZonesMap = new HashMap<>();

        for (final JsonNode objNode : json) {
            // Parse through data and delete all old stuff.
            Zone submittedZone = Json.fromJson(objNode, Zone.class);
            submittedZonesMap.put(submittedZone.getId(), submittedZone);
        }

        // Delete missing zones
        for (Zone zone : zones) {
            if (!submittedZonesMap.containsKey(zone.getId())) {
                floorZoneRepository.remove(zone);
            }
        }
        // Reload data from CouchDb
        zones = floorZoneRepository.findByFloorId(myFloor.getId());
        // Map them into a HashMap: <ID, ZONE>
        // Add to Existing Zone Maps
        Map<String, Zone> zonesMaps = new HashMap<>();
        for (Zone zone : zones) {
            zonesMaps.put(zone.getId(), zone);
        }

        // Parse through data and delete all old stuff.
        for (final JsonNode objNode : json) {
            Zone submittedZone = Json.fromJson(objNode, Zone.class);
            if (zonesMaps.containsKey(submittedZone.getId())) {
                Zone zoneToBeUpdated = zonesMaps.get(submittedZone.getId());
                zoneToBeUpdated.setName(submittedZone.getName());
                zoneToBeUpdated.setData(submittedZone.getData());
                floorZoneRepository.update(zoneToBeUpdated);
            } else {
                floorZoneRepository.add(submittedZone);
            }
        }

        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        // ReloadZpones
        zones = floorZoneRepository.findByFloorId(myFloor.getId());
        result.set("floor_zones", Json.toJson(zones));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result putFloorSensors(String floorId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        JsonNode json = request().body().asJson();

        // Only allow users with Role READ_WRITE to access post method.
        if (!myUser.getRole().getDevicesModuleAccess().equals(Role.RoleAccess.READ_WRITE)) {
            return badRequest();
        }

        if (!json.isArray()) {
            return badRequest();
        }

        // Get Floor and its current list of zones
        Floor myFloor = Floor.findById(floorId);
        Set<Device> sensors = new HashSet<>();
        // Parse through data and delete all old stuff.
        for (final JsonNode objNode : json) {
            Device sensor = Device.findByDeviceId(objNode.get("id").asText());
            String placeName = "sensor-" + sensor.getName() + "-on-floor-" + myFloor.getName();
            // Update sensor and save its data
            Place p = new Place(placeName,
                    PlaceType.findByName("Marker", myFloor.getOrganization()),
                    myFloor.getOrganization());
            // Get Cartesian X and Y COORDINATES.
            ////////////////////////////////////////////////
            ////   IMPORTANT: TRIANGULATION COORDINATES. ///
            ////////////////////////////////////////////////
            p.setCartesianX(objNode.get("current_place").get("cartesian_x").asInt());
            p.setCartesianY(objNode.get("current_place").get("cartesian_y").asInt());
            p.save();
            sensor.updateCurrentPlace(p);
            sensor.save();
            sensors.add(sensor);
        }
        myFloor.setSensors(sensors);
        myFloor.save();

        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("floor_sensors", Json.toJson(sensors));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result getOrganizationFloors(String id) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Organization organization = Organization.findById(id);
        JsonNode json = request().body().asJson();
        if (myUser.getSuperadmin()) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("floors", Json.toJson(Floor.findAllByOrganization(organization)));
            return ok(result);
        } else if (myUser.getOrganization().equals(organization)) {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("floors", Json.toJson(Floor.findAllByOrganization(organization)));
            return ok(result);
        } else {
            return badRequest();
        }
    }

    public Result getFloors() {
        if (!Utils.isDebugMode()) {
            return badRequest();
        } else {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("floors", Json.toJson(Floor.all()));
            return ok(result);
        }
    }


    public Result getZones(String floorId) {
        if (!Utils.isDebugMode()) {
            return badRequest();
        } else {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            ZoneRepository floorZoneRepository = new ZoneRepository(CouchDB.db);
            List<Zone> zones = floorZoneRepository.findByFloorId(Floor.findById(floorId).getId());
            result.set("floor_zones", Json.toJson(zones));
            return ok(result);
        }
    }

    public Result getSensors(String floorId) {
        if (!Utils.isDebugMode()) {
            return badRequest();
        } else {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("floor_sensors", Json.toJson(Floor.findById(floorId).getSensors()));
            return ok(result);
        }
    }

    public Result postTrilaterate(String floorId) {
        if (!Utils.isDebugMode()) {
            return badRequest();
        } else {
            double[][] positions = new double[][]{{5.0, -6.0}, {13.0, -15.0}, {21.0, -3.0}, {12.4, -21.2}};
            double[] distances = new double[]{8.06, 13.97, 23.32, 15.31};

            try {
                NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
                LeastSquaresOptimizer.Optimum optimum = solver.solve();

                // the answer
                double[] centroid = optimum.getPoint().toArray();

                // error and geometry information; may throw SingularMatrixException depending the threshold argument provided
                RealVector standardDeviation = optimum.getSigma(0);
                RealMatrix covarianceMatrix = optimum.getCovariances(0);
            } catch (Exception e) {
                Logger.info("Unable to multitrilaterate");
            }

            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("floor_sensors", Json.toJson(Floor.findById(floorId).getSensors()));
            return ok(result);
        }
    }

}