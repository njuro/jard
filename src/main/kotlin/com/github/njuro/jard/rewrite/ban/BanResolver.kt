package com.github.njuro.jard.rewrite.ban

import com.github.njuro.jard.rewrite.ban.dto.BanDto
import com.github.njuro.jard.rewrite.common.PLACEHOLDER_BAN
import com.github.njuro.jard.rewrite.utils.PathVariableArgumentResolver
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.ModelAndViewContainer
import java.util.UUID

/** Resolver for mapping ban id (path variable) to respective [BanDto] object  */
@Component
class BanResolver(private val banFacade: BanFacade) : PathVariableArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType == BanDto::class.java


    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val id = getPathVariable(PLACEHOLDER_BAN, webRequest)
        return banFacade.resolveBan(UUID.fromString(id))
    }
}
