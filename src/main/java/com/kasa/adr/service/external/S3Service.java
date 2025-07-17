package com.kasa.adr.service.external;


import com.kasa.adr.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class S3Service {
    private final Logger logger = LoggerFactory.getLogger(S3Service.class);
    private final String region = "ap-south-1";
    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.api.key}")
    private String s3AccessKey;

    @Value("${aws.api.secret}")
    private String s3SecretKey;


    public List<String> uploadFileToS3(MultipartFile[] multipartFiles, String id, String folder) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(s3AccessKey, s3SecretKey);
        // Create S3 client
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        List<String> responses = new ArrayList<>();
        logger.info("File upload in progress.Total --->{}", Arrays.stream(multipartFiles).count() + "----" + id);

        try {
            Arrays.asList(multipartFiles).stream().forEach(multipartFile -> {
                if (multipartFile.getOriginalFilename().contains("..")) {
                    responses.add("INVALID_INPUT_FILE_SEQUENCE");
                }
                final File file = convertMultiPartFileToFile(multipartFile);
                final String uniqueFileName = CommonUtils.generateUniqueFileName(file.getName().toLowerCase());
                String objectPath = folder + "/" + id + "/" + uniqueFileName;
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(objectPath)
                        .build();

                // Upload the file to S3
                PutObjectResponse response = s3Client.putObject(putObjectRequest,
                        Paths.get(file.getPath()));

                logger.info("File uploaded successfully. {}: ", response);
                file.deleteOnExit(); // To remove the file locally created in the project folder.
                responses.add(uniqueFileName);
            });
        } catch (Exception ex) {
            logger.error("Error= {} while uploading file.", ex.getMessage());
            responses.add("Failed");
        }
        return responses;
    }


    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(multipartFile.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {

            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            logger.error("Error converting the multi-part file to file= {}", ex.getMessage());
        }
        logger.info("File details = {}", file.getName());
        return file;
    }

    public void createEmptyMissingFile(String id, String fileName) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(s3AccessKey, s3SecretKey);
        // Create S3 client
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

        try {


            File file = new File(fileName);
            String objectPath = "error/" + id + "/" + fileName;
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectPath)
                    .build();

            // Upload the file to S3
            PutObjectResponse response = s3Client.putObject(putObjectRequest,
                    Paths.get(file.getPath()));

            logger.info("File uploaded successfully. {}: ", response);
            file.deleteOnExit(); // To remove the file locally created in the project folder.


        } catch (Exception ex) {
            logger.error("Error= {} while uploading file.", ex.getMessage());

        }

    }

    public String localFilePath(String key) {
        logger.info("Key--" + key + " bucket" + bucket);
        String localFilePath = new Random().nextInt() + "_temp_file.csv"; // File path to save locally
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(s3AccessKey, s3SecretKey);
        // Create S3 client
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

        try {
            // Build the GetObjectRequest
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            logger.info("S3 Objecj request {}", getObjectRequest);
            // Download the file
            try (InputStream s3ObjectInputStream = s3Client.getObject(getObjectRequest);
                 FileOutputStream fileOutputStream = new FileOutputStream(new File(localFilePath))) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = s3ObjectInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                System.out.println("File downloaded successfully to: " + localFilePath);
            }
        } catch (Exception e) {
            System.err.println("Error occurred while downloading the file from S3:");
            e.printStackTrace();
        } finally {
            s3Client.close();
        }
        return localFilePath;
    }

    public void uploadSingleFile(File file, String folder) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(s3AccessKey, s3SecretKey);
        // Create S3 client
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

        try {


            String objectPath = folder + "/" + file.getName();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectPath)
                    .build();

            // Upload the file to S3
            PutObjectResponse response = s3Client.putObject(putObjectRequest,
                    Paths.get(file.getPath()));

            logger.info("File uploaded successfully. {}: ", response);
            file.deleteOnExit(); // To remove the file locally created in the project folder.


        } catch (Exception ex) {
            logger.error("Error= {} while uploading file.", ex.getMessage());

        }

    }

    public String uploadCaseFile(String caseId, MultipartFile multipartFile, String cases) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(s3AccessKey, s3SecretKey);
        // Create S3 client
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        File file = convertMultiPartFileToFile(multipartFile);
        String uniqueFileName = CommonUtils.generateUniqueFileName(file.getName().toLowerCase());
        try {

            String objectPath = cases + "/" + caseId + "/" + uniqueFileName;
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectPath)
                    .build();

            // Upload the file to S3
            PutObjectResponse response = s3Client.putObject(putObjectRequest,
                    Paths.get(file.getPath()));

            logger.info("File uploaded successfully. {}: ", response);
            // To remove the file locally created in the project folder.

        } catch (Exception ex) {
            logger.error("Error= {} while uploading file.", ex.getMessage());

        } finally {
            file.deleteOnExit();
            s3Client.close();
        }
        return uniqueFileName;
    }
}
