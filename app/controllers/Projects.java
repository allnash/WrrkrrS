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

package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

import java.util.*;

/*
 * This controller contains Roles app common logic
 */

public class Projects extends BaseController {

    @Security.Authenticated(Secured.class)
    public Result get(String projectId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Project project = Project.findById(projectId);
        if (project == null) {
            return badRequest();
        }
        Organization organization = hasOrganizationProjectReadAccess(myUser, project.getOrganization());
        if (organization == null) {
            return badRequest();
        }
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("project", Json.toJson(project));
        return ok(result);
    }


    @Security.Authenticated(Secured.class)
    public Result getProjects(String query, String organizationId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Organization organization = hasOrganizationProjectReadWriteAccess(myUser, Organization.findById(organizationId));
        if (organization == null) {
            return badRequest();
        }
        ObjectNode result = Json.newObject();
        List<Project> projects;
        if (query == null) {
            projects = Project.all(organization);
        } else if (query.isEmpty()) {
            projects = Project.all(organization);
        } else {
            projects = Project.all(query, organization);
        }
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("projects", Json.toJson(projects));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result getProjectTags(String query, String organizationId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Organization organization = hasOrganizationProjectReadWriteAccess(myUser, Organization.findById(organizationId));
        if (organization == null) {
            return badRequest();
        }
        ArrayList<String> tags = new ArrayList<>();
        if (query != null) {
            List<ProjectTag> searchedTags = ProjectTag.all(query, organization);
            for (ProjectTag t : searchedTags) {
                tags.add(t.getText());
            }
        }
        return ok(Json.toJson(tags));
    }


    @Security.Authenticated(Secured.class)
    public Result getProjectStatuses(String organizationId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Organization organization = hasOrganizationProjectReadWriteAccess(myUser, Organization.findById(organizationId));
        if (organization == null) {
            return badRequest();
        }
        ObjectNode result = Json.newObject();
        List<ProjectStatus> statuses = ProjectStatus.findByOrganization(organization);
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("project_statuses", Json.toJson(statuses));
        return ok(result);
    }


    @Security.Authenticated(Secured.class)
    public Result getProjectPriorities(String organizationId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Organization organization = hasOrganizationProjectReadWriteAccess(myUser, Organization.findById(organizationId));
        if (organization == null) {
            return badRequest();
        }
        ObjectNode result = Json.newObject();
        List<ProjectPriority> priorities = ProjectPriority.findByOrganization(organization);
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("project_priorities", Json.toJson(priorities));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result getProjectTypes(String organizationId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Organization organization = hasOrganizationProjectReadWriteAccess(myUser, Organization.findById(organizationId));
        if (organization == null) {
            return badRequest();
        }
        ObjectNode result = Json.newObject();
        List<ProjectType> types = ProjectType.findByOrganization(organization);
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("project_types", Json.toJson(types));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result getTaskStatuses(String organizationId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Organization organization = hasOrganizationProjectReadWriteAccess(myUser, Organization.findById(organizationId));
        if (organization == null) {
            return badRequest();
        }
        ObjectNode result = Json.newObject();
        List<TaskStatus> statuses = TaskStatus.findByOrganization(organization);
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("task_statuses", Json.toJson(statuses));
        return ok(result);
    }


    @Security.Authenticated(Secured.class)
    public Result getTaskPriorities(String organizationId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Organization organization = hasOrganizationProjectReadWriteAccess(myUser, Organization.findById(organizationId));
        if (organization == null) {
            return badRequest();
        }
        ObjectNode result = Json.newObject();
        List<TaskPriority> priorities = TaskPriority.findByOrganization(organization);
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("task_priorities", Json.toJson(priorities));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result getTaskTypes(String organizationId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Organization organization = hasOrganizationProjectReadWriteAccess(myUser, Organization.findById(organizationId));
        if (organization == null) {
            return badRequest();
        }
        ObjectNode result = Json.newObject();
        List<TaskType> types = TaskType.findByOrganization(organization);
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("task_types", Json.toJson(types));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result getProjectTask(String projectId, String taskId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Project project = Project.findById(projectId);
        if (project == null) {
            return badRequest();
        }
        Organization organization = hasOrganizationProjectReadAccess(myUser, project.getOrganization());
        if (organization == null) {
            return badRequest();
        }
        Task task = Task.findById(taskId);
        if (task == null) {
            return badRequest();
        }
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("task", Json.toJson(task));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result postTask(String projectId, String organizationId) {
        Organization o = organization();
        Organization myOrg = Organization.findById(organizationId);
        if (!myOrg.getId().equals(o.getId())) {
            return badRequest();
        }

        User myUser = User.findByEmail(session().get("email"), myOrg);

        Organization organization = hasOrganizationProjectReadWriteAccess(myUser, Organization.findById(organizationId));

        try {
            JsonNode json = request().body().asJson();
            // Create project.
            String description = json.get("description").asText("");
            String taskType = json.get("type").get("name").asText("");
            String taskStatus = json.get("status").get("name").asText("");
            String taskPriority = json.get("priority").get("name").asText("");
            JsonNode participantsJson = json.get("participants");
            double budgetedHours = json.get("budgeted_hours").asDouble(0.0);
            // Create Task for project with participants
            Set<User> participants = new HashSet<>();
            if (participantsJson.isArray()) {
                for (final JsonNode participantJson : participantsJson) {
                    User participant = User.findByEmail(participantJson.get("email").asText(""), myOrg);
                    participants.add(participant);
                }
            }
            // Task
            Task task = new Task(description, budgetedHours,
                    TaskType.findByName(taskType, organization),
                    TaskStatus.findByName(taskStatus, organization),
                    TaskPriority.findByName(taskPriority, organization),
                    organization);
            task.setParticipants(participants);
            task.save();

            // Add Task to Project Cycle
            ProjectCycle currentCycle = Project.findById(projectId).getCurrentCycle();
            Set<Task> tasks = currentCycle.getTasks();
            tasks.add(task);
            currentCycle.setTasks(tasks);
            currentCycle.save();

            // Return task as a JSON
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("task", Json.toJson(task));
            return ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("Error creating project - " + e.getLocalizedMessage());
            return badRequest();
        }
    }


    @Security.Authenticated(Secured.class)
    public Result putProjectTask(String projectId, String taskId) {
        Organization o = organization();
        User myUser = User.findByEmail(session().get("email"), o);
        Organization organization = hasOrganizationProjectReadWriteAccess(myUser, o);
        Project p = Project.findById(projectId);
        if (organization == null || p == null) {
            return badRequest();
        }

        try {
            JsonNode json = request().body().asJson();
            // Create project.
            String description = json.get("description").asText("");
            String taskType = json.get("type").get("name").asText("");
            String taskStatus = json.get("status").get("name").asText("");
            String taskPriority = json.get("priority").get("name").asText("");
            JsonNode participantsJson = json.get("participants");
            double budgetedHours = json.get("budgeted_hours").asDouble(0.0);
            // Create Task for project with participants
            Set<User> participants = new HashSet<>();
            if (participantsJson.isArray()) {
                for (final JsonNode participantJson : participantsJson) {
                    User participant = User.findByEmail(participantJson.get("email").asText(""), organization);
                    participants.add(participant);
                }
            }
            // Task
            Task task = Task.findById(taskId);
            task.setDescription(description);
            task.setType(TaskType.findByName(taskType, organization));
            task.setStatus(TaskStatus.findByName(taskStatus, organization));
            task.setPriority(TaskPriority.findByName(taskPriority, organization));
            task.setParticipants(participants);
            task.setBudgetedHours(budgetedHours);
            task.save();

            // Return task as a JSON
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.put("task", Json.toJson(task));
            return ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("Error creating project - " + e.getLocalizedMessage());
            return badRequest();
        }
    }


    @Security.Authenticated(Secured.class)
    public Result postProjectTaskOperation(String projectId, String taskId) {

        Organization o = organization();
        User myUser = User.findByEmail(session().get("email"), o);
        Organization organization = hasOrganizationProjectReadWriteAccess(myUser, o);
        Project p = Project.findById(projectId);
        Task t =  Task.findById(taskId);
        if (organization == null || p == null) {
            return badRequest();
        }

        try {

            JsonNode json = request().body().asJson();
            ObjectNode result = Json.newObject();
            String operationName = json.get("name").asText("");
            /*
              MOVE Task Operation
             */
            if(operationName.equals("MOVE")) {

                String fromProjectId = json.get("from").get("project").get("id").asText(null);
                String toProjectId = json.get("to").get("project").get("id").asText(null);
                // Do some basic API call and make sure its correct from.project.id and :fromProjectId
                if(!fromProjectId.equals(projectId)){
                    return badRequest();
                }
                // Remove Task from Project
                ProjectCycle myProjectCycle = Project.findById(projectId).getCurrentCycle();
                Set<Task> fromProjectTasks = myProjectCycle.getTasks();
                fromProjectTasks.removeIf(task -> task.getId().equals(t.getId()));
                myProjectCycle.setTasks(fromProjectTasks);
                myProjectCycle.save();

                // Add Task to a different Project
                ProjectCycle myToProjectCycle = Project.findById(toProjectId).getCurrentCycle();
                Set<Task> toProjectTasks = myProjectCycle.getTasks();
                toProjectTasks.add(t);
                myToProjectCycle.setTasks(toProjectTasks);
                myToProjectCycle.save();

                result.put("status", "ok");
                result.put("success", "ok");
            } else {
                result.put("status", "ok");
            }
            return ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("Error creating project - " + e.getLocalizedMessage());
            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result updateTasksStatus(String projectId) {
        Organization o = organization();
        User myUser = User.findByEmail(session().get("email"), o);
        Organization organization = hasOrganizationProjectReadWriteAccess(myUser, o);
        Project p = Project.findById(projectId);
        boolean updated = false;
        if (organization == null || p == null) {
            return badRequest();
        }

        try {
            JsonNode json = request().body().asJson();
            String taskStatus = json.get("status").asText("");
            JsonNode tasksJson = json.get("tasks");
            if (tasksJson.isArray()) {
                for (final JsonNode taskJson : tasksJson) {
                    Task t = Task.findById(taskJson.get("id").asText(""));
                    if (!t.getStatus().getName().equals(taskStatus)) {
                        t.setStatus(TaskStatus.findByName(taskStatus, organization));
                        t.save();
                        updated = true;
                    }
                }
            }

            // Return task as a JSON
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.put("updated", updated);
            return ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("Error creating project - " + e.getLocalizedMessage());
            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result removeParticipant(String projectId, String userId) {
        User myUser = User.findByEmail(session().get("email"), organization());
        Organization organization = hasOrganizationProjectReadWriteAccess(myUser, organization());
        if (organization == null) {
            return badRequest();
        }
        final User participant = User.findById(userId);
        ProjectCycle myProjectCycle = Project.findById(projectId).getCurrentCycle();
        Set<User> participants = myProjectCycle.getParticipants();
        participants.removeIf(user -> user.getId().equals(participant.getId()));
        myProjectCycle.setParticipants(participants);
        myProjectCycle.save();
        // Return project as a JSON
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        return ok(result);
    }


    @Security.Authenticated(Secured.class)
    public Result putParticipant(String projectId, String userId) {
        User myUser = User.findByEmail(session().get("email"), organization());
        Organization organization = hasOrganizationProjectReadWriteAccess(myUser, organization());
        if (organization == null) {
            return badRequest();
        }
        final User participant = User.findById(userId);
        ProjectCycle myProjectCycle = Project.findById(projectId).getCurrentCycle();
        Set<User> participants = myProjectCycle.getParticipants();
        participants.add(participant);
        myProjectCycle.setParticipants(participants);
        myProjectCycle.save();
        // Return project as a JSON
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result post(String organizationId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        Organization organization = hasOrganizationProjectReadWriteAccess(myUser, Organization.findById(organizationId));
        try {
            JsonNode json = request().body().asJson();
            // Create project.
            String name = json.get("name").asText("");
            String description = json.get("description").asText("");
            Html descriptionHtml = new Html(description, organization);
            descriptionHtml.save();
            // Create project
            Project project = new Project(name,
                    descriptionHtml,
                    organization,
                    myUser);
            project.save();
            // Create project cycle.
            String projectType = json.get("type").get("name").asText("");
            String priority = json.get("priority").get("name").asText("");
            String status = json.get("status").get("name").asText("");
            ProjectCycle cycle = new ProjectCycle(ProjectType.findByName(projectType, organization),
                    ProjectStatus.findByName(status, organization),
                    ProjectPriority.findByName(priority, organization),
                    organization,
                    myUser);
            if (json.hasNonNull("total_budget")) {
                cycle.setTotalBudget(json.get("total_budget").asDouble(0));
            }
            if (json.hasNonNull("total_budgeted_hours")) {
                cycle.setTotalBudget(json.get("total_budgeted_hours").asDouble(0));
            }
            // Disable as we may not need to add the user right now.
            // List<User> participants = new ArrayList<>();
            // participants.add(myUser);
            /// cycle.setParticipants(participants);
            cycle.save();
            // Set Cycle to project
            Set<ProjectCycle> cycles = new HashSet<>();
            cycles.add(cycle);
            project.setCycles(cycles);
            project.update();
            // Return project as a JSON
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("project", Json.toJson(project));
            return ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("Error creating project - " + e.getLocalizedMessage());
            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result put(String projectId) {
        User myUser = User.findByEmail(session().get("email"), organization());
        Organization myOrg = hasOrganizationProjectReadWriteAccess(myUser, organization());
        try {
            JsonNode json = request().body().asJson();
            // Create project.
            String projectType = json.get("type").get("name").asText("");
            String name = json.get("name").asText("");
            String description = json.get("description_html").asText("");
            String priority = json.get("priority").get("name").asText("");
            String status = json.get("status").get("name").asText("");
            // Get project
            Project project = Project.findById(projectId);
            if (project == null) {
                return badRequest();
            }
            Html html = project.getDescription();
            html.setData(description);
            html.save();
            project.setName(name);
            project.save();

            // Get project cycle.
            ProjectCycle cycle = project.getCurrentCycle();
            if (json.hasNonNull("total_budget")) {
                cycle.setTotalBudget(json.get("total_budget").asDouble(0));
            }
            if (json.hasNonNull("total_budgeted_hours")) {
                cycle.setTotalBudgetedHours(json.get("total_budgeted_hours").asDouble(0));
            }
            cycle.setType(ProjectType.findByName(projectType, myOrg));
            cycle.setStatus(ProjectStatus.findByName(status, myOrg));
            cycle.setPriority(ProjectPriority.findByName(priority, myOrg));
            cycle.save();

            // Return project as a JSON
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("project", Json.toJson(project));
            return ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("Error creating project - " + e.getLocalizedMessage());
            return badRequest();
        }
    }


    @Security.Authenticated(Secured.class)
    public Result putProjectStatus(String projectId) {
        User myUser = User.findByEmail(session().get("email"), organization());
        Organization myOrg = hasOrganizationProjectReadWriteAccess(myUser, organization());
        try {
            JsonNode json = request().body().asJson();
            String status = json.get("status").asText("");
            // Get project
            Project project = Project.findById(projectId);
            if (project == null) {
                return badRequest();
            }

            // Get project cycle.
            ProjectCycle cycle = project.getCurrentCycle();
            cycle.setStatus(ProjectStatus.findByName(status, myOrg));
            cycle.save();

            // Return project as a JSON
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("project", Json.toJson(project));
            return ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("Error creating project - " + e.getLocalizedMessage());
            return badRequest();
        }
    }


    private Organization hasOrganizationProjectReadWriteAccess(User user, Organization organization) {
        if (user.getSuperadmin()) {
            return organization;
        } else if (user.getRole().getProjectsModuleAccess() == Role.RoleAccess.READ_WRITE) {
            return user.getOrganization();
        } else {
            return null;
        }
    }

    private Organization hasOrganizationProjectReadAccess(User user, Organization organization) {
        if (user.getSuperadmin()) {
            return organization;
        } else if (user.getRole().getProjectsModuleAccess() != Role.RoleAccess.NONE) {
            return user.getOrganization();
        } else {
            return null;
        }
    }

}