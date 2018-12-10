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
public class ZoneRepository extends CouchDbRepositorySupport<Zone> {

    @Autowired
    public ZoneRepository(CouchDbConnector db) {
        super(Zone.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View( name="all", map = "function(doc) { if (doc.type == 'ZONE') { emit(doc.dateCreated, doc._id) } }")
    public List<Zone> getAll() {
        ViewQuery q = createQuery("all").descending(true);
        return db.queryView(q, Zone.class);
    }

    @GenerateView
    public List<Zone> findByFloorId(String floorId) {
        return queryView("by_floorId", floorId);
    }

}