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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.ebean.Finder;
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.util.*;

@Entity
public class Message extends BaseModel {


    /**   ^^^^^
     * Message Table
     */

    /**
     * Message Type
     */

    public enum MessageType {
        WARNING, FAULT, NEW_FEATURE, ALERT, REMINDER, TODOS, SYSTEM, IMPORTANT, PERSONAL;
    }

    public enum MessageReadState {
        UNREAD, READ
    }

    public enum MessageState {
        INBOX, ARCHIVED, DELETED
    }

    @JsonProperty("external_id")
    public String externalId;
    @JsonProperty("subject")
    public String subject;
    @JsonIgnore
    @Transient
    public String descriptionHtml;
    @JsonIgnore
    @OneToOne
    public Html description;
    @JsonProperty("when_read")
    public Timestamp whenRead;
    @JsonProperty("message_type")
    public MessageType messageType;
    @Transient
    @JsonProperty("from")
    public JsonNode from;
    @JsonIgnore
    @ManyToOne
    public User fromUser;
    @Transient
    @JsonProperty("to")
    public JsonNode to;
    @JsonIgnore
    @ManyToOne
    public User toUser;
    @JsonProperty("message_read_state")
    public MessageReadState messageReadState;
    @JsonProperty("message_state")
    public MessageState messageState;
    public boolean starred;


    public Message() {
        this.generateMessageId();
    }

    public Message(String subject, Html description, User fromUser, User toUser) {
        this.generateMessageId();
        this.subject = subject;
        this.description = description;
        this.messageType = MessageType.PERSONAL;
        this.messageReadState =  MessageReadState.UNREAD;
        this.messageState = MessageState.INBOX;
        this.fromUser = fromUser;
        this.toUser =  toUser;
        this.starred = false;
    }

    public static Finder<Long, Message> find = new Finder<>(Message.class);

    public static Message findByExternalId(String externalId) {
        return find.query().where().eq("external_id", externalId).findOne();
    }

    public static Message findByExternalIdToUser(String externalId, User toUser) {
        return find.query().where().eq("external_id", externalId)
                .eq("to_user_id", toUser.getId()).findOne();
    }

    public static Message findByExternalIdFromUser(String externalId, User fromUser) {
        return find.query().where().eq("external_id", externalId)
                .eq("from_user_id", fromUser.getId()).findOne();
    }

    public static List<Message> findReceivedMessages(User toUser) {
        return find.query().where()
                .eq("to_user_id", toUser.getId()).findList();
    }

    public static List<Message> findSentMessages(User fromUser) {
        return find.query().where()
                .eq("from_user_id", fromUser.getId()).findList();
    }

    public void generateMessageId() {
        if (this.externalId == null || this.externalId.equals("")) {
            this.externalId = UUID.randomUUID().toString();
        }
    }

    public void markRead() {
        this.whenRead = new Timestamp(Calendar.getInstance().getTimeInMillis());
        this.save();
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Timestamp getWhenRead() {
        return whenRead;
    }

    public void setWhenRead(Timestamp whenRead) {
        this.whenRead = whenRead;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public JsonNode getFrom() {
        ObjectNode from = Json.newObject();
        from.put("id", this.fromUser.getId());
        from.put("email", this.fromUser.getEmail());
        from.put("full_name", this.fromUser.getFullName());
        from.put("initials", this.toUser.getInitials());
        return from;
    }

    public JsonNode getTo() {
        ObjectNode to = Json.newObject();
        to.put("id", this.toUser.getId());
        to.put("email", this.toUser.getEmail());
        to.put("full_name", this.toUser.getFullName());
        to.put("initials", this.toUser.getInitials());
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Html getDescription() {
        return description;
    }

    public void setDescription(Html description) {
        this.description = description;
    }

    public String getDescriptionHtml() {
        if(this.description != null){
            return this.description.getData();
        } else {
            return null;
        }
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public MessageReadState getMessageReadState() {
        return messageReadState;
    }

    public void setMessageReadState(MessageReadState messageReadState) {
        this.messageReadState = messageReadState;
    }

    public MessageState getMessageState() {
        return messageState;
    }

    public void setMessageState(MessageState messageState) {
        this.messageState = messageState;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

}
