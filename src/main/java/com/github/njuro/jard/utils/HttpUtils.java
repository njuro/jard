package com.github.njuro.jard.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/** Utility class for working with HTTP requests and responses. */
@Component
@Slf4j
public class HttpUtils {

  private final ObjectMapper objectMapper;

  private static final String[] IP_HEADER_CANDIDATES = {
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
  };

  @Autowired
  public HttpUtils(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Retrieves client IP address from request.
   *
   * @param request HTTP request to get IP from
   * @return retrieved client IP address or {@code null} if none was retrieved
   */
  public String getClientIp(HttpServletRequest request) {
    if (request == null) {
      return null;
    }

    for (String header : IP_HEADER_CANDIDATES) {
      String ipList = request.getHeader(header);
      if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
        return ipList.split(",")[0];
      }
    }

    return request.getRemoteAddr();
  }

  /**
   * Serializes given object and writes is as response body.
   *
   * @param response response to HTTP request
   * @param object object to serialize
   * @throws IOException if serializing or writing to response fails
   */
  public void writeJsonToResponse(HttpServletResponse response, Object object) throws IOException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(objectMapper.writeValueAsString(object));
    response.getWriter().flush();
  }

  /**
   * Gets origin URL from given url string.
   *
   * @param url URL to be parsed
   * @return origin URL or unchanged input if given url string is {@code null} or malformed
   */
  public static String getOriginUrl(String url) {
    try {
      var urlObject = new URL(url);
      return new URL(urlObject.getProtocol(), urlObject.getHost(), urlObject.getPort(), "")
          .toExternalForm();
    } catch (MalformedURLException e) {
      log.error("Invalid URL string passed");
      return url;
    }
  }

  /**
   * Gets domain from given url string.
   *
   * @param url URL to be parsed
   * @return parsed domain or unchanged input if given url string is {@code null} or malformed
   */
  public static String getDomain(String url) {
    try {
      if (url.toLowerCase().startsWith("localhost")) {
        return "localhost";
      }

      var urlObject = new URL(url);
      String host = urlObject.getHost();
      if (host.startsWith("www.")) {
        host = host.substring(4);
      }
      if (!host.contains(".")) {
        // assume domains without TLD are local - this can be for instance case of named docker
        // containers
        host = "localhost";
      }

      return host;
    } catch (MalformedURLException e) {
      log.error("Invalid URL string passed");
      return url;
    }
  }
}
