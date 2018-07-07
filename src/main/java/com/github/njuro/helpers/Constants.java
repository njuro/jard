package com.github.njuro.helpers;

import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.nio.file.Paths;


@UtilityClass
public class Constants {

    /**
     * User content location
     **/
    public static final Path USER_CONTENT_PATH = Paths.get(System.getProperty("user.dir"), "usercontent");
    public static final String USER_CONTENT_URL = "/usercontent/";

    /**
     * Images
     **/
    public static final double IMAGE_MAX_THUMB_WIDTH = 300;
    public static final double IMAGE_MAX_THUMB_HEIGHT = 300;

    /**
     * Tripcodes
     */
    public static final String TRIPCODE_SEPARATOR = "!";
    public static final int TRIPCODE_LENGTH = 10;

    /**
     * Threads & Posts
     */
    public static final int MAX_NAME_LENGTH = 32;
    public static final int MAX_PASSWORD_LENGTH = 80;
    public static final int MAX_SUBJECT_LENGTH = 255;
    public static final int MAX_POST_LENGTH = 1000;
    public static final int MAX_ATTACHMENT_SIZE = 2_000_000;

}
