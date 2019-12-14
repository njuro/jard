package com.github.njuro.jboard.post;

import static com.github.njuro.jboard.common.Constants.TRIPCODE_LENGTH;
import static com.github.njuro.jboard.common.Constants.TRIPCODE_SEPARATOR;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/**
 * Utility methods for generating tripcodes
 *
 * @author njuro
 */
@UtilityClass
public class TripcodeUtils {

  /**
   * Generates tripcode from given password using SHA-512 hashing algorithm
   *
   * <p>Tripcode length and separator are specified in {@link
   * com.github.njuro.jboard.common.Constants Constants}
   */
  @SneakyThrows(NoSuchAlgorithmException.class)
  public String generateTripcode(String password) {
    if (password == null || password.isEmpty()) {
      return null;
    }

    MessageDigest md = MessageDigest.getInstance("SHA-512");
    md.update(password.getBytes());
    byte[] byteData = md.digest();

    StringBuilder tripcode = new StringBuilder();
    for (int i = 0; i < byteData.length; i++) {
      tripcode.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
    }

    return TRIPCODE_SEPARATOR + tripcode.substring(tripcode.length() - TRIPCODE_LENGTH);
  }
}
