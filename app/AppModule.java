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

import com.google.inject.AbstractModule;

import java.time.Clock;
import java.util.TimeZone;

import play.Logger;
import services.ApplicationTimer;
import services.AtomicCounter;
import services.Counter;
import system.ApplicationStart;
import system.ApplicationStop;
import tasks.ProcessSightingsTask;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 * <p>
 * Play will automatically use any class called `AppModule` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class AppModule extends AbstractModule {

    public static String domain = "";

    @Override
    public void configure() {

        // FORCE default timezone as UTC.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        // Use the system clock as the default implementation of Clock
        bind(Clock.class).toInstance(Clock.systemDefaultZone());
        // Ask Guice to create an instance of ApplicationTimer when the
        // application starts.
        bind(ApplicationTimer.class).asEagerSingleton();
        // Set AtomicCounter as the implementation for Counter.
        bind(Counter.class).to(AtomicCounter.class);
        // Process sighting Task
        bind(ProcessSightingsTask.class).asEagerSingleton();
        // TODO: disabling because of Broken logic/
        // Process Shifts Task
        // bind(ProcessShiftsTask.class).asEagerSingleton();
        // Process Sightings into Visits Task
        // bind(ProcessVisitsTask.class).asEagerSingleton();

        // Applications Starts
        Logger.info("Binding application start");
        bind(ApplicationStart.class).asEagerSingleton();

        Logger.info("Binding application stop");
        bind(ApplicationStop.class).asEagerSingleton();
    }

}
