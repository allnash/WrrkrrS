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


import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.apache.commons.codec.binary.Base64;

import play.Logger;
import play.Logger.ALogger;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;

public class BasicAuthAction extends Action<Result> {
    private static ALogger log = Logger.of(BasicAuthAction.class);

    private static final String AUTHORIZATION = "Authorization";
    private static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    private static final String REALM = "Basic realm=\"Realm\"";

    @Override
    public CompletionStage<Result> call(Context context) {
        String authHeader = context.request().getHeader(AUTHORIZATION);
        if (authHeader == null) {
            context.response().setHeader(WWW_AUTHENTICATE, REALM);
            return CompletableFuture.completedFuture(status(Http.Status.UNAUTHORIZED, "Needs authorization"));
        }

        String[] credentials;
        try {
            credentials = parseAuthHeader(authHeader);
        } catch (Exception e) {
            log.warn("Cannot parse basic auth info", e);
            return CompletableFuture.completedFuture(status(Http.Status.FORBIDDEN, "Invalid auth header"));
        }

        String username = credentials[0];
        String password = credentials[1];
        boolean loginCorrect = checkLogin(username, password);

        if (!loginCorrect) {
            log.warn("Incorrect basic auth login, username=" + username);
            return CompletableFuture.completedFuture(status(Http.Status.FORBIDDEN, "Forbidden"));
        } else {
            //context.request().setUsername(username);
            log.info("Successful basic auth login, username=" + username);
            return delegate.call(context);
        }
    }

    private String[] parseAuthHeader(String authHeader) throws UnsupportedEncodingException {
        if (!authHeader.startsWith("Basic ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        String[] credString;
        String auth = authHeader.substring(6);
        byte[] decodedAuth = new Base64().decode(auth);
        credString = new String(decodedAuth, "UTF-8").split(":", 2);
        if (credString.length != 2) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        return credString;
    }

    private boolean checkLogin(String username, String password) {
        /// change this
        return username.equals("vlad");
    }
}