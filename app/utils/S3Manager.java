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

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class S3Manager {

    public static AmazonS3 client;
    public static final String SUFFIX = "/";

    public static String uploadWrrKrrProfileImage(String fileFolder, String fileName, File file) {

        Logger.info("Uploading WrrKrr profile image to Amazon S3");

        Config conf = ConfigFactory.load();

        String access_key_id = conf.getString("aws.aws_access_key_id");
        String aws_secret_access_key = conf.getString("aws.aws_secret_access_key");

        String aws_bucket = conf.getString("aws.aws_bucket");
        String aws_url_scheme = conf.getString("aws.aws_url_scheme");
        String aws_profile_folder = conf.getString("aws.aws_profile_folder");


        if (client == null) {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(access_key_id, aws_secret_access_key);
            client = new AmazonS3Client(awsCreds);
        }

        String existingBucketName = aws_bucket;
        String folderName = aws_profile_folder + SUFFIX + fileFolder;
        String keyName = folderName + SUFFIX + fileName;

        try {

            CompletionStage<Integer> promiseOfFileUrl = CompletableFuture.supplyAsync(() ->
            {
                upload(existingBucketName, keyName, file);
                return 1;
            });

            String fileUrl = aws_url_scheme + SUFFIX + keyName;
            if (Utils.isDebugMode()) {
                Logger.info("successfully uploaded file - " + fileUrl);
            }

            return fileUrl;
        } catch (Exception e) {
            Logger.error("Error uploading WrrKrr profile image to S3");
            e.printStackTrace();
            return null;
        }

    }

    public static String uploadWrrKrrSystemImage(String fileFolder, String fileName, File file) {

        Logger.info("Uploading WrrKrr system image to Amazon S3");

        Config conf = ConfigFactory.load();

        String access_key_id = conf.getString("aws.aws_access_key_id");
        String aws_secret_access_key = conf.getString("aws.aws_secret_access_key");

        String aws_bucket = conf.getString("aws.aws_bucket");
        String aws_url_scheme = conf.getString("aws.aws_url_scheme");
        String aws_image_folder = conf.getString("aws.aws_image_folder");


        if (client == null) {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(access_key_id, aws_secret_access_key);
            client = new AmazonS3Client(awsCreds);
        }

        String existingBucketName = aws_bucket;
        String folderName = aws_image_folder + SUFFIX + fileFolder;
        String keyName = folderName + SUFFIX + fileName;

        try {

            CompletionStage<Integer> promiseOfFileUrl = CompletableFuture.supplyAsync(() ->
            {
                upload(existingBucketName, keyName, file);
                return 1;
            });

            String fileUrl = aws_url_scheme + SUFFIX + keyName;
            if (Utils.isDebugMode()) {
                Logger.info("successfully uploaded file - " + fileUrl);
            }
            return fileUrl;
        } catch (Exception e) {
            Logger.error("Error uploading WrrKrr system image to S3");
            e.printStackTrace();
            return null;
        }

    }

    private static void upload(String existingBucketName, String keyName, File file) {
        // Step 0: Data needed for file upload.
        // Create a list of UploadPartResponse objects. You get one of these
        // for each part upload.
        List<PartETag> partETags = new ArrayList<PartETag>();

        // Step 1: Initialize.
        InitiateMultipartUploadRequest initRequest = new
                InitiateMultipartUploadRequest(existingBucketName, keyName);
        //This is where we are enabling this file for public access
        initRequest.setCannedACL(CannedAccessControlList.PublicRead);
        InitiateMultipartUploadResult initResponse =
                client.initiateMultipartUpload(initRequest);
        final long contentLength = file.length();
        final long[] bytesTransferred = {0};
        long partSize = 5242880; // Set part size to 5 MB.

        try {

            // Step 2: Upload parts.
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                // Last part can be less than 5 MB. Adjust part size.
                partSize = Math.min(partSize, (contentLength - filePosition));

                // Create request to upload a part.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(existingBucketName).withKey(keyName)
                        .withUploadId(initResponse.getUploadId()).withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partSize);
                uploadRequest.setGeneralProgressListener(new ProgressListener() {
                    @Override
                    public void progressChanged(ProgressEvent progressEvent) {
                        bytesTransferred[0] += progressEvent.getBytesTransferred();
                        if (bytesTransferred[0] % 10000 == 0) {
                            // TODO: this is not perfect/but does the job.
                            Logger.info("S3 file upload transferred bytes - " + humanReadableByteCount(bytesTransferred[0], true));
                        }
                    }
                });


                // Upload part and add response to our list.
                partETags.add(client.uploadPart(uploadRequest).getPartETag());

                filePosition += partSize;
            }

            // Step 3: Complete.
            CompleteMultipartUploadRequest compRequest = new
                    CompleteMultipartUploadRequest(
                    existingBucketName,
                    keyName,
                    initResponse.getUploadId(),
                    partETags);

            client.completeMultipartUpload(compRequest);

            Logger.info("S3 File uploaded successfully - " + keyName);

            file.delete();
            if (!file.exists())
                Logger.info("Successfully deleted the file.");

        } catch (Exception e) {
            client.abortMultipartUpload(new AbortMultipartUploadRequest(
                    existingBucketName, keyName, initResponse.getUploadId()));
            Logger.error("Error uploading WrrKrr image to S3. Aborting upload.");
        }
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}


