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
import models.*;
import play.Logger;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import utils.Utils;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ProcessVisitsTask {

    /*
     *
     *  Runs a Auto update Task to contact the server every minute.
     *   There is an initial 50 second delay to kick off the task.
     * */

    private final ActorSystem actorSystem;
    private final ExecutionContext executionContext;

    @Inject
    public ProcessVisitsTask(ActorSystem actorSystem, ExecutionContext executionContext) {
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;
        this.initialize();
    }

    private void initialize() {
        this.actorSystem.scheduler().schedule(
                Duration.create(100, TimeUnit.SECONDS), // initialDelay
                Duration.create(5, TimeUnit.MINUTES), // interval
                this::processSightingsToVisits,
                this.executionContext
        );
    }

    private void processSightingsToVisits() {
        Logger.info("Process Sightings>Visits task started at." + Utils.getCurrentTimestamp().toString());
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
            // Get 5 MIN start time
            Timestamp startTime = new Timestamp(gcal.getTime().getTime());
            GregorianCalendar gcalEnd = new GregorianCalendar();
            gcalEnd.setTime(gcal.getTime());
            gcalEnd.add(Calendar.MINUTE, 5);
            // Get 5 MIN End Time
            Timestamp endTime = new Timestamp(gcalEnd.getTime().getTime());
            // Create Visit entry.
            for (Sighting deviceSighting : Sighting.findDistinctDevices(startTime, endTime)) {
                List<Sighting> processedSightings = Sighting.findProcessedByUserAndDeviceWithoutVisit(deviceSighting.getSightedDevice(), startTime, endTime);
                if (processedSightings.size() > 0) {
                    // If there is only one shift then just clock 1 entry for 5 mins.
                    Sighting single = processedSightings.get(0);
                    String name = single.getReaderDevice().getName() + " ---saw--> " + single.getSightedDevice().getName();
                    Visit visit = new Visit(name, single.getSightedUser(), Visit.VisitType.NOTYPE, startTime);
                    visit.setWhenEnded(endTime);
                    visit.setPlace(single.getReaderDevice().getCurrentPlace());
                    visit.setEndpointDevice(single.getReaderDevice());
                    visit.setUserDevice(single.getSightedDevice());
                    visit.save();
                    for (Sighting sighting : processedSightings) {
                        sighting.setVisit(visit);
                        sighting.save();
                    }
                }
            }
            // Add 5 mins and continue through the while loop.
            gcal.add(Calendar.MINUTE, 5);
        }
        Logger.info("Process Sightings>Visits task ended at." + Utils.getCurrentTimestamp().toString());
    }

}