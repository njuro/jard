package com.github.njuro.jboard.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.github.njuro.jboard.helpers.Constants.TRIPCODE_LENGTH;
import static com.github.njuro.jboard.helpers.Constants.TRIPCODE_SEPARATOR;

/**
 * Utility methods for generating tripcodes
 *
 * @author njuro
 */
@UtilityClass
public class Tripcodes {

    /**
     * Generates tripcode from given password using SHA-512 hashing algorithm
     * <p>
     * Tripcode length and separator are specified in {@link com.github.njuro.jboard.helpers.Constants Constants}
     */
    @SneakyThrows(NoSuchAlgorithmException.class)
    public String generateTripcode(String password) {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(password.getBytes());
        byte byteData[] = md.digest();

        StringBuilder tripcode = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            tripcode.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return TRIPCODE_SEPARATOR + tripcode.substring(tripcode.length() - TRIPCODE_LENGTH);
    }

}
