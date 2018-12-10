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
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import utils.S3Manager;
import utils.Utils;

import java.io.File;
import java.util.*;

import static models.DataMigration.OMEGATRACE_DOMAIN;

/*
 * This controller contains Products app common logic
 */

public class ProductReleases extends BaseController {

    /*
     * # System Product Releases
     * ### NoDocs ###
     * GET         /app/products/:productId/releases                                    controllers.ProductReleases.getAll(productId: String)
     * ### NoDocs ###
     * GET         /app/products/:productId/releases/:releaseId                         controllers.ProductsReleases.get(productId: String, releaseId: String)
     * ### NoDocs ###
     * POST        /app/products/:productId/releases                                    controllers.ProductsReleases.post(productId: String)
     * ### NoDocs ###
     * PUT         /app/products/:productId/releases/:releaseId                         controllers.ProductsReleases.put(productId: String, releaseId: String)
     *
     * */

    @Security.Authenticated(Secured.class)
    public Result get(String productId, String releaseId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        if(!myUser.getSuperadmin()) {
            return badRequest();
        }
        ObjectNode result = Json.newObject();
        ProductRelease release = ProductRelease.findByProductIdReleasId(productId, releaseId);
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("release", Json.toJson(release));
        return ok(result);
    }

    @Security.Authenticated(SecuredApi.class)
    public Result getUpgrade(String productId, String releaseId) {
        ObjectNode result = Json.newObject();
        ProductRelease release = ProductRelease.findByProductIdReleasId(productId, releaseId).getUpgrade();
        if(release != null){
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("product_release", Json.toJson(release));
        }
        return ok(result);
    }


    @Security.Authenticated(Secured.class)
    public Result getAll(String productId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        if(!myUser.getSuperadmin()) {
            return badRequest();
        }

        ObjectNode result = Json.newObject();
        Set<ProductRelease> releases = Product.findById(productId).releases;
        result.put("status", "ok");
        result.put("success", "ok");
        result.set("releases", Json.toJson(releases));
        return ok(result);
    }

    @Security.Authenticated(Secured.class)
    public Result post(String productId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        if(!myUser.getSuperadmin()) {
            return badRequest();
        }

        try {
            JsonNode json = request().body().asJson();
            Product product = Product.findById(productId);
            ProductRelease submittedProductRelease = Json.fromJson(json, ProductRelease.class);
            submittedProductRelease.setProduct(product);
            submittedProductRelease.setBy(myUser);
            Html html =  new Html(submittedProductRelease.releaseNoteHtml, Organization.findByEmailDomain(OMEGATRACE_DOMAIN));
            html.save();
            submittedProductRelease.setHtml(html);
            submittedProductRelease.setUpgrade(ProductRelease.findByProductIdReleasId(product.id, submittedProductRelease.upgradeId));
            submittedProductRelease.save();
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("release", Json.toJson(submittedProductRelease));
            return ok(result);
        } catch (Exception e){
            return badRequest();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result put(String productId, String releaseId) {
        // Check if the user has Role access
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        if(!myUser.getSuperadmin()) {
            return badRequest();
        }
        ProductRelease release = ProductRelease.findByProductIdReleasId(productId, releaseId);
        // merge logic
        try {
            JsonNode json = request().body().asJson();
            ProductRelease submittedProductRelease = Json.fromJson(json, ProductRelease.class);
            release.setName(submittedProductRelease.name);
            release.setVersionX(submittedProductRelease.versionX);
            release.setVersionY(submittedProductRelease.versionY);
            release.setVersionZ(submittedProductRelease.versionZ);
            release.setDownloadUrl(submittedProductRelease.downloadUrl);
            release.setBeta(submittedProductRelease.beta);
            release.setShipped(submittedProductRelease.shipped);
            if(submittedProductRelease.shipped){
                release.setWhenShipped(Utils.getCurrentTimestamp());
            }
            if(submittedProductRelease.upgradeId != null){
                if(!submittedProductRelease.upgradeId.isEmpty()){
                    release.setUpgrade(ProductRelease.findByProductIdReleasId(release.product.id, submittedProductRelease.upgradeId));
                } else {
                    release.setUpgrade(null);
                }
            } else {
                release.setUpgrade(null);
            }
            release.save();
            ObjectNode result = Json.newObject();
            result.put("status", "ok");
            result.put("success", "ok");
            result.set("release", Json.toJson(release));
            return ok(result);
        }
        catch (Exception e){
            return badRequest();
        }
    }



    @BodyParser.Of(FileMultipartFormDataBodyParser.class)
    @Security.Authenticated(Secured.class)
    public Result postUploads(String productId, String releaseId) {
        Organization myOrg = organization();
        User myUser = User.findByEmail(session().get("email"), myOrg);
        if(!myUser.getSuperadmin()) {
            return badRequest();
        }

        Product project = Product.findById(productId);
        ProductRelease release = ProductRelease.findByProductIdReleasId(productId, releaseId);
        final Http.MultipartFormData<File> formData = request().body().asMultipartFormData();
        final Http.MultipartFormData.FilePart<File> filePart = formData.getFile("file");
        final File file = filePart.getFile();

        try {
            if (filePart != null) {
                String fileName = filePart.getFilename();
                String contentType = filePart.getContentType();

                String fileUrl =  S3Manager.uploadWrrKrrSystemImage(release.getReleaseVersion(), fileName,file);
                if(fileUrl == null){
                    return internalServerError("File not uploaded to S3.");
                }

                // Save file URL
                release.setDownloadUrl(fileUrl);
                release.save();

                // Return object
                // NOTE: The upload task will complete async. We are going to return the URL before this task completes for UI.
                ObjectNode result = Json.newObject();
                result.put("status", "ok");
                result.put("success", "ok");
                result.put("download_url", fileUrl);

                return ok(result);
            } else {
                return internalServerError("File not uploaded correctly.");
            }
        } catch (Exception e){
            return badRequest();
        }
    }


}