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

package system;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.neovisionaries.oui.Oui;
import com.neovisionaries.oui.OuiCsvParser;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import db.CouchDB;
import db.Seed;
import models.DataMigration;
import play.Environment;
import play.Logger;
import utils.Mailer;
import utils.Utils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by ngadre
 */
@Singleton
public class ApplicationStart {

    @Inject
    public ApplicationStart(Environment environment) {

        Logger.info("Application has started");
        if (environment.isTest()) {
            // your code
        } else {
            Config conf = ConfigFactory.load();

            ////////////////////////////////
            // Load CouchDB / Caches HERE //
            ////////////////////////////////
            CouchDB.configureDb();

            //////////////////////////////
            // Load TYPE CACHED OBJECTS //
            //////////////////////////////

            // TODO: unused for now.

            /////////////////////////////////////////
            // Load Demo Data HERE FOR DEVELOPMENT //
            /////////////////////////////////////////
            if (conf.getString("deployment.instance_type").equals("LOCAL")) {
                Seed.seedDevelopment();
            }

            ////////////////////////////////////////
            // RUN ALL DATA MIGRATIONS AFTER THIS //
            ////////////////////////////////////////
            DataMigration.run_data_mirations();


            /////////////////////////////////////
            // SEND RELEASE EMAIL ON START-UPS //
            /////////////////////////////////////
            if (conf.getString("deployment.instance_type").equals("LIVE")) {
                Logger.info("Detected deploy type - LIVE - Sending Release Email");
                //  Mailer.MailReleaseEmail("releases@omegatrace.com");
            } else {
                Logger.info("Detected deploy type - LOCAL - Skipping Release Email");
            }
        }

        // you can use yourInjectedService here

    }
}