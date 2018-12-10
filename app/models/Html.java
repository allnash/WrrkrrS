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

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.List;
import java.util.Map;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Html extends BaseTenantModel {

    /**
     * HTML Table, Tag escaped data field.
     */

    @Column(columnDefinition = "MEDIUMTEXT")
    public String data;
    public String notes;

    public Html() {
    }

    public Html(String data, Organization o) {
        this.data = data.replaceAll("(?i)<(/?script[^>]*)>", "&lt;$1&gt;");
        this.organization = o;
    }

    public static Finder<String, Html> find = new Finder<>(Html.class);

    public static List<Html> all(Map parameterMap) {
        if (parameterMap != null)
            return find.query().where().allEq(parameterMap).findList();
        else
            return null;
    }

    public static Html findById(String id) {
        return find.query().where().eq("id", id).findOne();
    }

    public String getNotes() {
        if (notes != null) {
            return notes.replaceAll("(?i)<(/?script[^>]*)>", "&lt;$1&gt;");
        } else {
            return notes;
        }
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getData() {
        if (data != null) {
            return data.replaceAll("(?i)<(/?script[^>]*)>", "&lt;$1&gt;");
        } else {
            return data;
        }
    }

    public void setData(String data) {
        this.data = data;
    }
}

