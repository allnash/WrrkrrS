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

import models.User;
import play.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static utils.Utils.getLoginlinkURL;

public class Mailer {


    public static void SendEmail(String toEmail, String emailSubject, String emailContent) {
        final String FROM_EMAIL_ACCOUNT = "engage@omegatrace.com";
        final String FROM_EMAIL_ACCOUNT_PASSWORD = Utils.getEmailAccountPassword();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(FROM_EMAIL_ACCOUNT, FROM_EMAIL_ACCOUNT_PASSWORD);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL_ACCOUNT));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toEmail));
            message.setSubject(emailSubject);
            message.setContent(emailContent, "text/html; charset=utf-8");
            Transport.send(message);
            String trimmedToEmail = toEmail.split("@")[1];
            Logger.info("Email sent successfully - TO_EMAIL:" + "***" + trimmedToEmail);
            Logger.debug("Email sent - FROM_EMAIL:" + toEmail + " - SUBJECT: " + emailSubject);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public static void MailReleaseEmail(String email) {
        Stream<String> releaseNotesFile = null;
        String fileName = "conf/RELEASE";
        try {
            releaseNotesFile = Files.lines(Paths.get(fileName));
        } catch (IOException e) {

        }
        String subject = "OmegaTrace: release completed on - " + Calendar.getInstance().getTime();
        List<String> releaseNotes = new ArrayList<>();
        if (releaseNotesFile != null) {
            StringBuilder data = new StringBuilder();
            releaseNotesFile.forEach(line -> releaseNotes.add(line));
            releaseNotesFile.close();
        }

        String content = views.html.email.release_email.render(releaseNotes).toString();
        SendEmail(email, subject, content);
    }

    public static void MailPasswordResetEmail(User user) {
        String subject = "OmegaTrace: You requested a password reset on - " + Calendar.getInstance().getTime();
        String link = getLoginlinkURL(user) + "/index.html#/passwordreset?email=" + user.getEmail() + "&reset_token=" + user.getResetToken() + "&workspace_name=" + user.getOrganization().getWorkspaceName();
        String content = views.html.email.password_reset_email.render(user.getEmail(), link).toString();
        SendEmail(user.getEmail(), subject, content);
    }

    public static void MailNewAccountEmail(User user) {
        String subject = "OmegaTrace: Welcome to WrrKrr, the OmegaTrace engagement tool.";
        String link = getLoginlinkURL(user) + "/index.html#/confirmaccount?email=" + user.getEmail() + "&confirmation_hash=" + user.getConfirmationHash() + "&workspace_name=" + user.getOrganization().getWorkspaceName();
        String content = views.html.email.confirm_email.render(user.getEmail(), link).toString();
        SendEmail(user.getEmail(), subject, content);
    }

    public static void MailNewAccountInviteEmail(User user, User by) {
        String subject = "OmegaTrace: Hi " + user.getFirstName() + "! You have been invited to join `" + user.getOrganization().getWorkspaceName() +
                "` team on omegatrace by " + by.getFullName() + '.';
        String link = getLoginlinkURL(user) + "/index.html#/confirmaccount?email=" + user.getEmail() + "&confirmation_hash=" + user.getConfirmationHash() + "&workspace_name=" + user.getOrganization().getWorkspaceName();
        String content = views.html.email.confirm_email.render(user.getEmail(), link).toString();
        SendEmail(user.getEmail(), subject, content);
    }

    public static void MailPasswordResetNotificationEmail(User user) {
        String subject = "OmegaTrace: Your login password was changed Recently on - " + Calendar.getInstance().getTime();
        String content = views.html.email.password_change_notification.render(user.getFirstName()).toString();
        SendEmail(user.getEmail(), subject, content);
    }

    public static void MailNewSurveyEmail(User user, String link, String subject, String header, String content) {
        String subjectString = "OmegaTrace: Hi " + user.getFirstName() + "!, " + subject;
        String contentString = views.html.email.new_survey_email.render(user.getEmail(), link, header, content).toString();
        SendEmail(user.getEmail(), subjectString, contentString);
    }

}
