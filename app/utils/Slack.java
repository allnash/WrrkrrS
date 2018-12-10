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

import models.Organization;
import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackMessage;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

public class Slack {

    // Post a slack message to the organization slack channel provided for the URL.
    public static void postMessage(Organization to, String message, String link) {
        // Send to all these orgs
        List<Organization> organizations = new ArrayList<>();
        organizations.add(to);
        // Duplicate check.
        for (Organization o : to.getCollaborators()) {
            if (!o.getEmailDomain().equals(to.getEmailDomain())) {
                organizations.add(o);
            }
        }
        for (Organization o : organizations) {
            if (o.getSlackHook() != null) {
                if (!o.getSlackHook().isEmpty()) {
                    String text = message + " - inside organization - " + to.getName() + ".";
                    if (link != null) {
                        text = text.concat(" <" + link + "|Click here> for details!");
                    }
                    send(o.getSlackHook(), text);
                    Logger.info("Sent slack message to organization - " + o.getName());
                }
            } else {
                Logger.warn("Not sending slack message to organization + " + o.getName() + " missing slack webhook.");
            }
        }
    }

    private static void send(String url, String text) {
        SlackApi api = new SlackApi(url);
        api.call(new SlackMessage(text));
    }

}
