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
import com.google.inject.Inject;
import models.*;
import play.Logger;
import play.http.HttpErrorHandler;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Security;
import utils.ImageUtils;
import utils.Mailer;
import utils.S3Manager;
import utils.Utils;

import java.io.File;
import java.sql.Timestamp;
import java.util.*;

import static utils.GoogleAuthenticator.*;
import static utils.Utils.nextSessionId;

/*
 * This controller contains AssessmentTypes app common logic
 */

public class Users extends BaseController {

    @Security.Authenticated(Secured.class)
    public Result getUser() {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("user", Json.toJson(myUser));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result getAllUsers() {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        if (!myUser.getSuperadmin()) {
            return badRequest();
        } else {
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("users", Json.toJson(User.all()));
            return ok(result);
        }
    }

    @Security.Authenticated(Secured.class)
    public Result getUserTwoFactorBarCode() {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);        // Check if the user exists.
        if (myUser == null) {
            return badRequest();
        }

        if (myUser.getTOTPConfigured()) {
            return badRequest();
        } else {
            ObjectNode result = Json.newObject();
            String barCode = getGoogleAuthenticatorBarCode(myUser.getTOTPKey(), myUser.getEmail(), "OmegaTrace Inc.");
            result.put("status", "ok");
            result.put("success", "ok");
            result.put("bar_code", barCode);
            return ok(result);
        }
    }

    @Security.Authenticated(Secured.class)
    public Result updateUser() {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        JsonNode json = request().body().asJson();
        String firstName = json.get("first_name").asText();
        String lastName = json.get("last_name").asText();
        String email = json.get("email").asText();
        String city = json.get("place").get("city").asText();
        String state = json.get("place").get("state").asText();
        Boolean enabled = json.get("enabled").asBoolean();
        ObjectNode result = Json.newObject();
        if (email.equals(myUser.getEmail())) {
            myUser.setFirstName(firstName);
            myUser.setLastName(lastName);
            myUser.setEnabled(enabled);
            Place myPlace = myUser.getPlace();
            myPlace.setCity(city);
            myPlace.setState(state);
            myPlace.save();
            myUser.save();
            result.set("user", Json.toJson(myUser));
            return ok(result);
        } else {
            result.set("user", Json.toJson(myUser));
            return badRequest(result);
        }
    }

