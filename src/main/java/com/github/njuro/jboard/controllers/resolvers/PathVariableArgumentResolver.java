package com.github.njuro.jboard.controllers.resolvers;

import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Common interface for resolvers resolving path variables
 *
 * @author njuro
 */
public interface PathVariableArgumentResolver extends HandlerMethodArgumentResolver {

    /**
     * Resolves path variable from request
     *
     * @param name    of path variable
     * @param request - web request
     * @return String representation of given path variable or null, if path variable does not exists
     */
    default String getPathVariable(String name, NativeWebRequest request) {
        return getPathVariables(request).get(name);
    }

    /**
     * @param request - web request
     * @return map of path variables from given request
     */
    default Map<String, String> getPathVariables(NativeWebRequest request) {
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);

        return CollectionUtils.isEmpty(pathVariables) ? Collections.emptyMap() : new LinkedHashMap<>(pathVariables);
    }
}
