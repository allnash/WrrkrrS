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
import utils.ThisLocalizedWeek;
import utils.Utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

/**
 * Created by gadre on 05/12/2018.
 */

public class Visits extends BaseController {

    @Security.Authenticated(Secured.class)
    public Result getVisits() {
        Organization myOrg = organization();
        ObjectNode result = Json.newObject();
        List<Object> data = new ArrayList<>();
        for (Visit visit : Visit.all(myOrg)) {
            Map<String, Object> element = new HashMap<>();
            element.put("when_started", visit.getWhenStarted().toString());
            element.put("when_ended", visit.getWhenEnded().toString());
            element.put("end_point_device_name", visit.getEndpointDevice().getName());
            element.put("user_device_name", visit.getUserDevice().getName());
            element.put("user_full_name", visit.getUser().getFullName());
            data.add(element);
        }
        result.put("success", "ok");
        result.put("status", "ok");
        result.set("visits", Json.toJson(data));
        return ok(result);
    }

    @Security.Authenticated(SecuredApi.class)
    public Result postReaderVisitReports() {
        ObjectNode result = Json.newObject();
        JsonNode json = request().body().asJson();
        JsonNode readerVisitReportsJson = json.get("reader_visit_reports");
        if (!readerVisitReportsJson.isArray()) {
            return badRequest();
        } else {
            Logger.info(readerVisitReportsJson.size() + " Reader visit reports received.");
        }
        for (JsonNode readerVisitReportJson : readerVisitReportsJson) {

            String id = readerVisitReportJson.get("id").asText();
            String readerDeviceId = readerVisitReportJson.get("reader_device_id").asText();
            Device readerDevice = Device.findByDeviceId(readerDeviceId);

            String readerVisitReportPlaceId = null;
            if (readerVisitReportJson.hasNonNull("place_id")) {
                readerVisitReportPlaceId = readerVisitReportJson.get("sighted_user_id").asText();
            }

            int androidCount = readerVisitReportJson.get("android_count").asInt();
            int appleCount = readerVisitReportJson.get("apple_count").asInt();
            // Note laptop vs laptops inside JSON. Obv typo bug.
            int laptopCount = readerVisitReportJson.get("laptops_count").asInt();
            int networkingDevicesCount = readerVisitReportJson.get("networking_devices_count").asInt();
            int maskedDevicesCount = readerVisitReportJson.get("masked_devices_count").asInt();
            int resolvedDevicesCount = readerVisitReportJson.get("resolved_devices_count").asInt();
            int othersCount = readerVisitReportJson.get("others_count").asInt();
            int notInOUICount = readerVisitReportJson.get("not_in_oui_count").asInt();
            // This value is : rvp.setDevicesCount(apples + androids + laptops);
            int devicesCount = readerVisitReportJson.get("devices_count").asInt();

            String whenSeen = readerVisitReportJson.get("when_seen").asText();
            Timestamp whenSeenTimestamp = new Timestamp(Long.valueOf(whenSeen));

            if (ReaderVisitReport.findById(id) != null) {
                continue;
            }

            // Create reader visit report record.
            ReaderVisitReport readerVisitReport = new ReaderVisitReport(readerDevice, whenSeenTimestamp, androidCount, appleCount, othersCount);
            readerVisitReport.setResolvedDevicesCount(resolvedDevicesCount);
            readerVisitReport.setDevicesCount(devicesCount);
            readerVisitReport.setMaskedDevicesCount(maskedDevicesCount);
            readerVisitReport.setLaptopCount(laptopCount);
            readerVisitReport.setNetworkingDevicesCount(networkingDevicesCount);
            readerVisitReport.setNotInOUICount(notInOUICount);
            readerVisitReport.setPlace(readerDevice.getCurrentPlace());
            readerVisitReport.save();
        }

        Logger.info(readerVisitReportsJson.size() + " Reader Visit reports loaded.");

        result.put("status", "ok");
        result.put("success", "ok");
        return ok(result);
    }


