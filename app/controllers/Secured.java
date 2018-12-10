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
import models.Session;
import models.User;
import play.Logger;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;
import utils.Utils;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        String email = ctx.session().get("email");
        String organizationId = ctx.session().get("organization_id");

        // If User does not exist then return null
        if (organizationId == null) {
            Logger.info("Organization cookie not detected.");
            discardCookie(ctx);
            return null;
        }

        User myUser = User.findByEmail(email, Organization.findById(organizationId));
        // If User does not exist then return null
        if (myUser == null) {
            Logger.info("Unauthorized cookie detected - " + ctx.session().get("email") + ".");
            discardCookie(ctx);
            return null;
        }

        // If Session does not exist then return null
        Session mySession = Session.findBySessionId(ctx.session().get("session_id"));

        if (mySession == null) {
            Logger.info("Unknown cookie detected - " + ctx.session().get("email"));
            discardCookie(ctx);
            return null;
        }

        if (mySession.isExpired() || !myUser.getEnabled()) {
            Logger.info("Unauthorized cookie detected - " + ctx.session().get("email"));
            discardCookie(ctx);
            return null;
        }

        return ctx.session().get("email");
    }

    private void discardCookie(Context ctx) {
        ctx.response().discardCookie("session_id", "/", Utils.getDomain());
        ctx.response().discardCookie("organization_id", "/", Utils.getDomain());
        ctx.session().clear();
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return unauthorized();
    }
}