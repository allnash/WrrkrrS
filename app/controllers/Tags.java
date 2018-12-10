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

import models.*;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.*;

/*
 * This controller contains Products app common logic
 */

@Security.Authenticated(Secured.class)
public class Tags extends Controller {

    /*
    *
    * # Marketplace Product Tags
    * ### NoDocs ###
    * GET         /app/tags                                                             controllers.Tags.getAll()
    */

    @Security.Authenticated(Secured.class)
    public Result getAll(String query) {

        ArrayList<String> tags = new ArrayList<>();
        if(query != null){
            List<Tag> searchedTags = Tag.all(query);
            for(Tag t: searchedTags){
                tags.add(t.getText());
            }
        }
        return ok(Json.toJson(tags));
    }

}