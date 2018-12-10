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

import cmodels.Zone;
import cmodels.ZoneRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import models.*;
import org.ektorp.DbAccessException;
import play.Logger;
import utils.Utils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static models.DataMigration.DEFAULT_TEST_PASSWORD;
import static utils.GoogleAuthenticator.getRandomSecretKey;

public class SeedOtacos {

    private static String OUTSTANING_TACOS_DOMAIN = "ot.ht";

    public static void createOrganization(){
        // Outstanding Tacos Tenancy
        Organization o = new Organization("OUTSTANDING TACOS INC.", "otacos");
        o.setEmailDomain(OUTSTANING_TACOS_DOMAIN);
        License l = o.getLicense();
        l.setClient(true);
        o.setApproved(true);
        o.setEnabled(true);
        o.save();
        Bootstrap.organization(o);

    }

    public static void createTestUsers() {
        Organization organization =  Organization.findByEmailDomain(OUTSTANING_TACOS_DOMAIN);
        Place  test1UserPlace = new Place("home", PlaceType.findByName("HOME", organization), organization);
        test1UserPlace.save();
        User test1User = new User("Taco", "Sauce", "test@" + organization.getEmailDomain(), getRandomSecretKey(), test1UserPlace, organization);
        Place  test2UserPlace = new Place("home", PlaceType.findByName("HOME", organization), organization);
        test2UserPlace.save();
        User test2User = new User("Taco", "Tortilla", "test2@" + organization.getEmailDomain(), getRandomSecretKey(), test2UserPlace, organization);

        try {
            test1User.setStrongPassword(DEFAULT_TEST_PASSWORD);
            test2User.setStrongPassword(DEFAULT_TEST_PASSWORD);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        test1User.setConfirmed(true);
        test1User.setSuperadmin(false);
        test1User.setAdmin(true);
        test1User.setRole(Role.findByName("Vendor", organization));
        test1User.save();

        test2User.setConfirmed(true);
        test2User.setSuperadmin(false);
        test2User.setAdmin(true);
        test2User.setRole(Role.findByName("Vendor", organization));
        test2User.save();
    }

    public static void createSampleProjectAndTasksInOtacosTenant() {
        Organization organization =  Organization.findByEmailDomain(OUTSTANING_TACOS_DOMAIN);
        // Fetch bootstrap info
        // Organization organization = Organization.findByEmailDomain(DataMigration.OUTSTANING_TACOS_DOMAIN);
        User testUser = User.findByEmail("test@" + organization.getEmailDomain(), organization);
        User test2User = User.findByEmail("test2@" + organization.getEmailDomain(), organization);
        Set<User> participants = new HashSet<>();
        participants.add(testUser);
        participants.add(test2User);

        // Create Test Clean Up project
        Html descriptionHtml = new Html("<ul><li><span style=\"color: inherit;\">Clean up Bathroom,&nbsp;</span><br></li><li><span style=\"color: inherit;\">Clean up Kitchen,</span><br></li><li><span style=\"color: inherit;\">Clean up Tables</span><br></li></ul>", organization);
        descriptionHtml.save();
        Project projectOne = new Project("Cleanup store before closing.",
                descriptionHtml,
                organization,
                testUser);
        projectOne.save();
        // Create project cycle.
        String priority = "MEDIUM";
        String status = "IN_REVIEW";
        ProjectCycle cycle = new ProjectCycle(ProjectType.findByName("BASIC", organization),
                ProjectStatus.findByName(status, organization),
                ProjectPriority.findByName(priority, organization),
                organization,
                testUser);
        cycle.setParticipants(participants);
        cycle.setTotalBudget(600.00);
        cycle.setTotalBudgetedHours(30.0);

        // Create Tasks for projectOne
        Task t1 = new Task("Clean Bathroom", 1.0,
                TaskType.findByName("BASIC", organization),
                TaskStatus.findByName("OPEN", organization),
                TaskPriority.findByName("HIGH", organization),
                organization);
        t1.setParticipants(participants);
        t1.save();

        Set<Task> tasks = new HashSet<>();
        tasks.add(t1);

        cycle.setTasks(tasks);
        cycle.save();

        // Set Cycle to project
        Set<ProjectCycle> cycles = new HashSet<>();
        cycles.add(cycle);
        projectOne.setCycles(cycles);
        projectOne.update();

        // Create Test Store Open Project
        Html descriptionHtmlOpenProject = new Html("<ul><li>Open doors</li><li><span style=\"color: inherit;\">Check if Tables are Clean,</span><br></li><li>Check<span style=\"color: inherit;\">&nbsp;</span>kitchen<span style=\"color: inherit;\">&nbsp;Inventory,</span><br></li><li><span style=\"color: inherit;\">Check shift schedule,</span></li><li>Check WrrKrr</li></ul>", organization);
        descriptionHtmlOpenProject.save();
        Project projectTwo = new Project("Open Store.",
                descriptionHtmlOpenProject,
                organization,
                testUser);
        projectTwo.save();
        // Create project cycle.
        priority = "MEDIUM";
        status = "TODO";
        cycle = new ProjectCycle(ProjectType.findByName("BASIC", organization),
                ProjectStatus.findByName(status, organization),
                ProjectPriority.findByName(priority, organization),
                organization,
                testUser);
        cycle.setParticipants(participants);
        cycle.setTotalBudget(100.0);
        cycle.setTotalBudgetedHours(10.0);

        // Create Tasks for projectTwo
        Task t2 = new Task("Open Store and perform Checks", 1.0,
                TaskType.findByName("ADVANCED", organization),
                TaskStatus.findByName("OPEN", organization),
                TaskPriority.findByName("CRITICAL", organization),
                organization);
        t2.setParticipants(participants);
        t2.save();
        Task t3 = new Task("Check Inventory", 1.0,
                TaskType.findByName("REPEATING", organization),
                TaskStatus.findByName("TODO", organization),
                TaskPriority.findByName("HIGH", organization),
                organization);
        t3.setParticipants(participants);
        t3.save();
        tasks = new HashSet<>();
        tasks.add(t2);
        tasks.add(t3);

        cycle.setTasks(tasks);
        cycle.save();

        // Set Cycle to project
        cycles = new HashSet<>();
        cycles.add(cycle);

        projectTwo.setCycles(cycles);
        projectTwo.update();
    }

    public static void createSampleFloorPlansAndZones() {
        Organization organization =  Organization.findByEmailDomain(OUTSTANING_TACOS_DOMAIN);
        // Fetch bootstrap info
        User adminUser = User.findByEmail("test@" + organization.getEmailDomain(), organization);
        // Create an image for floor
        TypeImage floorImage = new TypeImage(organization, "https://s3.amazonaws.com/wrrkrr-development/floors/sample_floorplan.png");
        floorImage.setW(842);
        floorImage.setH(589);
        floorImage.save();

        // Create Floor
        Floor f = new Floor("Piscatway Store", organization, adminUser);
        Set<Device> floorSensors = new HashSet<>();
        Device floorSensor = Device.findByExternalDeviceId("9KT3-SRBQB");
        floorSensors.add(floorSensor);
        f.setSensors(floorSensors);
        f.setImage(floorImage);
        f.setBy(adminUser);
        f.save();

        // Update place of sensor
        // Get Cartesian X and Y COORDINATES.
        ////////////////////////////////////////////////
        ////   IMPORTANT: TRIANGULATION COORDINATES. ///
        ////////////////////////////////////////////////
        Place p = new Place("Place of Beacon",
                PlaceType.findByName("Marker", f.getOrganization()),
                f.getOrganization());
        p.setCartesianX(-178);
        p.setCartesianY(215);
        p.save();
        floorSensor.updateCurrentPlace(p);
        floorSensor.save();

        // Create Zones
        String bedroomZonePoints = "{\"points\":[[160, 148], [161, 102], [198, 51], [293, 51], [334, 105], [333, 183], [351, 183], [352, 319], [101, 321], [100, 148]]}";
        String entranceZonePoints = "{\"points\":[[357, 556], [356, 460], [493, 385], [493, 418], [555, 418], [556, 483], [590, 482], [591, 555]]}";

        // Add Device state
        ZoneRepository zoneRepository = new ZoneRepository(CouchDB.db);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Deserialize and save a Map of zone points
            Map<String, Object> zonePoints = objectMapper.readValue(bedroomZonePoints, new TypeReference<Map<String, Object>>() {
            });
            Zone zone = new Zone();
            zone.setId(UUID.randomUUID().toString());
            zone.setFloorId(f.getId());
            zone.setName("Kitchen Zone");
            zone.setData(zonePoints);
            zoneRepository.add(zone);
            // Deserialize and save a Map of zone points
            zonePoints = objectMapper.readValue(entranceZonePoints, new TypeReference<Map<String, Object>>() {
            });
            zone = new Zone();
            zone.setId(UUID.randomUUID().toString());
            zone.setFloorId(f.getId());
            zone.setName("Entrance Zone");
            zone.setData(zonePoints);
            zoneRepository.add(zone);
        } catch (NullPointerException e) {
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

    }

    public static void createFakeUsers() {
        Organization organization =  Organization.findByEmailDomain(OUTSTANING_TACOS_DOMAIN);
        Faker faker =  new Faker();
        for(int i=0; i<10; i++){
            Place  testUserPlace = new Place("home", PlaceType.findByName("HOME", organization), organization);
            testUserPlace.save();
            User testUser = new User(faker.name().firstName(), faker.name().lastName(), faker.name().username() + "@" + organization.getEmailDomain(), getRandomSecretKey(), testUserPlace, organization);
            try {
                testUser.setStrongPassword(DEFAULT_TEST_PASSWORD);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            testUser.setConfirmed(true);
            testUser.setSuperadmin(false);
            testUser.setAdmin(false);
            testUser.setRole(Role.findByName("Vendor", organization));
            testUser.save();
        }
    }

    public static void createDemoDevices(){
        // Outstanding Tacos domain
        Organization outstandingTacosOrganization = Organization.findByEmailDomain(OUTSTANING_TACOS_DOMAIN);
        SeedDevices.setTestDevices(outstandingTacosOrganization);
    }

    public static void createDemoShifts(){
        // Fetch bootstrap info
        Organization organization = Organization.findByEmailDomain(OUTSTANING_TACOS_DOMAIN);
        User adminUser = User.findByEmail("test@" + organization.getEmailDomain(), organization);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2018);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        Date start = Utils.atStartOfDay(cal.getTime());

        //set date to last day of 2014
        cal.set(Calendar.YEAR, 2018);
        cal.set(Calendar.MONTH, 7); // 11 = december
        cal.set(Calendar.DAY_OF_MONTH, 31); // new years eve if 31st of december and so on.
        Date end = Utils.atEndOfDay(cal.getTime());

        //Iterate through the two dates
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(start);
        while (gcal.getTime().before(end)) {
            gcal.add(Calendar.DAY_OF_YEAR, 1);
            Timestamp startTime = new Timestamp(gcal.getTime().getTime());
            createShiftWithStartTime(adminUser, startTime);
        }
    }

    public static void createShiftWithStartTime(User user, Timestamp startTime) {

        // CLOCK IN
        Shift clockIn = new Shift(user, Shift.ShiftEvent.START, startTime);
        clockIn.setWhenCreated(startTime);
        clockIn.setWhenUpdated(startTime);
        clockIn.save();

        // Create some delay for shift end.
        int min = 30;
        int max = 480;
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int delayMinutes = ThreadLocalRandom.current().nextInt(min, max + 1);
        long time = startTime.getTime();
        long newTime = time + delayMinutes * 60 * 1000;
        Timestamp endTime = new Timestamp(newTime);

        // CLOCK OUT
        Shift ClockOut = new Shift(user, Shift.ShiftEvent.END, endTime);
        ClockOut.setWhenCreated(endTime);
        ClockOut.setWhenUpdated(endTime);
        ClockOut.save();
    }




}
