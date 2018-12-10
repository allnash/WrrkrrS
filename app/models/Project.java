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
import utils.Utils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project extends BaseTenantModel {

    /**
     * Project Table
     */

    public String name;
    public Integer localId;
    @Transient
    @JsonProperty("local_name")
    public String localName;
    @OneToOne
    @JsonIgnore
    public Html description;
    @Transient
    @JsonProperty("total_budget")
    public double totalBudget;
    @Transient
    @JsonProperty("total_budgeted_hours")
    public double totalBudgetedHours;
    @Transient
    @JsonProperty("description_html")
    public String descriptionHtml;
    @Transient
    public ProjectType type;
    @Transient
    public ProjectPriority priority;
    @Transient
    public ProjectStatus status;
    @Transient
    @JsonProperty("access_url")
    public String accessUrl;
    @Transient
    @JsonIgnore
    private String route = "/index.html#/index/projects/";
    /*
     *  Transient Variables
     * */
    @Transient
    @JsonProperty("current_cycle")
    public ProjectCycle currentCycle;
    @Transient
    @JsonProperty("progress_percentage")
    public Integer progressPercentage;
    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<ProjectCycle> cycles;
    @ManyToMany
    @JoinTable(
            name = "project_products",
            joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
    public List<Product> products;
    @ManyToOne
    public Organization client;
    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<ProjectTag> tags;


    public Project() {

    }

    public Project(String name, Html description, Organization organization, User by) {
        this.name = name;
        this.description = description;
        this.organization = organization;
        this.by = by;
        List<Project> projects = Project.all(this.organization);
        if (projects.size() > 0) {
            this.localId = projects.get(0).localId + 1;
        } else {
            this.localId = 1;
        }
    }

    public static Finder<Long, Project> find = new Finder<>(Project.class);

    public static List<Project> all(Map parameterMap) {
        if (parameterMap != null)
            return find.query().where().allEq(parameterMap).findList();
        else
            return find.all();
    }

    public static List<Project> all() {
        return find.all();
    }

    public static List<Project> all(Organization organization) {
        return find.query().where()
                .eq("organization_id", organization.getId())
                .orderBy("when_created desc").findList();
    }

    public static List<Project> all(String text, Organization organization) {
        if (text != null) {
            return find.query().where()
                    .eq("organization_id", organization.getId())
                    .like("name", "%" + text + "%")
                    .orderBy("when_created desc").findList();
        } else {
            return new ArrayList<>();
        }
    }

    public static Project findById(String projectId) {
        return find.query().where().eq("id", projectId).findOne();
    }

    public Integer getLocalId() {
        return localId;
    }

    public void setLocalId(Integer localId) {
        this.localId = localId;
    }

    public ProjectCycle getCurrentCycle() {
        if (this.getCycles().size() > 0) {
            for (ProjectCycle cycle : this.getCycles()) {
                if (cycle.getCurrent()) {
                    return cycle;
                }
            }
        } else {
            currentCycle = null;
        }
        return currentCycle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Html getDescription() {
        return description;
    }

    public void setDescription(Html description) {
        this.description = description;
    }

    public String getAccessUrl() {
        return Utils.getEngageURL() + route + this.id;
    }

    public Organization getClient() {
        return client;
    }

    public void setClient(Organization client) {
        this.client = client;
    }

    public User getBy() {
        return by;
    }

    public void setBy(User by) {
        this.by = by;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Set<ProjectCycle> getCycles() {
        return cycles;
    }

    public void setCycles(Set<ProjectCycle> cycles) {
        this.cycles = cycles;
    }

    public String getOrganizationId() {
        return this.organization.id;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return this.organization.getName();
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public ProjectType getType() {
        if (this.getCurrentCycle() != null) {
            return this.getCurrentCycle().getType();
        } else {
            return null;
        }
    }

    public ProjectPriority getPriority() {
        if (this.getCurrentCycle() != null) {
            return this.getCurrentCycle().getPriority();
        } else {
            return null;
        }
    }

    public ProjectStatus getStatus() {
        if (this.getCurrentCycle() != null) {
            return this.getCurrentCycle().getStatus();
        } else {
            return null;
        }
    }

    public double getProgressPercentage() {
        if (this.getCurrentCycle() != null) {
            return this.getCurrentCycle().getProgressPercentage();
        } else {
            return 0.0;
        }
    }

    public String getLocalName() {
        return "PROJECT-" + this.localId;
    }

    public String getDescriptionHtml() {
        return this.description.getData();
    }

    public double getTotalBudget() {
        if (this.getCurrentCycle() == null) {
            return 0.0;
        } else {
            return this.getCurrentCycle().getTotalBudget();
        }
    }

    public double getTotalBudgetedHours() {
        if (this.getCurrentCycle() == null) {
            return 0.0;
        } else {
            return this.getCurrentCycle().getTotalBudgetedHours();
        }
    }
}