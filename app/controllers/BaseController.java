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

import models.Organization;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import utils.Utils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Superclass for a Java-based Omegatrace controller.
 */
public abstract class BaseController extends Controller {

    /**
     * Returns the current Organization in context.
     *
     * @return the organization
     */
    public static Organization organization() {
        String domain = getDomainName(Utils.getDeploymentHttpProtocol() +
                request().host());
        return Organization.findByWorkspaceName(getWorkspaceName(domain));
    }

    public static String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException | NullPointerException e) {
            Logger.error("Error parsing domain - " + url);
            return "";
        }
    }

    public static String getWorkspaceName(String domain) {
        try {
            String[] separated = domain.split("\\.");
            if (separated.length == 3) {
                return separated[0];
            } else {
                return "";
            }
        } catch (NullPointerException e) {
            Logger.error("Error parsing domain");
            return "";
        }
    }

}
