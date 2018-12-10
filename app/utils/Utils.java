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

package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import models.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String getToken() {
        return "";
    }

    public static String getRecaptchaSecret() {
        return conf.getString("deployment.google_recaptcha_secret");
    }

    public static String getEmailAccountPassword() {
        return conf.getString("deployment.gmail_user_password");
    }

    public static String getGimbalUrl() {
        return conf.getString("deployment.gimbal_url");
    }

    public static String getGimbalToken() {
        return conf.getString("deployment.gimbal_token");
    }

    public static JsonNode textToJsonNode(String jsonText) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(jsonText);
            return jsonNode;
        } catch (IOException e) {
            Logger.error("Error converting json text to json node - " + e.getMessage());
            return null;
        }
    }

    private enum DeploymentType {
        LOCAL, LIVE,
    }

    private static Config conf = ConfigFactory.load();

    public static String getDomain() {
        return conf.getString("deployment.cookie_domain");
    }

    public static String getPortalURL() {
        return getDeploymentHttpProtocol() +
                conf.getString("deployment.portal_url");
    }

    public static String getEngageURL() {
        return getDeploymentHttpProtocol() +
                conf.getString("deployment.engage_url");
    }

    /*
     * Login URL should always be <protocol>://workspace_name.WrrKrr.com:<port>/...
     * */
    public static String getLoginlinkURL(User user) {
        return getDeploymentHttpProtocol() + user.getOrganization().getWorkspaceName() + "." +
                conf.getString("deployment.login_url");
    }

    public static String getHomeDirectory() {
        return conf.getString("deployment.home_directory");
    }

    public static String getScannerDirectory() {
        return conf.getString("deployment.scanner_directory");
    }

    public static String getDeviceTypeName() {
        return conf.getString("deployment.device_type_name");
    }

    public static String getDeviceExternalId() {
        try {
            return conf.getString("deployment.device_external_id");
        } catch (ConfigException e) {
            return null;
        }
    }

    public static String getDeviceVersionNumber() {
        return conf.getString("deployment.device_version_number");
    }

    public static String getDeviceProductReleaseId() {
        return conf.getString("deployment.device_product_release_id");
    }

    public static String getDeviceProductId() {
        return conf.getString("deployment.device_product_id");
    }

    public static String getDeploymentHttpProtocol() {
        return conf.getString("deployment.http_protocol") + "://";
    }

    public static String getAuthyKey() {
        return conf.getString("deployment.authy_api_key");
    }

    public static String getAWSImageUrlScheme() {
        return conf.getString("aws.aws_image_url_scheme");
    }

    public static String getDeploymentType() {
        return conf.getString("deployment.instance_type");
    }

    public static String getDeploymentMode() {
        return conf.getString("deployment.instance_mode");
    }

    public static Boolean isAutoRegistrationEnabled() {
        return conf.getBoolean("deployment.auto_registration");
    }

    public static boolean isDebugMode() {
        if (getDeploymentType().toUpperCase().equals(DeploymentType.LOCAL.toString())) {
            return true;
        } else if (getDeploymentType().toUpperCase().equals(DeploymentType.LIVE.toString())) {
            return false;
        } else {
            return true;
        }
    }

    public static String getEmailDomain(String email) {

        Pattern regex = Pattern.compile("(?<=@)\\S+");
        Matcher regexMatcher = regex.matcher(email);

        if (regexMatcher.find()) {
            return regexMatcher.group();
        } else {
            return null;
        }
    }

    public static String getTimeIn8601(Timestamp t) {
        // same time in millis
        Instant fromEpochMilli = Instant.ofEpochMilli(t.getTime());
        return fromEpochMilli.toString();
    }

    public static void sleep(float seconds) {
        try {
            Thread.currentThread();
            Thread.sleep((int) (seconds * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String generatePin() {
        Random generator = new Random();
        generator.setSeed(System.currentTimeMillis());

        int num = generator.nextInt(99999) + 99999;
        if (num < 100000 || num > 999999) {
            num = generator.nextInt(99999) + 99999;
            if (num < 100000 || num > 999999) {
                return null;
            }
        }
        return String.valueOf(num);
    }

    public static String generateRandomString() {
        RandomString random = new RandomString(6);
        return random.nextString();
    }

    private static SecureRandom random = new SecureRandom();

    public static String nextSessionId() {
        return new BigInteger(256, random).toString(32);
    }

    public static Timestamp getCurrentTimestamp() {
        java.util.Date date = new java.util.Date();
        return new Timestamp(date.getTime());
    }

    public static long compareTwoTimeStamps(Timestamp currentTime, Timestamp oldTime) {
        long milliseconds1 = oldTime.getTime();
        long milliseconds2 = currentTime.getTime();

        long diff = milliseconds2 - milliseconds1;
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        return diffMinutes;
    }

    public static Timestamp getTimestamp(LocalDateTime t) {
        return Timestamp.valueOf(t);
    }

    public static Timestamp getTimestamp(ZonedDateTime t) {
        return Timestamp.valueOf(t.toLocalDateTime());
    }

    /**
     * Returns the {@link Date} of midnight at the start of the given {@link Date}.
     *
     * <p>This returns a {@link Date} formed from the given {@link Date} at the time of midnight,
     * 00:00, at the start of this {@link Date}.
     *
     * @return the {@link Date} of midnight at the start of the given {@link Date}
     */
    public static Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    /**
     * Returns the {@link Date} at the end of day of the given {@link Date}.
     *
     * <p>This returns a {@link Date} formed from the given {@link Date} at the time of 1 millisecond
     * prior to midnight the next day.
     *
     * @return the {@link Date} at the end of day of the given {@link Date}j
     */
    public static Date atEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static JsonNode convertJsonFormat(JSONObject json) {
        ObjectNode ret = JsonNodeFactory.instance.objectNode();

        @SuppressWarnings("unchecked")
        Iterator<String> iterator = json.keys();
        for (; iterator.hasNext(); ) {
            String key = iterator.next();
            Object value;
            try {
                value = json.get(key);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            if (json.isNull(key))
                ret.putNull(key);
            else if (value instanceof String)
                ret.put(key, (String) value);
            else if (value instanceof Integer)
                ret.put(key, (Integer) value);
            else if (value instanceof Long)
                ret.put(key, (Long) value);
            else if (value instanceof Double)
                ret.put(key, (Double) value);
            else if (value instanceof Boolean)
                ret.put(key, (Boolean) value);
            else if (value instanceof JSONObject)
                ret.put(key, convertJsonFormat((JSONObject) value));
            else if (value instanceof JSONArray)
                ret.put(key, convertJsonFormat((JSONArray) value));
            else
                throw new RuntimeException("not prepared for converting instance of class " + value.getClass());
        }
        return ret;
    }

    public static JsonNode convertJsonFormat(JSONArray json) {
        ArrayNode ret = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < json.length(); i++) {
            Object value;
            try {
                value = json.get(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            if (json.isNull(i))
                ret.addNull();
            else if (value instanceof String)
                ret.add((String) value);
            else if (value instanceof Integer)
                ret.add((Integer) value);
            else if (value instanceof Long)
                ret.add((Long) value);
            else if (value instanceof Double)
                ret.add((Double) value);
            else if (value instanceof Boolean)
                ret.add((Boolean) value);
            else if (value instanceof JSONObject)
                ret.add(convertJsonFormat((JSONObject) value));
            else if (value instanceof JSONArray)
                ret.add(convertJsonFormat((JSONArray) value));
            else
                throw new RuntimeException("not prepared for converting instance of class " + value.getClass());
        }
        return ret;
    }


}


 