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
import java.util.List;
import java.util.Map;
import java.util.Set;


@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task extends BaseTenantModel {

    /**
     * ProjectTask Table
     */

    @Column(columnDefinition = "TEXT")
    public String description;

    @JsonProperty("detail")
    @OneToOne
    public Html detail;

    public String notes;

    @ManyToOne
    public TaskStatus status;
    @ManyToOne
    public TaskType type;
    @ManyToOne
    public TaskPriority priority;
    @ManyToOne
    public TaskRisk risk;
    public Boolean verified;
    @JsonProperty("budgeted_hours")
    public double budgetedHours;
    @JsonProperty("spent_hours")
    public double spentHours;
    public Boolean enabled;

    @ManyToMany
    @JoinTable(
            name = "task_participants",
            joinColumns = @JoinColumn(name = "task_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    public Set<User> participants;

    @Transient
    @JsonProperty("access_url")
    public String accessUrl;

    @Transient
    @JsonIgnore
    private String route = "/index.html#/index/tasks/";


    public Task() {
    }

    public Task(String description, double budgetedHours, TaskType type, TaskStatus status, TaskPriority priority,
                Organization organization) {
        this.type = type;
        this.status = status;
        this.priority = priority;
        this.description = description;
        this.budgetedHours = budgetedHours;
        this.verified = true;
        this.enabled = true;
        this.deleted = false;
        this.organization = organization;
    }

    public static Finder<Long, Task> find = new Finder<>(Task.class);

    public static List<Task> all(Map parameterMap) {
        if (parameterMap != null)
            return find.query().where().allEq(parameterMap).findList();
        else
            return null;
    }

    public static List<Task> all() {
        return find.all();
    }

    public static Task findById(String id) {
        return find.query().where().eq("id", id).findOne();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Html getDetail() {
        return detail;
    }

    public void setDetail(Html detail) {
        this.detail = detail;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getAccessUrl() {
        return Utils.getEngageURL() + route + this.id;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public TaskRisk getRisk() {
        return risk;
    }

    public void setRisk(TaskRisk risk) {
        this.risk = risk;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public User getBy() {
        return by;
    }

    public void setBy(User by) {
        this.by = by;
    }

    public double getBudgetedHours() {
        return budgetedHours;
    }

    public void setBudgetedHours(double budgetedHours) {
        this.budgetedHours = budgetedHours;
    }

    public double getSpentHours() {
        return spentHours;
    }

    public void setSpentHours(double spentHours) {
        this.spentHours = spentHours;
    }
}

