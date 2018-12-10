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

import play.Logger;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

import java.util.Optional;

public class SecuredApi extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        String authorizationToken = getTokenFromHeader(ctx);
        try {
            if(authorizationToken.equals("1234567")){
                return "";
            } else {
                return null;
            }
        } catch (NullPointerException e){
            Logger.error("Authorization Token Missing.");
            return null;
        }
    }


    private String getTokenFromHeader(Http.Context ctx) {
        Optional<String> authTokenHeaderValue = ctx.request().header("XAUTHTOKEN");
        return authTokenHeaderValue.orElse(null);
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return unauthorized();
    }
}
