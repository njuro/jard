package com.github.njuro.jard.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerMapping;

/** Common interface for resolvers resolving method arguments from path variables. */
public interface PathVariableArgumentResolver extends HandlerMethodArgumentResolver {

  /**
   * Resolves path variable from request.
   *
   * @param name name of path variable
   * @param request http request
   * @return String representation of given path variable or null, if path variable does not exists
   */
  default String getPathVariable(String name, NativeWebRequest request) {
    return getPathVariables(request).get(name);
  }

  /**
   * Resolves all path variables from request.
   *
   * @param request http request
   * @return map of path variables from given request
   */
  default Map<String, String> getPathVariables(NativeWebRequest request) {
    Map<String, String> pathVariables =
        (Map<String, String>)
            request.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);

    return CollectionUtils.isEmpty(pathVariables)
        ? Collections.emptyMap()
        : new LinkedHashMap<>(pathVariables);
  }
}