    @Security.Authenticated(SecuredApi.class)
    public Result postReaderVisits() {
        ObjectNode result = Json.newObject();
        JsonNode json = request().body().asJson();
        JsonNode readerVisitsJson = json.get("reader_visits");
        if (!readerVisitsJson.isArray()) {
            return badRequest();
        } else {
            Logger.info(readerVisitsJson.size() + " Reader visits received.");
        }
        for (JsonNode readerVisitJson : readerVisitsJson) {

            String visitId = readerVisitJson.get("id").asText();
            String endpointDeviceId = readerVisitJson.get("endpoint_device_id").asText();
            String userDeviceId  = readerVisitJson.get("user_device_id").asText();
            Device endpointDevice = Device.findByDeviceId(endpointDeviceId);
            Device userDevice = Device.findByDeviceId(userDeviceId);
            String whenStarted = readerVisitJson.get("when_started").asText();
            String whenEnded = readerVisitJson.get("when_ended").asText();
            Timestamp whenStartedTimestamp = new Timestamp(Long.valueOf(whenStarted));
            Timestamp whenEndedTimestamp = new Timestamp(Long.valueOf(whenEnded));

            // Create visit entry if missing
            Visit visit = Visit.findById(visitId);
            if(visit == null){
                visit =  new Visit(endpointDevice.getName() +" >> " + userDevice.getName(), userDevice.getOwner(),
                        Visit.VisitType.PLACE, whenStartedTimestamp);
                // OVERRIDE VISIT ID with the ID provided here.
                visit.setId(visitId);
                visit.setEndpointDevice(endpointDevice);
                visit.setUserDevice(userDevice);
                visit.setWhenEnded(whenEndedTimestamp);
                visit.save();
                Logger.info("Create visit record - " + visitId + " for visit type - PLACE");
            } else {
                visit.setWhenEnded(whenEndedTimestamp);
                visit.save();
                Logger.info("Updating visit record - " + visitId + " for activity - PLACE");
            }

        }

        Logger.info(readerVisitsJson.size() + " Reader Visits received.");

        result.put("status", "ok");
        result.put("success", "ok");
        return ok(result);
    }

