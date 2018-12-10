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
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.Finder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityType extends BaseModel {

    public static ConcurrentHashMap<String, ActivityType> types = new ConcurrentHashMap<String, ActivityType>();

    public String name;
    public String description;

    @OneToOne
    @JsonProperty("image")
    public TypeImage image;

    // YES MEANS 1 | NO MEANS 0
    @JsonProperty("available_publicly")
    public Boolean availablePublicly;

    @ManyToOne
    public Organization organization;

    public ActivityType() {

    }

    public ActivityType(String name, String description) {
        this.name = name.toUpperCase();
        this.description = description;
        this.availablePublicly = true;
        this.save();
    }


    public static ActivityType of(String string) {
        if (types.containsKey(string)) {
            return types.get(string);
        } else return null;
    }


    public static ActivityType add(String name, String description) {
        ActivityType d = new ActivityType(name, description);
        d.save();
        ActivityType.load();
        return d;
    }

    public static boolean load() {
        for (ActivityType d : find.all()) {
            types.put(d.name, d);
        }
        if (types.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    public static Finder<String, ActivityType> find = new Finder<>(ActivityType.class);

    public static List<ActivityType> all() {
        return find.all();
    }

    public static ActivityType findById(Long id) {
        return find.query().where().eq("id", id).findOne();
    }

    public static ActivityType findByName(String name) {
        return find.query().where().eq("name", name).findOne();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailablePublicly() {
        return availablePublicly;
    }

    public void setAvailablePublicly(Boolean availablePublicly) {
        this.availablePublicly = availablePublicly;
    }

    public TypeImage getImage() {
        return image;
    }

    public void setImage(TypeImage image) {
        this.image = image;
    }


}
