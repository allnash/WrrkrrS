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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.CaseFormat;
import models.Session;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import utils.Utils;

import static play.mvc.Results.badRequest;

/**
 * Created by ngadre on 2/26/16.
 */
public class ControllerHelper {

    public static String jsonKeyNameForClass(Class c) {
        String className = c.getSimpleName();
        className = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
        return className;
    }

    public static Result standardParseErrorResponse() {
        ObjectNode result = Json.newObject();
        result.put("status", "error");
        result.put("error", "parse");
        return badRequest(result);
    }

    public static Result standardDuplicateIDErrorResponse() {
        ObjectNode result = Json.newObject();
        result.put("status", "error");
        result.put("error", "Duplicate External ID");
        return badRequest(result);
    }

    public static Result standardObjectMissingError(Class c) {
        ObjectNode result = Json.newObject();
        result.put("status", "error");
        result.put("error", "Data missing for - " + jsonKeyNameForClass(c));
        return badRequest(result);
    }

    public static Result standardNullPointerErrorResponse() {
        ObjectNode result = Json.newObject();
        result.put("status", "error");
        result.put("error", "object was not saved properly");
        return badRequest(result);
    }

    public static Result standardDataValidationErrorResponse() {
        ObjectNode result = Json.newObject();
        result.put("status", "error");
        result.put("error", "invalid data");
        return badRequest(result);
    }

    public static Result standardInvalidUserErrorResponse() {
        ObjectNode result = Json.newObject();
        result.put("status", "error");
        result.put("error", "invalid user");
        return badRequest(result);
    }

    public static Http.Cookie getSessionCookie(Session s) {
        String name = "session_id";         // name
        String value = s.sessionId;         // value
        int age = 3600 * 12;                // maximum age
        String path = "/";                  // path
        String domain = Utils.getDomain();  // domain
        boolean secure = false;             // secure
        boolean http_only = true;          // http only
        return new Http.Cookie(name, value, age, path, domain, secure, http_only);
    }

    public static Http.Cookie getOrganizationCookie(Session s) {
        String name = "organization_id";         // name
        String value = s.getOrganization().getId();         // value
        int age = 3600 * 12;                // maximum age
        String path = "/";                  // path
        String domain = Utils.getDomain();  // domain
        boolean secure = false;             // secure
        boolean http_only = true;          // http only
        return new Http.Cookie(name, value, age, path, domain, secure, http_only);
    }

    public static ObjectNode buildJsonResponse(String type, String message) {
        ObjectNode result = Json.newObject();
        result.put("status", "fail");
        result.put(type, message);
        return result;
    }


    public static ObjectNode addErrors(String type, String message) {
        ObjectNode wrapper = Json.newObject();
        ObjectNode msg = Json.newObject();
        msg.put("message", message);
        wrapper.set(type, msg);
        return wrapper;
    }

}
