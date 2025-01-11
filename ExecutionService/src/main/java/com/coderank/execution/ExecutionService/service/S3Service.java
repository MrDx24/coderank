package com.coderank.execution.ExecutionService.service;

import com.coderank.execution.ExecutionService.util.DatabaseSecretsUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;

    public S3Service(DatabaseSecretsUtil databaseSecretsUtil) {
        Map<String, String> secrets = databaseSecretsUtil.getSecrets("aws_credentials");
        String accessKey = secrets.get("AWS_ACCESS_KEY");
        String secretKey = secrets.get("AWS_SECRET_KEY");
        String region = secrets.get("AWS_REGION");
        this.bucketName = "coderank-bucket";

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    public String uploadFile(String content, String fileName) {
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        String fileKey = "uploads/" + fileName;

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileKey)
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, content.length())
        );

        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileKey);
    }

    public String getFileContent(String fileUrl) {
        String fileKey = fileUrl.substring(fileUrl.indexOf(".com/") + 5); // Extract S3 key from URL

        InputStream inputStream = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileKey)
                        .build()
        );

        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
