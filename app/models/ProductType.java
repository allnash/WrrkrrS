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

import javax.persistence.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"organization_id", "name"})
)
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductType extends BaseTypeModel {

    public static ConcurrentHashMap<String, ProductType> types = new ConcurrentHashMap<String, ProductType>();

    public ProductType() {

    }

    public ProductType(String name, String description, Organization organization) {
        this.name = name.toUpperCase();
        this.description = description;
        this.visibility = Visibility.PRIVATE;
        this.organization = organization;
        this.save();
    }


    public static ProductType of(String string) {
        return types.getOrDefault(string, null);
    }


    public static ProductType add(String name, String description, Organization organization) {
        ProductType d = new ProductType(name, description, organization);
        d.save();
        return d;
    }

    public static boolean load() {
        for (ProductType d : find.all()) {
            types.put(d.name, d);
        }
        Logger.info("Reloading ProductTypes");
        if (types.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    public static Finder<Long, ProductType> find = new Finder<>(ProductType.class);

    public static List<ProductType> all() {
        return find.all();
    }

    public static ProductType findById(Long id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static ProductType findByName(String name, Organization organization) {
        return find.query().where()
                .eq("name", name)
                .eq("organization_id", organization.getId()).findOne();
    }

    public static void bootstrapProductTypesForOrganization(Organization organization) {
        /*
         *  Add 5 product types for each org that gets created.
         * */
        ProductType.add("Basic", "Basic product type", organization);
        ProductType.add("Device", "Device product type", organization);
        ProductType.add("Phone", "Phone product type", organization);
        ProductType.add("Hardware", "Hardware product type", organization);
        ProductType.add("Informational", "Informational product type", organization);
        ProductType.load();

    }


}
