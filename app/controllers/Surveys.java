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
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import utils.Mailer;

import java.util.LinkedHashMap;
import java.util.List;

/*
 * This controller contains Messages app common logic
 */

@Security.Authenticated(Secured.class)
public class Surveys extends BaseController {

    /*
    # Surveys
    ### NoDocs ###
    POST        /app/surveys                                                         controllers.Surveys.post
    */

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */


    public Result post() {

        // Add user who created the message as an assignee.
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        if(!myUser.getSuperadmin()){
            return badRequest();
        }

        JsonNode json = request().body().asJson();
        JsonNode users = json.get("users");
        String httpLink = json.get("http_link").asText(null);
        String subject = json.get("subject").asText(null);
        String header = json.get("header").asText(null);
        String content = json.get("content").asText(null);


        if(httpLink == null || subject == null || content == null || header == null) {
            return badRequest();
        }

        List<LinkedHashMap> submittedUsers = Json.fromJson(users, List.class);

        try {

            // Send survey to user.
            for (LinkedHashMap<String, String> submittedUser : submittedUsers) {
                Object vId = submittedUser.get("id");
                User surveyUser = User.findById(((String) vId));
                Mailer.MailNewSurveyEmail(surveyUser, httpLink, subject, header, content);
            }

            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            return ok(result);

        } catch (Exception e){
            return internalServerError();
        }

    }

}