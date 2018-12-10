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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.Index;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectTag extends Model {
    @Id
    @Constraints.Required
    @Formats.NonEmpty
    @Index
    public String text;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    public Set<Project> projects;

    @CreatedTimestamp
    @JsonProperty("when_created")
    Timestamp whenCreated;

    @ManyToOne
    public Organization organization;

    @JsonIgnore
    public Timestamp getWhenCreated() {
        return whenCreated;
    }

    public void setWhenCreated(Timestamp whenCreated) {
        this.whenCreated = whenCreated;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void save() {
        super.save();
        Logger.info(this.getClass().getCanonicalName() + " -  saved at " + Calendar.getInstance().getTime().toString());
    }

    public static Finder<String, ProjectTag> find = new Finder<>(ProjectTag.class);

    public static ProjectTag findByText(String text) {
        return find.query().where().eq("text", text).findOne();
    }

    public static ProjectTag findByText(String text, Organization organization) {
        return find.query().where()
                .eq("text", text)
                .eq("organization_id", organization.getId()).findOne();
    }


    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public static List<ProjectTag> all(String text, Organization organization) {
        if (text != null) {
            return find.query().where()
                    .eq("organization_id", organization.getId())
                    .like("text", "%" + text + "%").findList();
        } else {
            return new ArrayList<>();
        }
    }
}

