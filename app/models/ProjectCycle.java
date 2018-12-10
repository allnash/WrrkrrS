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

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectCycle extends BaseModel {

    /**
     * ProjectCycle Table
     */


    @JsonProperty("with_deadline")
    Timestamp withDeadline;
    @ManyToMany
    @JoinTable(
            name = "project_cycle_tasks",
            joinColumns = @JoinColumn(name = "project_cycle_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "task_id", referencedColumnName = "id"))
    public Set<Task> tasks;
    @ManyToMany
    @JoinTable(
            name = "project_cycle_participants",
            joinColumns = @JoinColumn(name = "project_cycle_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    public Set<User> participants;
    @JsonProperty("total_budget")
    public double totalBudget;
    @JsonProperty("total_budgeted_hours")
    public double totalBudgetedHours;
    @JsonProperty("total_spent_hours")
    public double totalSpentHours;
    public String notes;
    public Boolean verified;
    public Boolean enabled;
    public Boolean current;
    @ManyToOne
    public ProjectType type;
    @ManyToOne
    public ProjectStatus status;
    @ManyToOne
    public ProjectPriority priority;
    @JsonIgnore
    @Column(name = "organization")
    @ManyToOne
    public Organization organization;
    @ManyToOne
    public User by;
    @Transient
    @JsonProperty("tasks_count")
    public Integer tasksCount;
    @Transient
    @JsonProperty("activities_count")
    public Integer activitiesCount;
    @Transient
    @JsonProperty("discussions_count")
    public Integer discussionsCount;
    @Transient
    @JsonProperty("progress_percentage")
    public Integer progressPercentage;
    @Transient
    @JsonProperty("budget_percentage")
    public Integer budgetPercentage;
    @JsonIgnore
    @ManyToMany(mappedBy = "cycles")
    public Set<Project> projects;

    public ProjectCycle() {

    }

    public ProjectCycle(ProjectType type, ProjectStatus status, ProjectPriority priority, Organization organization, User by) {
        this.type = type;
        this.status = status;
        this.priority = priority;
        this.verified = true;
        this.enabled = true;
        this.current = true;
        this.totalBudget = 0.0;
        this.totalBudgetedHours = 0.0;
        this.totalSpentHours = 0.0;
        this.organization = organization;
        this.by = by;
    }

    public static Finder<Long, ProjectCycle> find = new Finder<>(ProjectCycle.class);

    public static List<ProjectCycle> all(Map parameterMap) {
        if (parameterMap != null)
            return find.query().where().allEq(parameterMap).findList();
        else
            return null;
    }

    public static List<ProjectCycle> all() {
        return find.all();
    }

    public static ProjectCycle findById(String ownerId) {
        return find.query().where().eq("id", ownerId).findOne();
    }

    public Integer getTaskCount() {
        List<Task> filteredTasks = new ArrayList<>();
        for(Task t: this.tasks){
            if(!t.getDeleted()){
                filteredTasks.add(t);
            }
        }
        return filteredTasks.size();
    }

    public Integer getDiscussionsCount() {
        return 0;
    }

    public double getTotalBudgetedHours() {
        return totalBudgetedHours;
    }

    public void setTotalBudgetedHours(double totalBudgetedHours) {
        this.totalBudgetedHours = totalBudgetedHours;
    }

    public double getTotalSpentHours() {
        return totalSpentHours;
    }

    public void setTotalSpentHours(double totalSpentHours) {
        this.totalSpentHours = totalSpentHours;
    }

    public double getBudgetPercentage() {
        if (totalSpentHours == 0 || totalBudgetedHours == 0)
            return 0.0;
        return totalSpentHours / totalBudgetedHours * 100;
    }

    public double getProgressPercentage() {
        if (totalSpentHours == 0 || totalBudgetedHours == 0)
            return 0.0;
        return totalSpentHours / totalBudgetedHours * 100;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Timestamp getWithDeadline() {
        return withDeadline;
    }

    public void setWithDeadline(Timestamp withDeadline) {
        this.withDeadline = withDeadline;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public User getBy() {
        return by;
    }

    public void setBy(User by) {
        this.by = by;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public ProjectPriority getPriority() {
        return priority;
    }

    public void setPriority(ProjectPriority priority) {
        this.priority = priority;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public Integer getTasksCount() {
        return tasksCount;
    }

    public void setTasksCount(Integer tasksCount) {
        this.tasksCount = tasksCount;
    }

    public Integer getActivitiesCount() {
        return activitiesCount;
    }

    public void setActivitiesCount(Integer activitiesCount) {
        this.activitiesCount = activitiesCount;
    }

    public void setDiscussionsCount(Integer discussionsCount) {
        this.discussionsCount = discussionsCount;
    }

    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public void setBudgetPercentage(Integer budgetPercentage) {
        this.budgetPercentage = budgetPercentage;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public ProjectType getType() {
        return type;
    }

    public void setType(ProjectType type) {
        this.type = type;
    }
}

