package com.github.njuro.jard.common;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import org.springframework.scheduling.annotation.Scheduled;

/** Various constants for configuration purposes. */
@UtilityClass
@SuppressWarnings({"RedundantModifiersUtilityClassLombok"})
public class Constants {

  /** Name of HTTP cookie the JSON Web Token is stored in. */
  public static final String JWT_COOKIE_NAME = "jwt_token";

  /**
   * Name of request attribute determining the expiration of JSON Web Token - session vs. set date -
   * akka the "Remember me" option".
   */
  public static final String JWT_REMEMBER_ME_ATTRIBUTE = "jard.jwt.rememberme";

  public static final String SBA_SECRET_HEADER = "X-SBA-SECRET";

  /** Root folder for storing files uploaded by posters on local filesystem. */
  public static final Path USER_CONTENT_PATH =
      Paths.get(System.getProperty("user.home"), "jard-usercontent");

  /** Default file extension for thumbnails of non-image files (video, pdf...). */
  public static final String DEFAULT_THUMBNAIL_EXTENSION = "png";

  /** Max width of thumbnail image (in pixels). */
  public static final double IMAGE_MAX_THUMB_WIDTH = 250;

  /** Max height of thumbnail image (in pixels). */
  public static final double IMAGE_MAX_THUMB_HEIGHT = 250;

  /** Character to put at the beginning of tripcode. */
  public static final String TRIPCODE_SEPARATOR = "!";

  /** Length of tripcode (akka first {@code n} chars of hashed poster password). */
  public static final int TRIPCODE_LENGTH = 10;

  /** Max threads per one page of board. */
  public static final int MAX_THREADS_PER_PAGE = 10;

  /** Pattern for detecting greentext. */
  public static final Pattern GREENTEXT_PATTERN =
      Pattern.compile("^\\s*>.*$", Pattern.MULTILINE); // >foo

  /** Tag to put at the beginning of greentext. */
  public static final String GREENTEXT_START = "<span class=\"greentext\">";

  /** Tag to put at the end of the greentext. */
  public static final String GREENTEXT_END = "</span>";

  /** Pattern for detecting crosslink. */
  public static final Pattern CROSSLINK_PATTERN =
      Pattern.compile(
          ">>(?:>/(?<board>.+)/)?(?:(?<postNo>\\d+)|(?<=/)\\s*)"); // >>5, >>>/b/, >>>/b/5 etc.

  /** Tag to put at the beginning of crosslink. */
  public static final String CROSSLINK_START =
      "<a data-post-number=\"${postNumber}\" data-board-label=\"${boardLabel}\" href=\"${linkHref}\" class=\"${linkClass}\">";

  /** Tag to put at the end of crosslink. */
  public static final String CROSSLINK_END = "</a>";

  /** Name of CSS class marking valid crosslink. */
  public static final String CROSSLINK_CLASS_VALID = "crosslink";

  /** Name of CSS class marking invalid crosslink. */
  public static final String CROSSLINK_CLASS_INVALID = "deadlink";

  /** Symbol indicating crosslink to another thread. */
  public static final String CROSSLINK_DIFF_THREAD = "→";

  /** Symbol indicating crosslink to original post of the thread. */
  public static final String CROSSLINK_OP = "(OP)";

  /** Pattern for detecting spoiler. */
  public static final Pattern SPOILER_PATTERN =
      Pattern.compile(
          "(\\[spoiler]|\\*\\*)(?<content>.*?\\w+.*?)(\\[/spoiler]|\\*\\*)", // [spoiler]foo[/spoiler] or **foo**
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  /** Tag to put at the beginning of spoiler. */
  public static final String SPOILER_START = "<span class=\"spoiler\">";

  /** Tag to put at the end of spoiler. */
  public static final String SPOILER_END = "</span>";

  /** Pattern for detecting code block. */
  public static final Pattern CODE_PATTERN =
      Pattern.compile(
          "\\[code](?<content>.*?\\w+.*?)\\[/code]",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL); // [code]foo[/code]

  /** Tag to put at the beginning of code block. */
  public static final String CODE_START = "<div class=\"code\">";

  /** Tag to put at the end of code block. */
  public static final String CODE_END = "</div>";

  /** Pattern for detecting hyperlink. */
  public static final Pattern HYPERLINK_PATTERN =
      Pattern.compile(
          "(?<href>https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*))",
          Pattern.CASE_INSENSITIVE);

  /** Tag to put at the beginning of hyperlink. */
  public static final String HYPERLINK_START =
      "<a href=\"${href}\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">";

  /** Tag to put at the end of hyperlink. */
  public static final String HYPERLINK_END = "</a>";

  /**
   * How often should system check for expired bans.
   *
   * @see Scheduled#fixedRateString()
   */
  public static final String EXPIRED_BANS_CHECK_PERIOD = "PT15M"; // every 15 minutes

  /** Pattern for detecting IP address. */
  public static final String IP_PATTERN =
      "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$"; // 127.0.0.1
}
