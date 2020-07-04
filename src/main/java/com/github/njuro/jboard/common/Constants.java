package com.github.njuro.jboard.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

/** Various constants for configuration purposes. May be replaced with properties in the future. */
@UtilityClass
public class Constants {

  public static final String JWT_COOKIE_NAME = "jwt_token";
  public static final String JWT_REMEMBER_ME_ATTRIBUTE = "jboard.jwt.rememberme";

  /** User content location */
  public static final Path USER_CONTENT_PATH =
      Paths.get(
          System.getProperty("user.home"),
          "WebstormProjects",
          "jboard-client",
          "public",
          "usercontent"); // TODO do it better

  public static final Path USER_CONTENT_THUMBS_PATH = USER_CONTENT_PATH.resolve("thumbs");

  /** Images */
  public static final String DEFAULT_THUMBNAIL_EXTENSION = "png"; // for non-image attachments

  public static final double IMAGE_MAX_THUMB_WIDTH = 250;

  public static final double IMAGE_MAX_THUMB_HEIGHT = 250;

  /** Tripcodes */
  public static final String TRIPCODE_SEPARATOR = "!";

  public static final int TRIPCODE_LENGTH = 10;

  /** Boards */
  public static final int MAX_BOARD_LABEL_LENGTH = 4;

  public static final int MAX_BOARD_NAME_LENGTH = 32;
  public static final int MAX_THREAD_LIMIT = 200;
  public static final int MAX_BUMP_LIMIT = 1000;
  public static final int MAX_THREADS_PER_PAGE = 10;

  /** Threads & Posts */
  public static final int MAX_NAME_LENGTH = 32;

  public static final int MAX_TRIPCODE_PASSWORD_LENGTH = 80;
  public static final int MAX_SUBJECT_LENGTH = 255;
  public static final int MAX_POST_LENGTH = 1000;
  public static final int MAX_ATTACHMENT_SIZE = 5_000_000;

  /** Decorators */
  public static final Pattern GREENTEXT_PATTERN = Pattern.compile("^\\s*>.*$", Pattern.MULTILINE);

  public static final String GREENTEXT_START = "<span class=\"greentext\">";
  public static final String GREENTEXT_END = "</span>";

  public static final Pattern CROSSLINK_PATTERN =
      Pattern.compile(">>(?:>/(?<board>.+)/)?(?:(?<postNo>\\d+)|(?<=/)\\s*)");
  public static final String CROSSLINK_START = "<a href=\"${linkHref}\" class=\"${linkClass}\">";
  public static final String CROSSLINK_END = "</a>";
  public static final String CROSSLINK_CLASS_VALID = "crosslink";
  public static final String CROSSLINK_CLASS_INVALID = "deadlink";
  public static final String CROSSLINK_DIFF_THREAD = "→";
  public static final String CROSSLINK_OP = "(OP)";

  public static final Pattern SPOILER_PATTERN =
      Pattern.compile(
          "(\\[spoiler]|\\*\\*)(?<content>.*?\\w+.*?)(\\[/spoiler]|\\*\\*)",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  public static final String SPOILER_START = "<span class=\"spoiler\">";
  public static final String SPOILER_END = "</span>";

  public static final Pattern CODE_PATTERN =
      Pattern.compile(
          "\\[code](?<content>.*?\\w+.*?)\\[/code]", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  public static final String CODE_START = "<div class=\"code\">";
  public static final String CODE_END = "</div>";

  /** Users */
  public static final int MIN_USERNAME_LENGTH = 2;

  public static final int MAX_USERNAME_LENGTH = 32;
  public static final int MIN_PASSWORD_LENGTH = 8;

  /** Bans */
  public static final String EXPIRED_BANS_CHECK_PERIOD = "PT15M"; // 15 minutes

  public static final String IP_PATTERN =
      "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";
}
