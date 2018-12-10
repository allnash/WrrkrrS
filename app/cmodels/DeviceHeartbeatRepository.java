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

package cmodels;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeviceHeartbeatRepository extends CouchDbRepositorySupport<DeviceHeartbeat> {

    @Autowired
    public DeviceHeartbeatRepository(CouchDbConnector db) {
        super(DeviceHeartbeat.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View( name="all", map = "function(doc) { if (doc.type == 'DEVICE_HEARTBEAT') { emit(doc.dateCreated, doc._id) } }")
    public List<DeviceHeartbeat> getAll() {
        ViewQuery q = createQuery("all").descending(true);
        return db.queryView(q, DeviceHeartbeat.class);
    }

    @GenerateView
    public List<DeviceHeartbeat> findByDeviceId(String deviceId) {
        return queryView("by_deviceId", deviceId);
    }
}