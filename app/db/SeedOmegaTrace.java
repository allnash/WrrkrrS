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

import models.*;
import utils.Utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SeedOmegaTrace {

    public static void createDemoShifts() {

        // Fetch bootstrap info
        Organization organization = Organization.findByEmailDomain(DataMigration.OMEGATRACE_DOMAIN);
        User adminUser = User.findByEmail("ops@" + organization.getEmailDomain(), organization);

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

    public static void createDemoSightings(User user, Device device) {
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
        Random random = new Random();

        // Add sightings
        while (gcal.getTime().before(end)) {
            gcal.add(Calendar.MINUTE, (random.nextInt(1000) + random.nextInt(300)));
            Timestamp startTime = new Timestamp(gcal.getTime().getTime());
            createSightingWithTime(user, startTime, device);
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
        int max = 470;
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

    public static void createSightingWithTime(User user, Timestamp whenSeen, Device sightedDevice) {

        Random rand = new Random();
        // FAKE SIGHTING
        // Create sighting record.
        Sighting sighting = new Sighting();
        sighting.setReaderDevice(Device.findById("1e9810e0-8a2d-4353-90eb-55c03fc84d98"));
        sighting.setSightedUser(user);
        int n = -(rand.nextInt(100) + 35);
        sighting.setRSSI(String.valueOf(n));
        sighting.setSightedDevice(sightedDevice);
        sighting.setTemperature(String.valueOf(20));
        sighting.setWhenSeen(whenSeen);
        sighting.setProcessed(false);
        sighting.setOrganization(user.getOrganization());
        sighting.save();

    }


}
