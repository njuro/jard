package com.github.njuro.jard.post;

import static com.github.njuro.jard.common.Constants.TRIPCODE_LENGTH;
import static com.github.njuro.jard.common.Constants.TRIPCODE_SEPARATOR;

import com.github.njuro.jard.common.Constants;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.experimental.UtilityClass;

/**
 * Utility class with methods for manipulating with <a
 * href="https://www.urbandictionary.com/define.php?term=Trip%20Code">tripcodes</a>.
 */
@UtilityClass
public class TripcodeUtils {

  /**
   * Generates tripcode from given password using SHA-512 hashing algorithm.
   *
   * <p>Tripcode length and separator are specified in constants.
   *
   * @param password non-empty password to generate tripcode from
   * @return generated tripcode or {@code null} if password is empty
   * @throws IllegalStateException if generation fails (e.g. if hashing algorithm is not supported
   *     by current platform)
   * @see Constants#TRIPCODE_LENGTH
   * @see Constants#TRIPCODE_SEPARATOR
   */
  public String generateTripcode(String password) {
    if (password == null || password.isEmpty()) {
      return null;
    }

    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      md.update(password.getBytes());
      byte[] byteData = md.digest();

      StringBuilder tripcode = new StringBuilder();
      for (int i = 0; i < byteData.length; i++) {
        tripcode.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
      }

      return TRIPCODE_SEPARATOR + tripcode.substring(tripcode.length() - TRIPCODE_LENGTH);
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException("Hashing of tripcode failed", ex);
    }
  }
}
