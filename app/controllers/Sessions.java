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
import models.Session;
import models.User;
import play.libs.Json;
import play.mvc.Result;

/*
 * This controller contains  app Session common logic
 */

public class Sessions extends BaseController {

    public Result getSession() {
        Session mySession = Session.findBySessionId(session().get("session_id"));
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        if(mySession == null){
            return ok(result);
        }
        // If session is not null then..
        User myUser = User.findByEmail(session().get("email"), organization());
        if(myUser != null){
            result.set("user", Json.toJson(myUser));
            result.set("session", Json.toJson(mySession));
            result.set("is_two_factored", Json.toJson((mySession.isTwoFactored)));
        }
        return ok(result);
    }

    public Result get(String sessionId) {
        Session mySession = Session.findBySessionId(sessionId);
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        if(mySession == null){
            return ok(result);
        } else {
            User myUser =  User.findByEmail(mySession.getEmail(), organization());
            if(mySession ==  null || myUser == null){
                // Do nothing
            } else {
                result.set("user", Json.toJson(myUser));
                result.set("session", Json.toJson(mySession));
                result.set("is_two_factored", Json.toJson((mySession.isTwoFactored)));

            }
            return ok(result);
        }
    }

    public Result removeSession() {
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        JsonNode json = request().body().asJson();
        String sessionId = json.get("session_id").asText();
        if(sessionId == null){
            sessionId = session().get("session_id");
        }
        if(sessionId != null){
            Session mySession = Session.findBySessionId(sessionId);
            User myUser =  User.findByEmail(mySession.getEmail(), organization());

            mySession.invalidate();
            mySession.save();
        }
        response().discardCookie("session_id");
        session().clear();
        return ok(result);
    }

    public Result remove(String sessionId) {
        Session mySession = Session.findBySessionId(sessionId);
        User myUser =  User.findByEmail(mySession.getEmail(), organization());
        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        mySession.invalidate();
        mySession.save();
        response().discardCookie("session_id");
        session().clear();
        return ok(result);
    }

    public Result updateApp(String sessionUUID) {
        Session mySession = Session.findBySessionId(sessionUUID);
        ObjectNode result = Json.newObject();
        JsonNode json = request().body().asJson();
        String app = null;
        if (json.get("app") != null) {
            app = json.get("app").asText();
        }
        mySession.setApp(app);
        mySession.save();
        result.put("status", "ok");
        result.put("success", "ok");
        if(mySession == null){
            return ok(result);
        } else {
            User myUser =  User.findByEmail(mySession.getEmail(), organization());
            if(mySession ==  null || myUser == null){
                // Do nothing
            } else {
                result.set("user", Json.toJson(myUser));
                result.set("session", Json.toJson(mySession));
                result.set("is_two_factored", Json.toJson((mySession.isTwoFactored)));
            }
            return ok(result);
        }
    }

}