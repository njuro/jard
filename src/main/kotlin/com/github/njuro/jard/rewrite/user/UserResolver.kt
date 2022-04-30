package com.github.njuro.jard.rewrite.user

import com.github.njuro.jard.rewrite.common.PLACEHOLDER_USER
import com.github.njuro.jard.rewrite.user.dto.UserDto
import com.github.njuro.jard.utils.PathVariableArgumentResolver
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.ModelAndViewContainer

/** Resolver for mapping username (path variable) to respective [UserDto] object  */
@Component
class UserResolver(private val userFacade: UserFacade) : PathVariableArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType == UserDto::class.java


    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): UserDto {
        val username = getPathVariable(PLACEHOLDER_USER, webRequest)
        return userFacade.resolveUser(username)
    }
}
