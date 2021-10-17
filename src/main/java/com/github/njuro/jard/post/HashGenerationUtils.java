package com.github.njuro.jard.post;

import static com.github.njuro.jard.common.Constants.TRIPCODE_LENGTH;
import static com.github.njuro.jard.common.Constants.TRIPCODE_SEPARATOR;

import com.github.njuro.jard.common.Constants;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;
import lombok.experimental.UtilityClass;

/** Utility class with methods for generating hashed strings from given inputs. */
@UtilityClass
public class HashGenerationUtils {

  /**
   * Generates <a href="https://en.wiktionary.org/wiki/tripcode">tripcode</a> from given password.
   *
   * <p>Tripcode length and separator are specified in constants.
   *
   * @param password password to generate tripcode from
   * @return generated tripcode, or {@code null} if password is {@code null} or empty string
   * @throws IllegalArgumentException if generation fails
   * @see Constants#TRIPCODE_LENGTH
   * @see Constants#TRIPCODE_SEPARATOR
   */
  public String generateTripcode(String password) {
    if (password == null || password.isBlank()) {
      return null;
    }

    return TRIPCODE_SEPARATOR + generateHash(password, TRIPCODE_LENGTH);
  }

  /**
   * Generates unique poster thread id for given poster IP.
   *
   * @param posterIp IP of poster
   * @param threadId ID of thread
   * @return generated thread poster id
   * @throws IllegalArgumentException if generation fails
   */
  public String generatePosterThreadId(String posterIp, UUID threadId) {
    return generateHash(posterIp + threadId.toString(), 8);
  }

  /**
   * Generates hex-encoded hash from given input.
   *
   * @param input Input to generate hash from
   * @param length length of resulting hash (must be between 1-128)
   * @return hash generated from given input of given length.
   * @throws IllegalArgumentException if generation fails (e.g. if hashing algorithm is not
   *     supported by current platform, or hash length is out of allowed range)
   */
  private String generateHash(String input, int length) {
    Objects.requireNonNull(input);
    if (length < 1 || length > 128) {
      throw new IllegalArgumentException("Hash length must be between 1 and 128 characters");
    }

    try {
      var md = MessageDigest.getInstance("SHA-512");
      md.update(input.getBytes());
      byte[] byteData = md.digest();

      var result = new StringBuilder();
      for (byte byteValue : byteData) {
        result.append(Integer.toString((byteValue & 0xff) + 0x100, 16).substring(1));
      }

      return result.substring(result.length() - length);
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalArgumentException("Hashing failed", ex);
    }
  }
}
