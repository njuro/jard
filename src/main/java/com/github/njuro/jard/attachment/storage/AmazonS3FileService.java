package com.github.njuro.jard.attachment.storage;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.github.njuro.jard.attachment.UserContentStorageMode;
import java.io.File;
import java.nio.file.Paths;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Service for uploading and deleting files from Amazon S3 bucket.
 *
 * <p>Must be enabled by {@link UserContentStorageMode#AMAZON_S3}
 */
@Service
@ConditionalOnProperty(name = "app.user.content.storage", havingValue = "AMAZON_S3")
public class AmazonS3FileService implements RemoteStorageService {

  /** Access key for AWS. */
  @Value("${app.aws.accesskey}")
  private String awsAccessKey;

  /** Secret key for AWS. */
  @Value("${app.aws.secretkey}")
  private String awsSecretKey;

  /** Region the Amazon S3 bucket is located in. */
  @Value("${app.aws.s3.region:eu-central-1}")
  private Regions region;

  /** Name of the Amazon S3 bucket the files are stored in. */
  @Value("${app.aws.s3.bucket}")
  private String bucket;

  /** Amazon S3 client. */
  private AmazonS3 awsClient;

  /**
   * Initialize and authenticate to Amazon S3 client (only if Amazon S3 storage mode is enables,
   * otherwise does nothing).
   *
   * @throws IllegalArgumentException if authentication to Amazon S3 client fails
   * @see UserContentStorageMode
   */
  @PostConstruct
  public void initializeClient() {
    try {
      AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
      awsClient =
          AmazonS3ClientBuilder.standard()
              .withCredentials(new AWSStaticCredentialsProvider(credentials))
              .withRegion(region)
              .build();
    } catch (AmazonClientException ex) {
      throw new IllegalArgumentException("Authentication to Amazon S3 client failed", ex);
    }
  }

  /** {@inheritDoc} */
  @Override
  public String uploadFile(String folder, String filename, File file) {
    try {
      var key = Paths.get(folder, filename).toString();
      awsClient.putObject(
          new PutObjectRequest(bucket, key, file)
              .withCannedAcl(CannedAccessControlList.PublicRead));
      return awsClient.getUrl(bucket, key).toExternalForm();
    } catch (AmazonClientException ex) {
      throw new IllegalArgumentException("Upload of file to Amazon S3 bucket failed", ex);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void deleteFile(String folder, String filename) {
    try {
      var key = Paths.get(folder, filename).toString();
      awsClient.deleteObject(bucket, key);
    } catch (AmazonClientException ex) {
      throw new IllegalArgumentException("Deletion of file from Amazon S3 bucket failed", ex);
    }
  }
}
