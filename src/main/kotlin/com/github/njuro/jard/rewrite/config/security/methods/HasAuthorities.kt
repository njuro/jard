package com.github.njuro.jard.rewrite.config.security.methods

import com.github.njuro.jard.user.UserAuthority
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.annotation.AnnotationTarget.PROPERTY_SETTER

/**
 * Annotation used to protect endpoints. Logged in user has access to endpoint if and only if he/she
 * has **all** of the user authorities defined in [.value].
 *
 * @see UserAuthority
 */
@Retention(RUNTIME)
@Target(FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
annotation class HasAuthorities(vararg val value: UserAuthority)
