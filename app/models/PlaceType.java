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

package models;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.ebean.Finder;
import play.Logger;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"organization_id", "name"})
)
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceType extends BaseTypeModel {

    public static ConcurrentHashMap<String, PlaceType> types = new ConcurrentHashMap<String, PlaceType>();

    public PlaceType() {

    }

    public PlaceType(String name, String description, Organization organization) {
        this.name = name.toUpperCase();
        this.description = description;
        this.visibility = Visibility.PRIVATE;
        this.organization = organization;
        this.save();
    }


    public static PlaceType add(String name, String description, Organization organization) {
        PlaceType d = new PlaceType(name, description, organization);
        d.save();
        return d;
    }

    public static boolean load() {
        for (PlaceType d : find.all()) {
            types.put(d.name, d);
        }
        Logger.info("Reloading Place Types");
        if (types.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    public static Finder<String, PlaceType> find = new Finder<>(PlaceType.class);

    public static List<PlaceType> all() {
        return find.all();
    }

    public static PlaceType findById(Long id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static PlaceType findByName(String name, Organization organization) {
        return find.query().where()
                .eq("name", name)
                .eq("organization_id", organization.getId()).findOne();
    }

    public static void bootstrapPlaceTypesForOrganization(Organization organization){
        /*
        *  Add 5 Place Types for each org that gets created.
        * */
        PlaceType.add("Home", "Home", organization);
        PlaceType.add("Office", "Office", organization);
        PlaceType.add("Store", "Store", organization);
        PlaceType.add("Marker", "Marker", organization);
        PlaceType.add("Other", "Other", organization);
        PlaceType.load();

    }


}
