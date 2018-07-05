package com.github.njuro.helpers;

import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.nio.file.Paths;


@UtilityClass
public class Constants {

    /**
     * User content location
     **/
    public final Path USER_CONTENT_PATH = Paths.get(System.getProperty("user.dir"), "usercontent");
    public final String USER_CONTENT_URL = "/usercontent/";

    /**
     * Images
     **/
    public final double IMAGE_MAX_THUMB_WIDTH = 300;
    public final double IMAGE_MAX_THUMB_HEIGHT = 300;

    /**
     * Tripcodes
     */
    public final String TRIPCODE_SEPARATOR = "!";
    public final int TRIPCODE_LENGTH = 10;

}
