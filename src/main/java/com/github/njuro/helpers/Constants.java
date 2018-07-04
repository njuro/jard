package com.github.njuro.helpers;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Constants {

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

    private Constants() {
    }
}
