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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.io.IOException;
import java.util.List;

/*
 * This controller contains Messages app common logic
 */

@Security.Authenticated(Secured.class)
public class Messages extends BaseController {

    /*
    # User Messages
    ### NoDocs ###
    GET         /app/messages                                                        controllers.Messages.getAll()
    ### NoDocs ###
    GET         /app/messages/:id                                                    controllers.Messages.get(id: String)
    ### NoDocs ###
    POST        /app/messages                                                        controllers.Messages.post()
    ### NoDocs ###
    PUT         /app/messages/:id                                                    controllers.Messages.put(id: String)
    */

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result getAll() {

        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        // Only allow super_admin > 0 to fetch by org
        if (myUser.getRole().getMessagesModuleAccess().equals(Role.RoleAccess.NONE)) {
            return badRequest();
        }

        ObjectNode messages = Json.newObject();

        List<Message> received =  Message.findReceivedMessages(myUser);
        List<Message> sent = Message.findSentMessages(myUser);
        messages.set("received", Json.toJson(received));
        messages.set("sent", Json.toJson(sent));
        messages.set("size", Json.toJson(received.size()));


        ObjectNode result = Json.newObject();
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("messages", messages);
        return ok(result);
    }

    public Result get(String externalId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        // Only allow super_admin > 0 to fetch by org
        if (myUser.getRole().getMessagesModuleAccess().equals(Role.RoleAccess.NONE)) {
            return badRequest();
        }

        Message message = Message.findByExternalId(externalId);

        if(message == null){
            return badRequest();
        }

        // Check if the user is a sender or not.
        if(!message.getFromUser().equals(myUser) && !message.getToUser().equals(myUser)){
            return badRequest();
        }

        try {
            ObjectNode result = Json.newObject();
            result.set("message",  Json.toJson(message));
            result.put("message_description_html", message.getDescriptionHtml());
            result.put("status", "ok");
            result.put("success", "ok");
            return ok(result);

        } catch (Exception e){
            return internalServerError();
        }
    }

    public Result post() {

        JsonNode json = request().body().asJson();

        String toUserId = json.get("to_user_id").asText( null);
        String subject = json.get("subject").asText(null);
        String messageType = json.get("message_type").asText(null);
        String descriptionHtml = json.get("description_html").asText(null);

        if(toUserId == null || subject == null || descriptionHtml == null || messageType == null){
            return badRequest();
        }

        // Add user who created the message as an assignee.
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        User fromUser = User.findByEmail(session().get("email"), myOrg);

        try {

            Html description = new Html(descriptionHtml, fromUser.getOrganization());
            description.save();

            Message submittedMessage = new Message(subject,description, fromUser, User.findById(toUserId));
            submittedMessage.setMessageType(Message.MessageType.valueOf(messageType));
            submittedMessage.save();

            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("message", Json.toJson(submittedMessage));

            return ok(result);


        } catch (Exception e){
            return internalServerError();
        }



    }

    public Result put(String externalId) {
        return ok("");
    }

    public Result putRead(String externalId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        // Only allow super_admin > 0 to fetch by org
        if (myUser.getRole().getMessagesModuleAccess().equals(Role.RoleAccess.NONE)) {
            return badRequest();
        }

        Message message = Message.findByExternalId(externalId);

        if(message == null){
            return badRequest();
        }

        // Check if the user is a sender or not.
        if(!message.getFromUser().equals(myUser) && !message.getToUser().equals(myUser)){
            return badRequest();
        }

        // Mark as read.
        if(!message.getToUser().equals(myUser)){
            message.setMessageReadState(Message.MessageReadState.READ);
            message.save();
        }

        try {
            ObjectNode result = Json.newObject();
            result.set("message",  Json.toJson(message));
            result.put("message_description_html", message.getDescriptionHtml());
            result.put("status", "ok");
            result.put("success", "ok");
            return ok(result);

        } catch (Exception e){
            return internalServerError();
        }
    }

    public Result putStarred(String externalId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        // Only allow super_admin > 0 to fetch by org
        if (myUser.getRole().getMessagesModuleAccess().equals(Role.RoleAccess.NONE)) {
            return badRequest();
        }

        Message message = Message.findByExternalId(externalId);

        if(message == null){
            return badRequest();
        }

        // Check if the user is a sender or not.
        if(!message.getFromUser().equals(myUser) && !message.getToUser().equals(myUser)){
            return badRequest();
        }

        Logger.info("Message starred by user - " + myUser.getId());

        // Set if its starred or unstarred.
        if(message.getToUser().equals(myUser)){
            if(message.isStarred()){
                message.setStarred(false);
            } else {
                message.setStarred(true);
            }
            message.save();
        }

        message = Message.findByExternalId(externalId);

        try {
            ObjectNode result = Json.newObject();
            result.set("message",  Json.toJson(message));
            result.put("message_description_html", message.getDescriptionHtml());
            result.put("status", "ok");
            result.put("success", "ok");
            return ok(result);

        } catch (Exception e){
            return internalServerError();
        }
    }

}