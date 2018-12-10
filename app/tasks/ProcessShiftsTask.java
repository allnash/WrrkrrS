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

package tasks;

import akka.actor.ActorSystem;
import com.google.inject.Inject;
import models.Shift;
import models.ShiftReport;
import models.User;
import play.Logger;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import utils.Utils;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.toIntExact;
import static utils.Utils.compareTwoTimeStamps;

public class ProcessShiftsTask {

    /*
     *
     *  Runs a Auto update Task to contact the server every minute.
     *   There is an initial 50 second delay to kick off the task.
     * */

    private final ActorSystem actorSystem;
    private final ExecutionContext executionContext;

    @Inject
    public ProcessShiftsTask(ActorSystem actorSystem, ExecutionContext executionContext) {
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;
        this.initialize();
    }

    private void initialize() {
        this.actorSystem.scheduler().schedule(
                Duration.create(30, TimeUnit.SECONDS), // initialDelay
                Duration.create(2, TimeUnit.MINUTES), // interval
                this::processShifts,
                this.executionContext
        );
    }

    private void processShifts() {
        Logger.info("Process shifts task started.");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2018);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        Date start = Utils.atStartOfDay(cal.getTime());

        //set date to last day of 2014
        cal.set(Calendar.YEAR, 2018);
        cal.set(Calendar.MONTH, 7); // 11 = december
        cal.set(Calendar.DAY_OF_MONTH, 31); // new years eve if 31st of december and so on.
        Date end = Utils.atEndOfDay(cal.getTime());

        List<User> users = User.all();
        for (User user : users) {
            //Iterate through the two dates
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTime(start);
            while (gcal.getTime().before(end)) {
                // Get DAY start time
                Timestamp startTime = new Timestamp(gcal.getTime().getTime());
                GregorianCalendar gcalEnd = new GregorianCalendar();
                gcalEnd.setTime(gcal.getTime());
                gcalEnd.add(Calendar.DAY_OF_YEAR, 1);
                // Get DAY start time and End Time
                Timestamp endTime = new Timestamp(gcalEnd.getTime().getTime());
                // Create shift report or Find existing one.
                ShiftReport report;
                report = ShiftReport.findByUserForDate(user, startTime);
                if (report == null) {
                    report = new ShiftReport(user, startTime, 0, 0);
                    report.save();
                }
                // Find out unprocessed Shifts for user
                List<Shift> unprocessedShifts = Shift.findUnprocessedByUser(user, startTime, endTime);
                if (unprocessedShifts.size() > 0) {
                    // If there is only one shift then just clock 8.
                    if (unprocessedShifts.size() == 1) {
                        report.setClockedHours(report.getClockedHours() + 8);
                        report.setClockedMinutes(report.getClockedMinutes());
                        report.save();
                    } else {
                        // Iterate and find SHIFT END and add time.
                        Shift startShift = unprocessedShifts.get(0);
                        for (ListIterator<Shift> it = unprocessedShifts.listIterator(); it.hasNext(); ) {
                            Shift current = it.next();
                            if (current.getEvent().equals(Shift.ShiftEvent.END)) {
                                long diff = compareTwoTimeStamps(current.getWhenReceived(), startShift.getWhenReceived());
                                int hours = toIntExact(diff / 60);
                                int mins = toIntExact((diff - (hours * 60)));
                                report.setClockedHours(report.getClockedHours() + hours);
                                report.setClockedMinutes(report.getClockedMinutes() + mins);
                                report.save();
                            }
                            current.setProcessed(true);
                            current.save();
                        }
                    }
                }
                // Add a day and continue through the while loop.
                gcal.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
        Logger.info("Process shifts task ended.");
    }

}