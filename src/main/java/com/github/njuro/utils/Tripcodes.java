package com.github.njuro.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.github.njuro.helpers.Constants.TRIPCODE_LENGTH;
import static com.github.njuro.helpers.Constants.TRIPCODE_SEPARATOR;

@UtilityClass
public class Tripcodes {

    @SneakyThrows(NoSuchAlgorithmException.class)
    public String generateTripcode(String password) {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(password.getBytes());
        byte byteData[] = md.digest();

        StringBuffer tripcode = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            tripcode.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return TRIPCODE_SEPARATOR + tripcode.substring(tripcode.length() - TRIPCODE_LENGTH);
    }

}
