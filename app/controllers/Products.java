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

import java.util.*;

/*
 * This controller contains Products app common logic
 */

@Security.Authenticated(Secured.class)
public class Products extends BaseController {


    public Result get(String productId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        if (!myUser.getRole().getMarketplaceModuleAccess().equals(Role.RoleAccess.NONE)
                || !myUser.getRole().getProjectsModuleAccess().equals(Role.RoleAccess.NONE)
                || !myUser.getRole().getTeamsModuleAccess().equals(Role.RoleAccess.NONE)
                || !myUser.getRole().getIssuesModuleAccess().equals(Role.RoleAccess.NONE)
                || !myUser.getRole().getWorkflowModuleAccess().equals(Role.RoleAccess.NONE)) {
            ObjectNode result = Json.newObject();
            Product product = Product.findById(productId);
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("product", Json.toJson(product));
            return ok(result);
        } else {
            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result getAll() {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        ObjectNode result = Json.newObject();
        List<Product> products = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        products = Product.all(parameters);
        result.set("products", Json.toJson(products));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result post() {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        if(!myUser.getSuperadmin()) {
            return badRequest();
        }

        try {
            JsonNode json = request().body().asJson();
            Product submittedProduct = Json.fromJson(json, Product.class);
            submittedProduct.setBy(myUser);
            submittedProduct.save();
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("product", Json.toJson(submittedProduct));
            return ok(result);
        } catch (Exception e){
            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result put(String id) {
        // Check if the user has Role access
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        if(!myUser.getSuperadmin()) {
            return badRequest();
        }
        Product product = Product.findById(id);
        // merge logic
        try {
            JsonNode json = request().body().asJson();
            Product submittedProduct = Json.fromJson(json, Product.class);
            product.setName(submittedProduct.name);
            product.setDescription(submittedProduct.description);
            product.setNotes(submittedProduct.notes);
            product.setEnabled(submittedProduct.enabled);
            product.setVerified(submittedProduct.verified);
            product.save();
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("product", Json.toJson(product));
            return ok(result);
        }
        catch (Exception e){
            return badRequest();
        }
    }


}