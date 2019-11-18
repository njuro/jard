package com.github.njuro.jboard.helpers;

import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Various constants for configuration purposes. May be replaced with properties in the future.
 */
@UtilityClass
public class Constants {

    public static final String JWT_COOKIE_NAME = "jwt_token";

    /**
     * User content location
     **/
    public static final Path USER_CONTENT_PATH = Paths.get(System.getProperty("user.home"), "WebstormProjects", "jboard-client", "public", "usercontent"); // TODO do it better
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
    public static final int MAX_TRIPCODE_PASSWORD_LENGTH = 80;
    public static final int MAX_SUBJECT_LENGTH = 255;
    public static final int MAX_POST_LENGTH = 1000;
    public static final int MAX_ATTACHMENT_SIZE = 2_000_000;

    /**
     * Decorators
     */

    public static final Pattern GREENTEXT_PATTERN = Pattern.compile("^\\s*>.*$", Pattern.MULTILINE);
    public static final String GREENTEXT_START = "<span class=\"greentext\">";
    public static final String GREENTEXT_END = "</span>";

    public static final Pattern CROSSLINK_PATTERN = Pattern.compile(">>(?:>/(?<board>.+)/)?(?:(?<postNo>\\d+)|(?<=/)\\s*)");
    public static final String CROSSLINK_START = "<a href=\"${linkHref}\" class=\"${linkClass}\">";
    public static final String CROSSLINK_END = "</a>";
    public static final String CROSSLINK_CLASS_VALID = "crosslink";
    public static final String CROSSLINK_CLASS_INVALID = "deadlink";
    public static final String CROSSLINK_DIFF_THREAD = "→";
    public static final String CROSSLINK_OP = "(OP)";

    // TODO accept [/spoiler] as end tag
    public static final Pattern SPOILER_PATTERN = Pattern.compile("(\\[spoiler\\]|\\*\\*)(?<content>.*?\\w+.*?)\\1",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    public static final String SPOILER_START = "<span class=\"spoiler\">";
    public static final String SPOILER_END = "</span>";

    public static final Pattern CODE_PATTERN = Pattern.compile("\\[code\\](?<content>.*?\\w+.*?)\\[/code\\]",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    public static final String CODE_START = "<div class=\"code\">";
    public static final String CODE_END = "</div>";

    /**
     * Users
     */
    public static final int MIN_USERNAME_LENGTH = 2;
    public static final int MAX_USERNAME_LENGTH = 32;
    public static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Bans
     */
    public static final String EXPIRED_BANS_CHECK_PERIOD = "PT15M"; // 15 minutes
    public static final String IP_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";

}
