package com.github.njuro.jard.rewrite.common

import java.nio.file.Paths
import java.util.regex.Pattern

/** Various constants for configuration purposes.  */
/** Name of HTTP cookie the JSON Web Token is stored in.  */
const val JWT_COOKIE_NAME = "jwt_token"

/**
 * Name of request attribute determining the expiration of JSON Web Token - session vs. set date -
 * akka the "Remember me" option".
 */
const val JWT_REMEMBER_ME_ATTRIBUTE = "jard.jwt.rememberme"
const val SBA_SECRET_HEADER = "X-SBA-SECRET"

/** Root folder for storing files uploaded by posters on local filesystem.  */
val USER_CONTENT_PATH = Paths.get(System.getProperty("user.home"), "jard-usercontent")

/** Name of folder where attachment thumbnails are stored  */
const val THUMBNAIL_FOLDER_NAME = "thumbs"

/** Default file extension for thumbnails of non-image files (video, pdf...).  */
const val DEFAULT_THUMBNAIL_EXTENSION = "png"

/** Max width of thumbnail image (in pixels).  */
const val IMAGE_MAX_THUMB_WIDTH = 250.0

/** Max height of thumbnail image (in pixels).  */
const val IMAGE_MAX_THUMB_HEIGHT = 250.0

/** Character to put at the beginning of tripcode.  */
const val TRIPCODE_SEPARATOR = "!"

/** Length of tripcode (akka first `n` chars of hashed poster password).  */
const val TRIPCODE_LENGTH = 10

/** Max threads per one page of board.  */
const val MAX_THREADS_PER_PAGE = 10

/** Pattern for detecting greentext.  */
val GREENTEXT_PATTERN = Pattern.compile("^\\s*>.*$", Pattern.MULTILINE) // >foo

/** Tag to put at the beginning of greentext.  */
const val GREENTEXT_START = "<span class=\"greentext\">"

/** Tag to put at the end of the greentext.  */
const val GREENTEXT_END = "</span>"

/** Pattern for detecting crosslink.  */
val CROSSLINK_PATTERN = Pattern.compile(
    ">>(?:>/(?<board>.+)/)?(?:(?<postNo>\\d+)|(?<=/)\\s*)"
) // >>5, >>>/b/, >>>/b/5 etc.

/** Tag to put at the beginning of crosslink.  */
const val CROSSLINK_START =
    "<a data-post-number=\"\${postNumber}\" data-board-label=\"\${boardLabel}\" href=\"\${linkHref}\" class=\"\${linkClass}\">"

/** Tag to put at the end of crosslink.  */
const val CROSSLINK_END = "</a>"

/** Name of CSS class marking valid crosslink.  */
const val CROSSLINK_CLASS_VALID = "crosslink"

/** Name of CSS class marking invalid crosslink.  */
const val CROSSLINK_CLASS_INVALID = "deadlink"

/** Symbol indicating crosslink to another thread.  */
const val CROSSLINK_DIFF_THREAD = "→"

/** Symbol indicating crosslink to original post of the thread.  */
const val CROSSLINK_OP = "(OP)"

/** Pattern for detecting spoiler.  */
val SPOILER_PATTERN = Pattern.compile(
    "(\\[spoiler]|\\*\\*)(?<content>.*?\\w+.*?)(\\[/spoiler]|\\*\\*)",  // [spoiler]foo[/spoiler] or **foo**
    Pattern.CASE_INSENSITIVE or Pattern.DOTALL
)

/** Tag to put at the beginning of spoiler.  */
const val SPOILER_START = "<span class=\"spoiler\">"

/** Tag to put at the end of spoiler.  */
const val SPOILER_END = "</span>"

/** Pattern for detecting code block.  */
val CODE_PATTERN = Pattern.compile(
    "\\[code](?<content>.*?\\w+.*?)\\[/code]",
    Pattern.CASE_INSENSITIVE or Pattern.DOTALL
) // [code]foo[/code]

/** Tag to put at the beginning of code block.  */
const val CODE_START = "<div class=\"code\">"

/** Tag to put at the end of code block.  */
const val CODE_END = "</div>"

/** Pattern for detecting hyperlink.  */
val HYPERLINK_PATTERN = Pattern.compile(
    "(?<href>https?://(www\\.)?[-a-z0-9@:%._+~#=]{1,256}\\.[a-z0-9()]{1,6}\\b([-a-z0-9()@:%_+.~#?&/=]*))",
    Pattern.CASE_INSENSITIVE
)

/** Tag to put at the beginning of hyperlink.  */
const val HYPERLINK_START = "<a href=\"\${href}\" target=\"_blank\" rel=\"noopener nofollow noreferrer\">"

/** Tag to put at the end of hyperlink.  */
const val HYPERLINK_END = "</a>"

/** Tag to put at the beginning of matched search result.  */
const val SEARCH_RESULT_HIGHLIGHT_START = "<span class=\"search-result-highlight\">"

/** Tag to put at the end of matched search result.  */
const val SEARCH_RESULT_HIGHLIGHT_END = "</span>"

/**
 * How often should system check for expired bans.
 *
 * @see Scheduled.fixedRateString
 */
const val EXPIRED_BANS_CHECK_PERIOD = "PT15M" // every 15 minutes

/** Pattern for detecting IP address (IPv4 and Ipv6).  */
const val IP_PATTERN =
    "^((25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})|(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))$"

/** How long after creation can the post be deleted by poster (in minutes).  */
const val OWN_POST_DELETION_TIME_LIMIT = 15

/** Name of analyser used to search posts.  */
const val POST_ANALYZER = "postAnalyzer"

/** The highest allowed results count for entity search.  */
const val MAX_SEARCH_RESULTS_COUNT = 50

/**
 * How often should system check for expired user tokens.
 *
 * @see Scheduled.fixedRateString
 */
const val EXPIRED_USER_TOKENS_CHECK_PERIOD = "PT5M" // every 5 minutes
