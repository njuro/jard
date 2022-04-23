package com.github.njuro.jard.rewrite.utils

import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE

/** Common interface for resolvers resolving method arguments from path variables.  */
interface PathVariableArgumentResolver : HandlerMethodArgumentResolver {
    /**
     * Resolves path variable from request.
     *
     * @param name name of path variable
     * @param request http request
     * @return String representation of given path variable or null, if path variable does not exists
     */
    fun getPathVariable(name: String?, request: NativeWebRequest): String? = getPathVariables(request)[name]


    /**
     * Resolves all path variables from request.
     *
     * @param request http request
     * @return map of path variables from given request
     */
    @Suppress("UNCHECKED_CAST")
    fun getPathVariables(request: NativeWebRequest): Map<String, String> {
        val pathVariables = request.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, SCOPE_REQUEST)
        return pathVariables as? Map<String, String> ?: emptyMap()
    }
}
