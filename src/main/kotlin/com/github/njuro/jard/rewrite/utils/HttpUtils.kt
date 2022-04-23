package com.github.njuro.jard.rewrite.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import mu.KotlinLogging
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import java.net.MalformedURLException
import java.net.URL
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// Utility methods for working with HTTP requests and responses.

/**
 * Retrieves client IP address from request.
 *
 * @param request HTTP request to get IP from
 * @return retrieved client IP address or {@code null} if none was retrieved
 */
fun HttpServletRequest.getClientIp() =
    ipHeaderCandidates.firstOrNull { getHeader(it).isValidIpList() }?.split(",")?.first() ?: remoteAddr

/**
 * Serializes given object with given {@link JsonView} and writes is as response body.
 *
 * @param response response to HTTP request
 * @param view {@link JsonView} to use
 * @param object object to serialize
 * @throws IOException if serializing or writing to response fails
 */
fun HttpServletResponse.writeJson(mapper: ObjectMapper, body: Any, view: Class<Any>? = null) {
    val objectWriter: ObjectWriter = if (view == null) mapper.writer() else mapper.writerWithView(view)
    contentType = APPLICATION_JSON_VALUE
    with(writer) {
        write(objectWriter.writeValueAsString(body))
        flush()
    }
}

/**
 * Gets origin URL from given url string.
 *
 * @param url URL to be parsed
 * @return origin URL or unchanged input if given url string is {@code null} or malformed
 */
fun getOriginUrl(url: String): String = try {
    URL(url).getOrigin()
} catch (ex: MalformedURLException) {
    logger.error { "Invalid URL string passed" }
    url
}

/**
 * Gets domain from given url string.
 *
 * @param url URL to be parsed
 * @return parsed domain or unchanged input if given url string is {@code null} or malformed
 */
fun getDomain(url: String): String {
    if (url.lowercase().startsWith(LOCALHOST)) {
        return LOCALHOST
    }

    val host = try {
        URL(url).host
    } catch (ex: MalformedURLException) {
        logger.error { "Invalid URL string passed" }
        url
    }

    return when {
        host.startsWith ("www.") -> host.substring(4)
        // assume domains without TLD are local - this can be for instance case of named docker containers
        !host.contains(".") -> LOCALHOST
        else -> host
    }
}

private val ipHeaderCandidates = arrayOf(
    "X-Forwarded-For",
    "Proxy-Client-IP",
    "WL-Proxy-Client-IP",
    "HTTP_X_FORWARDED_FOR",
    "HTTP_X_FORWARDED",
    "HTTP_X_CLUSTER_CLIENT_IP",
    "HTTP_CLIENT_IP",
    "HTTP_FORWARDED_FOR",
    "HTTP_FORWARDED",
    "HTTP_VIA",
    "REMOTE_ADDR"
)

private fun String?.isValidIpList() = !isNullOrEmpty() && !equals("unknown", ignoreCase = true)

private fun URL.getOrigin(): String = URL(protocol, host, port, "").toExternalForm()

private const val LOCALHOST = "localhost"

private val logger = KotlinLogging.logger {}
