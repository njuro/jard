package com.github.njuro.jard.attachment.storage;

import com.github.njuro.jard.attachment.UserContentStorageMode;
import java.io.File;

/**
 * Interface for services responsible for storing files on remote servers.
 *
 * @see UserContentStorageMode
 */
public interface RemoteStorageService {

  /**
   * Uploads file to remote server.
   *
   * @param folder path to the folder to which the file should be put on the server
   * @param filename name of the uploaded file on the server
   * @param file file to upload
   * @return shareable url to uploaded file
   * @throws IllegalArgumentException if upload of file fails
   */
  String uploadFile(String folder, String filename, File file);

  /**
   * Deletes file from remote server.
   *
   * @param folder path to the folder on the server where the file is located
   * @param filename name of the file
   * @throws IllegalArgumentException if deletion of file fails
   */
  void deleteFile(String folder, String filename);
}
