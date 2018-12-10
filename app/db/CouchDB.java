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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DbAccessException;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import play.Logger;

import java.net.MalformedURLException;

public class CouchDB {

    public static HttpClient httpClient;
    public static CouchDbInstance dbInstance;
    public static CouchDbConnector db;

    public static void configureDb() {

        Config conf = ConfigFactory.load();

        String host = conf.getString("couchdb.host");
        String port = conf.getString("couchdb.port");
        String username = conf.getString("couchdb.username");
        String password = conf.getString("couchdb.password");

        String dbname = conf.getString("couchdb.dbname");

        try {
            httpClient = new StdHttpClient.Builder()
                    .url("http://" + host + ":" + port)
                    .username(username)
                    .password(password)
                    .build();

            dbInstance = new StdCouchDbInstance(httpClient);
            db = new StdCouchDbConnector(dbname, dbInstance);

        } catch (MalformedURLException | DbAccessException e) {
            Logger.error("CouchDB - There was an error establishing a connection, please check");
            Logger.error("CouchDB - Exception - " + e.getMessage());
        }
    }
}
