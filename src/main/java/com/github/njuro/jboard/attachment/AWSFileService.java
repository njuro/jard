package com.github.njuro.jboard.attachment;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.nio.file.Paths;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AWSFileService {

  @Value("${app.user.content.storage:LOCAL}")
  private UserContentStorageMode storageMode;

  @Value("${app.aws.accesskey}")
  private String awsAccessKey;

  @Value("${app.aws.secretkey}")
  private String awsSecretKey;

  @Value("${app.aws.region}")
  private Regions region;

  @Value("${app.aws.bucket}")
  private String bucket;

  private AmazonS3 awsClient;

  @PostConstruct
  public void initializeClient() {
    if (storageMode != UserContentStorageMode.AWS) {
      return;
    }

    try {
      AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
      awsClient =
          AmazonS3ClientBuilder.standard()
              .withCredentials(new AWSStaticCredentialsProvider(credentials))
              .withRegion(region)
              .build();
    } catch (AmazonClientException ex) {
      throw new IllegalStateException("Failed to connect to AWS: " + ex.getMessage());
    }
  }

  public void uploadFile(String path, String filename, File file) {
    if (storageMode != UserContentStorageMode.AWS) {
      log.warn("Storage mode is not set to AWS, skipping upload");
      return;
    }

    try {
      String key = Paths.get(path, filename).toString();
      awsClient.putObject(
          new PutObjectRequest(bucket, key, file)
              .withCannedAcl(CannedAccessControlList.PublicRead));
    } catch (AmazonClientException ex) {
      log.error("Failed to upload file to AWS: " + ex.getMessage());
    }
  }

  public void deleteFile(String path, String filename) {
    if (storageMode != UserContentStorageMode.AWS) {
      log.warn("Storage mode is not set to AWS, skipping deletion");
      return;
    }

    try {
      String key = Paths.get(path, filename).toString();
      awsClient.deleteObject(bucket, key);
    } catch (AmazonClientException ex) {
      log.error("Failed to delete file from AWS: " + ex.getMessage());
    }
  }
}
