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
import models.Device;
import models.Sighting;
import play.Logger;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import tools.blocks.beacon.GenericBeacon;
import tools.blocks.constants.RPiEmbeddedAntennaConstants;
import tools.blocks.filter.KalmanFilter;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProcessSightingsTask {

    /*
     *
     *  Runs a Auto update Task to contact the server every minute.
     *   There is an initial 50 second delay to kick off the task.
     * */

    private final ActorSystem actorSystem;
    private final ExecutionContext executionContext;

    @Inject
    public ProcessSightingsTask(ActorSystem actorSystem, ExecutionContext executionContext) {
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;
        this.initialize();
    }

    private void initialize() {
        this.actorSystem.scheduler().schedule(
                Duration.create(30, TimeUnit.SECONDS), // initialDelay
                Duration.create(1, TimeUnit.MINUTES), // interval
                this::processSightings,
                this.executionContext
        );
    }

    private void processSightings() {
        Logger.info("Process sightings task started.");
        List<Device> devices = Device.all();
        try {
            for (Device d : devices) {
                List<Sighting> sightingsList = Sighting.findUnprocessedBySightedDevice(d);
                if (sightingsList.size() > 0) {
                    // init kahlman filter
                    KalmanFilter kalmanFilter = new KalmanFilter();
                    // Set generic beacon
                    GenericBeacon genericBeacon = new GenericBeacon(d.getId(), -29);
                    genericBeacon.setMeasurementDeviceConstants(new RPiEmbeddedAntennaConstants());
                    genericBeacon.setRssiFilter(new KalmanFilter());
                    // Process sightings
                    for (Sighting sighting : sightingsList) {
                        int kRssi = (int) kalmanFilter.applyFilter(Integer.valueOf(sighting.getRSSI()) * -1);
                        sighting.setkRssi(String.valueOf(kRssi * -1));
                        genericBeacon.setRssi((double) kRssi * -1);
                        sighting.setkDistance(String.valueOf(genericBeacon.getDistance()));
                        sighting.setProcessed(true);
                        sighting.save();
                    }
                }
            }
        } catch (Exception e){
            Logger.error("Process sightings task did not finish due to an error." + e.getMessage());
        }
        Logger.info("Process sightings task ended.");
    }

    private void processSightingVisits() {
        // Logger.info("Visit processed.");
    }
}