    // DO NOT USE THIS. ITS ALL STUFF PROCESSED
    @Deprecated
    @Security.Authenticated(Secured.class)
    public Result getDailyReaderVisitReports(String readerDeviceId) {
        Organization myOrg = organization();
        ObjectNode result = Json.newObject();
        Device readerDevice = Device.findById(readerDeviceId);

        if (readerDevice == null) {
            return forbidden();
        }

        if (!readerDevice.getOrganizationId().equals(myOrg.getId())) {
            return forbidden();
        }

        List<Object> data = new ArrayList<>();
        List<ReaderVisitReport> readerVisitReportsList = ReaderVisitReport.findAllByReaderDeviceToday(readerDevice);
        for (ListIterator<ReaderVisitReport> iterator = readerVisitReportsList.listIterator(); iterator.hasNext(); ) {
            int visitors = 0;
            if (iterator.nextIndex() == 0) {
                ReaderVisitReport first = iterator.next();
                Map<String, Object> element = new HashMap<>();
                element.put("when_seen", first.getWhenSeen());
                element.put("devices_count", first.getDevicesCount());
                element.put("visitors", first.getDevicesCount());
                data.add(element);
                iterator.previous();
            }

            ReaderVisitReport value = iterator.next();
            ReaderVisitReport nextValue;

            if (iterator.hasNext()) {
                nextValue = iterator.next();
                // Reset the state of the iterator
                try {
                    iterator.previous();
                    visitors = (nextValue.getAppleCount() + nextValue.getAndroidCount()) - (value.getAppleCount() + value.getAndroidCount());
                } catch (NoSuchElementException e) {
                    visitors = value.getAppleCount() + value.getAndroidCount();
                }
                Map<String, Object> element = new HashMap<>();
                element.put("when_seen", nextValue.getWhenSeen());
                element.put("devices_count", nextValue.getAppleCount() + nextValue.getAndroidCount());
                element.put("visitors", visitors);
                data.add(element);
            }
        }


        result.put("success", "ok");
        result.put("status", "ok");
        result.set("reader_visit_reports", Json.toJson(data));
        Logger.info("Fetching daily reader visitor report for device - " + readerDeviceId + " , found " + readerVisitReportsList.size() + " entries.");
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result getDailyReaderHourlyVisitorCounts(String readerDeviceId, String date) {
        Organization myOrg = organization();
        ObjectNode result = Json.newObject();
        Device readerDevice = Device.findById(readerDeviceId);
        User myUser = User.findByEmail(session().get("email"), myOrg);

        if (readerDevice == null) {
            return forbidden();
        }

        if (!readerDevice.getOrganizationId().equals(myOrg.getId()) && !myUser.getSuperadmin()) {
            return forbidden();
        }

        List<Object> visitorTimesList = new ArrayList<>();
        visitorTimesList.add("x");

        List<Object> visitorCountsList = new ArrayList<>();
        visitorCountsList.add("pv");

        int dailyVisitors = 0;
        int dailyAppleCount = 0;
        int dailyAndroidCount = 0;

        // Daily report is always 1 day before.
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate yesterday = LocalDate.now().minusDays(1);
        if (date != null) {
            yesterday = LocalDate.parse(date);
        }
        LocalDateTime startTime = LocalDateTime.of(yesterday, midnight);
        LocalDateTime startTimeEST = startTime.plusHours(4);
        LocalDateTime endTimeEST = startTimeEST.plusHours(24);

        List<ReaderVisitReport> readerVisitReportsList = ReaderVisitReport
                .findAllByReaderDeviceWithTimeWindow(readerDevice, Utils.getTimestamp(startTimeEST), Utils.getTimestamp(endTimeEST));

        int visitors = 0;
        int androids = 0;
        int apples = 0;

        for (ListIterator<ReaderVisitReport> iterator = readerVisitReportsList.listIterator(); iterator.hasNext(); ) {

            int difference = 0;
            int appleDifference = 0;
            int androidDifference = 0;

            if (iterator.nextIndex() == 0) {
                ReaderVisitReport first = iterator.next();
                visitors = (first.getAppleCount() + first.getAndroidCount());
                iterator.previous();
            }

            ReaderVisitReport value = iterator.next();
            ReaderVisitReport nextValue;

            if (iterator.hasNext()) {
                nextValue = iterator.next();
                // Reset the state of the iterator
                try {
                    iterator.previous();
                    difference = (nextValue.getAppleCount() + nextValue.getAndroidCount()) - (value.getAppleCount() + value.getAndroidCount());
                    appleDifference = nextValue.getAppleCount() - value.getAppleCount();
                    androidDifference = nextValue.getAndroidCount() - value.getAndroidCount();
                } catch (NoSuchElementException e) {
                    difference = value.getDevicesCount();
                }
            }

            if (difference > 0) {
                visitors += difference;
            }

            if (appleDifference > 0) {
                apples += appleDifference;
            }

            if (androidDifference > 0) {
                androids += androidDifference;
            }

            visitorTimesList.add(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss Z").format(value.getWhenSeen()));
            visitorCountsList.add(value.getAppleCount() + value.getAndroidCount());
        }

        dailyVisitors += visitors;
        dailyAndroidCount += androids;
        dailyAppleCount += apples;

        // PRIOR DAY REPORT
        midnight = LocalTime.MIDNIGHT;
        LocalDate dayBeforeYesterday = LocalDate.now().minusDays(2);
        startTime = LocalDateTime.of(dayBeforeYesterday, midnight);
        startTimeEST = startTime.plusHours(4);
        endTimeEST = startTimeEST.plusHours(24);
        readerVisitReportsList = ReaderVisitReport
                .findAllByReaderDeviceWithTimeWindow(readerDevice, Utils.getTimestamp(startTimeEST), Utils.getTimestamp(endTimeEST));
        Map<String, Integer> dayBeforeCounts = processReaderVisitRecords(readerVisitReportsList);
        // Get Prior Day counts
        int dayBeforeVisitors = dayBeforeCounts.get("visitors");
        int dayBeforeAndroidCount = dayBeforeCounts.get("androids");
        int dayBeforeAppleCount = dayBeforeCounts.get("apples");

        // Calculate Change percentages
        float percentVisitors, percentAndroid, percentApple;
        if (dayBeforeVisitors == 0) {
            percentVisitors = 0;
        } else {
            percentVisitors = ((dailyVisitors - dayBeforeVisitors) * 100) / dayBeforeVisitors;
        }
        if (dayBeforeAndroidCount == 0) {
            percentAndroid = 0;
        } else {
            percentAndroid = ((dailyAndroidCount - dayBeforeAndroidCount) * 100) / dayBeforeAndroidCount;
        }
        if (dayBeforeAppleCount == 0) {
            percentApple = 0;
        } else {
            percentApple = ((dailyAppleCount - dayBeforeAppleCount) * 100) / dayBeforeAppleCount;
        }

        ArrayList<Object> data = new ArrayList<>();
        data.add(visitorTimesList);
        data.add(visitorCountsList);

        result.put("success", "ok");
        result.put("status", "ok");

        result.set("daily_reader_hourly_visitor_counts", Json.toJson(data));

        result.put("daily_visitor_counts", dailyVisitors);
        result.put("daily_apple_counts", dailyAppleCount);
        result.put("daily_android_counts", dailyAndroidCount);

        result.put("visitor_change_percent", percentVisitors);
        result.put("apple_change_percent", percentApple);
        result.put("android_change_percent", percentAndroid);
        return ok(result);
    }


    @Security.Authenticated(Secured.class)
    public Result getWeeklyVisitorCounts(String readerDeviceId, String date) {
        Organization myOrg = organization();
        ObjectNode result = Json.newObject();
        Device readerDevice = Device.findById(readerDeviceId);
        User myUser = User.findByEmail(session().get("email"), myOrg);

        if (readerDevice == null) {
            return forbidden();
        }

        if (!readerDevice.getOrganizationId().equals(myOrg.getId()) && !myUser.getSuperadmin()) {
            return forbidden();
        }

        List<Object> visitorTimesList = new ArrayList<>();
        visitorTimesList.add("x");

        List<Object> visitorCountsList = new ArrayList<>();
        visitorCountsList.add("data1");

        // Daily report is always 1 day before.
        ThisLocalizedWeek tw = new ThisLocalizedWeek(Locale.US);
        LocalDate firstDay = tw.getFirstDay();
        LocalDate lastDay = tw.getLastDay();

        for (int k = 0; k < 7; k++) {
            // Daily report is always 1 day before.
            LocalTime midnight = LocalTime.MIDNIGHT;
            LocalDate day = firstDay.plusDays(k);

            LocalDateTime startTime = LocalDateTime.of(day, midnight);
            LocalDateTime startTimeEST = startTime.plusHours(4);
            LocalDateTime endTimeEST = startTimeEST.plusHours(24);

            List<ReaderVisitReport> readerVisitReportsList = ReaderVisitReport
                    .findAllByReaderDeviceWithTimeWindow(readerDevice, Utils.getTimestamp(startTimeEST), Utils.getTimestamp(endTimeEST));

            if (readerVisitReportsList.size() == 0) {
                visitorTimesList.add(day.toString());
                visitorCountsList.add(0);
                continue;
            }

            Map<String, Integer> dailyCounts = processReaderVisitRecords(readerVisitReportsList);
            visitorTimesList.add(day.toString());
            visitorCountsList.add(dailyCounts.get("visitors"));
        }

        ArrayList<Object> data = new ArrayList<>();
        data.add(visitorTimesList);
        data.add(visitorCountsList);

        result.put("success", "ok");
        result.put("status", "ok");
        result.set("weekly_visitor_counts", Json.toJson(data));

        return ok(result);
    }


    @Security.Authenticated(Secured.class)
    public Result getLastSevenDaysVisitorCounts(String readerDeviceId) {
        Organization myOrg = organization();
        ObjectNode result = Json.newObject();
        Device readerDevice = Device.findById(readerDeviceId);
        User myUser = User.findByEmail(session().get("email"), myOrg);

        if (readerDevice == null) {
            return forbidden();
        }

        if (!readerDevice.getOrganizationId().equals(myOrg.getId()) && !myUser.getSuperadmin()) {
            return forbidden();
        }

        List<Object> visitorTimesList = new ArrayList<>();
        visitorTimesList.add("x");

        List<Object> visitorCountsList = new ArrayList<>();
        visitorCountsList.add("data1");

        // Daily report is always 1 day before.
        LocalDate firstDay = LocalDate.now().minusDays(7);

        for (int k = 0; k < 7; k++) {
            // Daily report is always 1 day before.
            LocalTime midnight = LocalTime.MIDNIGHT;
            LocalDate day = firstDay.plusDays(k);

            LocalDateTime startTime = LocalDateTime.of(day, midnight);
            LocalDateTime startTimeEST = startTime.plusHours(4);
            LocalDateTime endTimeEST = startTimeEST.plusHours(24);

            List<ReaderVisitReport> readerVisitReportsList = ReaderVisitReport
                    .findAllByReaderDeviceWithTimeWindow(readerDevice, Utils.getTimestamp(startTimeEST), Utils.getTimestamp(endTimeEST));

            if (readerVisitReportsList.size() == 0) {
                visitorTimesList.add(day.toString());
                visitorCountsList.add(0);
                continue;
            }

            Map<String, Integer> dailyCounts = processReaderVisitRecords(readerVisitReportsList);
            visitorTimesList.add(day.toString());
            visitorCountsList.add(dailyCounts.get("visitors"));
        }

        ArrayList<Object> data = new ArrayList<>();
        data.add(visitorTimesList);
        data.add(visitorCountsList);

        result.put("success", "ok");
        result.put("status", "ok");
        result.set("visitor_counts", Json.toJson(data));
        return ok(result);
    }


    @Security.Authenticated(Secured.class)
    public Result getMonthToDateVisitorCounts(String readerDeviceId) {
        Organization myOrg = organization();
        ObjectNode result = Json.newObject();
        Device readerDevice = Device.findById(readerDeviceId);
        User myUser = User.findByEmail(session().get("email"), myOrg);

        if (readerDevice == null) {
            return forbidden();
        }

        if (!readerDevice.getOrganizationId().equals(myOrg.getId()) && !myUser.getSuperadmin()) {
            return forbidden();
        }

        List<Object> visitorTimesList = new ArrayList<>();
        visitorTimesList.add("x");

        List<Object> visitorCountsList = new ArrayList<>();
        visitorCountsList.add("data1");

        int today = LocalDate.now().getDayOfMonth();
        LocalDate firstDay = LocalDate.now().minusDays(today);

        for (int k = 1; k < today; k++) {
            // Daily report is always 1 day before.
            LocalTime midnight = LocalTime.MIDNIGHT;
            LocalDate day = firstDay.plusDays(k);

            LocalDateTime startTime = LocalDateTime.of(day, midnight);
            LocalDateTime startTimeEST = startTime.plusHours(4);
            LocalDateTime endTimeEST = startTimeEST.plusHours(24);

            List<ReaderVisitReport> readerVisitReportsList = ReaderVisitReport
                    .findAllByReaderDeviceWithTimeWindow(readerDevice, Utils.getTimestamp(startTimeEST), Utils.getTimestamp(endTimeEST));

            if (readerVisitReportsList.size() == 0) {
                visitorTimesList.add(day.toString());
                visitorCountsList.add(0);
                continue;
            }

            Map<String, Integer> dailyCounts = processReaderVisitRecords(readerVisitReportsList);
            visitorTimesList.add(day.toString());
            visitorCountsList.add(dailyCounts.get("visitors"));
        }

        ArrayList<Object> data = new ArrayList<>();
        data.add(visitorTimesList);
        data.add(visitorCountsList);

        result.put("success", "ok");
        result.put("status", "ok");
        result.set("visitor_counts", Json.toJson(data));
        return ok(result);
    }

    private Map<String, Integer> processReaderVisitRecords(List<ReaderVisitReport> readerVisitReportsList) {

        int visitors = 0;
        int androids = 0;
        int apples = 0;

        for (ListIterator<ReaderVisitReport> iterator = readerVisitReportsList.listIterator(); iterator.hasNext(); ) {

            int difference = 0;
            int appleDifference = 0;
            int androidDifference = 0;

            if (iterator.nextIndex() == 0) {
                ReaderVisitReport first = iterator.next();
                visitors = (first.getAppleCount() + first.getAndroidCount());
                iterator.previous();
            }

            ReaderVisitReport value = iterator.next();
            ReaderVisitReport nextValue;

            if (iterator.hasNext()) {
                nextValue = iterator.next();
                // Reset the state of the iterator
                try {
                    iterator.previous();
                    difference = (nextValue.getAppleCount() + nextValue.getAndroidCount()) - (value.getAppleCount() + value.getAndroidCount());
                    appleDifference = nextValue.getAppleCount() - value.getAppleCount();
                    androidDifference = nextValue.getAndroidCount() - value.getAndroidCount();
                } catch (NoSuchElementException e) {
                    difference = value.getDevicesCount();
                }
            }

            if (difference > 0) {
                visitors += difference;
            }

            if (appleDifference > 0) {
                apples += appleDifference;
            }

            if (androidDifference > 0) {
                androids += androidDifference;
            }
        }

        Map<String, Integer> dailyCounts = new HashMap<>();
        dailyCounts.put("visitors", visitors);
        dailyCounts.put("androids", androids);
        dailyCounts.put("apples", apples);
        return dailyCounts;
    }

}