    @Security.Authenticated(Secured.class)
    public Result getAll() {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        ObjectNode result = Json.newObject();
        List<User> users = new ArrayList<>();
        result.put("status", "ok");
        if (!myUser.getSuperadmin()) {
            users = User.all();
        }
        result.set("users", Json.toJson(users));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result getUsers(String organizationId) {

        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);

        if (organizationId != null) {
            myOrg = Organization.findById(organizationId);
        } else {
            myOrg = myUser.getOrganization();
        }

        List<User> users = User.findAllByOrganization(myOrg);
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.set("users", Json.toJson(users));

        if (myUser.getSuperadmin()) {
            return ok(result);
        } else {

            if (myUser.getOrganization().equals(myOrg)) {
                return ok(result);
            } else {

                for (Organization o : myUser.getOrganization().getCollaborators()) {
                    if (o.equals(myOrg)) {
                        return ok(result);
                    }
                }
            }

            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result get(String id) {
        User currentUser = User.findByEmail(session().get("email"), organization());
        if (currentUser.getSuperadmin()) {
            ObjectNode result = Json.newObject();
            User user = User.findById(id);
            result.put("status", "ok");
            result.set("user", Json.toJson(user));
            return ok(result);
        } else {
            User user = User.findById(id);
            boolean accessGrant = false;
            if (currentUser.getOrganization().equals(user.getOrganization())) {
                accessGrant = true;
            } else {
                for (Organization o : currentUser.getOrganization().getCollaborators()) {
                    if (o.equals(currentUser.getOrganization())) {
                        accessGrant = true;
                    }
                }
            }

            if (accessGrant) {
                ObjectNode result = Json.newObject();
                result.put("status", "ok");
                result.set("user", Json.toJson(user));
                return ok(result);
            } else {
                return badRequest();
            }

        }
    }

    @Security.Authenticated(Secured.class)
    public Result post() {
        ObjectNode result = Json.newObject();
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        JsonNode json = request().body().asJson();
        String firstName = null;
        if (json.get("first_name") != null) {
            firstName = json.get("first_name").asText();
        }
        String lastName = null;
        if (json.get("last_name") != null) {
            lastName = json.get("last_name").asText();
        }
        String email = null;
        if (json.get("email") != null) {
            email = json.get("email").asText();
        }
        String password = null;
        if (json.get("password") != null) {
            password = json.get("password").asText();
        }
        String phone_number = null;
        if (json.get("phone_number") != null) {
            phone_number = json.get("phone_number").asText();
        }
        Boolean isAdmin = null;
        if (json.get("is_admin") != null) {
            isAdmin = json.get("is_admin").asBoolean();
        }
        Boolean enabled = null;
        if (json.get("enabled") != null) {
            enabled = json.get("enabled").asBoolean();
        }

        String organizationId = null;
        if (organizationId == null) {
            if (json.get("organization").get("id") != null) {
                organizationId = json.get("organization").get("id").asText();
            }
        }

        String roleId = null;
        if (json.has("role")) {
            if (json.get("role").get("id") != null) {
                roleId = json.get("role").get("id").asText();
            }
        }

        List<Object> errorsList = new ArrayList<>();

        if (firstName == null || firstName.isEmpty()) {
            // First Name Error
            ObjectNode errorObject = Json.newObject();
            errorObject.put("first_name", "REQUIRED");
            errorsList.add(errorObject);
        }

        if (lastName == null || lastName.isEmpty()) {
            // Last Name Error
            ObjectNode errorObject = Json.newObject();
            errorObject.put("last_name", "REQUIRED");
            errorsList.add(errorObject);
        }

        if (email == null || email.isEmpty()) {
            // Email Error
            ObjectNode errorObject = Json.newObject();
            errorObject.put("email", "REQUIRED");
            errorsList.add(errorObject);
        }


        Organization o = null;

        if (myUser.getSuperadmin()) {
            if (organizationId != null) {
                o = Organization.findById(organizationId);
            } else {
                if (email != null) {
                    o = Organization.findByEmailDomain(Utils.getEmailDomain(email));
                }
            }
        } else {
            if (email != null) {
                o = Organization.findByEmailDomain(Utils.getEmailDomain(email));
            }
        }

        if (o == null) {
            result.put("status", "error");
            ObjectNode errorObject = Json.newObject();
            errorObject.put("organization", "NOT_FOUND");
            errorsList.add(errorObject);
        }

        Role r = null;

        if (roleId != null) {
            r = Role.findById(roleId);
        } else {
            r = Role.findByName("Default", o);
        }

        if (!errorsList.isEmpty()) {
            result.put("status", "error");
            result.set("errors", Json.toJson(errorsList));
            Logger.error("error creating user - " + Json.prettyPrint(result));
            return ok(result);
        }

        User user = User.findByEmail(email, organization());
        if (user != null) {
            result.put("status", "error");
            ObjectNode errorObject = Json.newObject();
            errorObject.put("user", "EXISTS");
            errorsList.add(errorObject);
            result.set("errors", Json.toJson(errorsList));
            return ok(result);
        }

        try {
            Place p = new Place("home", PlaceType.findByName("HOME", o), o);
            p.save();
            user = new User(firstName, lastName, email, getRandomSecretKey(), p, o);
            if (phone_number != null) {
                user.setPhoneNumber(phone_number);
            }
            user.setAdmin(isAdmin);
            user.setAdmin(enabled);
            user.setRole(r);
            user.setConfirmed(true);
            user.save();
            if (user != null) {
                user.setResetToken(Utils.nextSessionId());
                Mailer.MailNewAccountEmail(user);
                user.save();
            }
        } catch (Exception e) {
            result.put("status", "error");
            ObjectNode errorObject = Json.newObject();
            errorObject.put("unknown", "UNKNOWN_ERROR");
            errorsList.add(errorObject);
            result.set("errors", Json.toJson(errorsList));
            Logger.error("error creating user - " + Json.prettyPrint(result) + " exception message: " + e.getLocalizedMessage());
            e.printStackTrace();
            return ok(result);
        }

        result.put("status", "ok");
        result.put("success", "ok");
        result.set("user", Json.toJson(user));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result postSetupTwoFactor(String id) {
        JsonNode json = request().body().asJson();
        Session mySession = Session.findBySessionId(session().get("session_id"));

        try {
            String totpCode = json.get("code").asText();

            // Make sure form entries are valid
            if (totpCode == null || totpCode.equals("null") || totpCode.isEmpty()) {
                ObjectNode result = Json.newObject();
                result.put("status", "fail");
                result.put("error", "User does not have two-factor authentication configured.");
                Logger.error("error - Incorrect TOTP code. for SESSION:" + mySession.getId());
                return badRequest(result);
            }

            User user = User.findById(id);
            // Make sure user exists
            if (user == null) {
                ObjectNode result = Json.newObject();
                result.put("status", "fail");
                result.put("error", "User does not exist.");
                Logger.error("error - User does not exist.");
                return badRequest(result);
            }

            // Check if Two factor is setup
            if (user.getTOTPConfigured()) {
                mySession.setTwoFactored(false);
                mySession.update();
                ObjectNode result = Json.newObject();
                result.put("status", "fail");
                result.put("error", "Two factor authentication method has been set up.");
                result.set("user", Json.toJson(user));
                result.set("session", Json.toJson(mySession));
                result.put("is_two_factored", false);
                result.put("next_page", user.getRole().getDefaultRoute());
                return ok(result);
            }

            if (validateTOTPCode(totpCode, user.getTOTPKey())) {
                user.setTOTPConfigured(true);
                user.update();
                mySession.setTwoFactored(false);
                mySession.update();
                ObjectNode result = Json.newObject();
                result.put("status", "ok");
                result.put("success", "Two factor authentication method has been set up.");
                result.set("user", Json.toJson(user));
                result.set("session", Json.toJson(mySession));
                result.put("is_two_factored", false);
                Logger.info("Setting up two factor authentication for USER:" + user.getId());
                return ok(result);
            } else {
                ObjectNode result = Json.newObject();
                result.set("user", Json.toJson(user));
                result.set("session", Json.toJson(mySession));
                result.put("is_two_factored", false);
                result.put("status", "fail");
                result.put("error", "Two factor authentication failed. Please retype code.");
                return ok(result);

            }
        } catch (NullPointerException e) {
            Logger.error("Authentication exception while setting up two factor - " + e.getMessage());
            ObjectNode result = Json.newObject();
            result.put("status", "fail");
            result.put("error", "Server Error.");
            result.set("session", Json.toJson(mySession));
            return ok(result);
        }
    }

    @Security.Authenticated(Secured.class)
    public Result postVerifyTwoFactor(String id) {
        try {
            JsonNode json = request().body().asJson();
            Session mySession = Session.findBySessionId(session().get("session_id"));
            String totpCode = json.get("code").asText();
            // Make sure form entries are valid
            if (totpCode == null || totpCode.equals("")) {
                ObjectNode result = Json.newObject();
                result.put("status", "fail");
                result.put("error", "User does not have two-factor authentication configured.");
                Logger.error("error - Incorrect TOTP code. for SESSION:" + mySession.getId());
                return badRequest(result);
            }

            User user = User.findById(id);
            // Make sure user exists
            if (user == null) {
                ObjectNode result = Json.newObject();
                result.put("status", "fail");
                result.put("error", "User does not exist.");
                Logger.error("error - User does not exist.");
                return badRequest(result);
            }
            // Check if Two factor is setup
            if (!user.getTOTPConfigured()) {
                ObjectNode result = Json.newObject();
                result.put("status", "fail");
                result.put("error", "User does not have two-factor authentication configured.");
                return badRequest(result);
            }

            if (validateTOTPCode(totpCode, user.getTOTPKey())) {
                mySession.setTwoFactored(true);
                mySession.update();
                ObjectNode result = Json.newObject();
                result.put("success", "ok");
                result.put("status", "ok");
                result.set("user", Json.toJson(user));
                result.set("session", Json.toJson(mySession));
                Logger.info("User authenticated with two factor successfully - " + user.getId());
                return ok(result);
            } else {
                ObjectNode result = Json.newObject();
                result.put("status", "fail");
                result.put("error", "Two factor authentication failed. Please retype code.");
                return badRequest(result);
            }
        } catch (NullPointerException e) {
            Logger.error("Authentication exception while verifying two factor code - " + e.getMessage());
            ObjectNode result = Json.newObject();
            result.put("status", "fail");
            result.put("error", "Server Error.");
            return badRequest(result);
        }
    }

    @Security.Authenticated(Secured.class)
    public Result put(String id) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        if (myUser.getSuperadmin()) {
            JsonNode json = request().body().asJson();
            String firstName = json.get("first_name").asText();
            String lastName = json.get("last_name").asText();
            String email = json.get("email").asText();
            String userId = json.get("id").asText();
            String roleId = json.get("role").get("id").asText();
            String organizationId = null;
            if (json.has("organization")) {
                if (json.get("organization") != null) {
                    organizationId = json.get("organization").asText();
                }
            }
            Boolean enabled = json.get("enabled").asBoolean();
            User user = User.findById(String.valueOf(userId));
            if (organizationId != null) {
                Organization org = Organization.findById(organizationId);
                user.setOrganization(org);
            }
            Role role = Role.findById(roleId);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setEnabled(enabled);
            user.setRole(role);
            user.update();
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("user", Json.toJson(user));
            return ok(result);
        } else {
            return badRequest();
        }
    }

    // Accept only 4MB of JSON data.
    public static class Text8MB extends BodyParser.Json {
        @Inject
        public Text8MB(HttpErrorHandler errorHandler) {
            super(8000 * 1024, errorHandler);
        }
    }

    @BodyParser.Of(Text8MB.class)
    @Security.Authenticated(Secured.class)
    public Result postProfileImage(String userId) {
        Organization myOrg = organization();
        ObjectNode result = Json.newObject();
        User myUser = User.findById(userId);
        JsonNode json = request().body().asJson();

        String imageData = json.get("image_data").asText();
        if (myUser == null) {
            result.put("status", "fail");
            result.put("error", "User not found.");
            return badRequest(result);
        }

        String fileName = nextSessionId() + ".png";
        TypeImage profileImage = new TypeImage(myOrg, "");

        try {

            ImageUtils.decoder(imageData, fileName);
            Logger.info(userId + " - " + " Uploading Image for this User");
            Logger.info("Image char Length - " + imageData.length());

            String profileImageUrl = S3Manager.uploadWrrKrrProfileImage(myOrg.getId() + S3Manager.SUFFIX + userId,
                    fileName, new File(fileName));

            if (profileImageUrl == null) {
                // TODO: handle this error a bit better.
                return internalServerError();
            }
            if (profileImage.getUrl() == null || profileImage.getUrl().equals("")) {
                profileImage.setUrl(profileImageUrl);
            }

            profileImage.save();
            myUser.setImage(profileImage);
            myUser.save();

        } catch (NullPointerException e) {
            Logger.error(e.getMessage());
            return internalServerError();
        }
        result.set("image", Json.toJson(profileImage));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result getUserProfileReport(String id) {
        User myUser = User.findByEmail(session().get("email"), organization());
        User user = User.findById(id);
        if (myUser.getSuperadmin() || user.getId().equals(myUser.getId())) {

            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");

            // Month To date report data
            ObjectNode monthToDateReport = Json.newObject();
            int[] sparklineMonthToDate = {8, 6, 0, 4, 3, 8, 8, 9};
            monthToDateReport.set("sparkline_data", Json.toJson(sparklineMonthToDate));
            monthToDateReport.put("clocked_hours", 25);
            monthToDateReport.put("clocked_minutes", 20);

            // Shift Report
            ObjectNode shiftReport = Json.newObject();
            List<String> dates = new ArrayList<>();
            dates.add("x");
            List<String> data = new ArrayList<>();
            data.add("Hours");


            // Month to date
            Calendar mycal = new GregorianCalendar(Utils.getCurrentTimestamp().toLocalDateTime().getYear(),
                    Utils.getCurrentTimestamp().toLocalDateTime().getMonth().getValue() - 1, 1);
            Timestamp startDate = new Timestamp(Utils.atStartOfDay(mycal.getTime()).getTime());
            Timestamp endDate = Utils.getCurrentTimestamp();
            List<ShiftReport> reports = ShiftReport.findInBetweenDates(myUser, startDate, endDate);
            for(ShiftReport report: reports){
               dates.add(report.getForDate().toString());
               data.add(String.format("%.1f",((float) report.getClockedHours() + ((float) report.getClockedMinutes() / 60))));
            }
            shiftReport.set("dates", Json.toJson(dates));
            shiftReport.set("data", Json.toJson(data));

            // Visit Report
            ObjectNode vistReport = Json.newObject();

            /*
              ['x', '2017-05-01', '2017-05-02', '2017-05-03', '2017-05-04', '2017-05-05', '2017-05-06'],
              ['ZONE A', 10, 4, 5, 5, 6, 5],
              ['ZONE B', 1, 3, 1, 5, 2, 3],
              ['ZONE C', 2, 5, 2, 4, 0, 3]
             */

            // User profile report data
            ObjectNode userProfileReport = Json.newObject();
            userProfileReport.set("month_to_date_report", monthToDateReport);
            userProfileReport.set("shift_report", shiftReport);
            userProfileReport.set("visit_report", vistReport);
            result.set("user_profile_report", userProfileReport);
            return ok(result);
        } else {
            return badRequest();
        }
    }


}