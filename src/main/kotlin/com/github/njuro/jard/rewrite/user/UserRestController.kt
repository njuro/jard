package com.github.njuro.jard.rewrite.user

import com.github.njuro.jard.rewrite.common.API_ROOT_USERS
import com.github.njuro.jard.rewrite.common.PATH_VARIABLE_USER
import com.github.njuro.jard.rewrite.config.security.captcha.CaptchaVerificationException
import com.github.njuro.jard.rewrite.config.security.methods.HasAuthorities
import com.github.njuro.jard.rewrite.user.UserAuthority.MANAGE_USERS
import com.github.njuro.jard.rewrite.user.dto.CurrentUserEditDto
import com.github.njuro.jard.rewrite.user.dto.CurrentUserPasswordEditDto
import com.github.njuro.jard.rewrite.user.dto.ForgotPasswordDto
import com.github.njuro.jard.rewrite.user.dto.ResetPasswordDto
import com.github.njuro.jard.rewrite.user.dto.UserDto
import com.github.njuro.jard.rewrite.user.dto.UserForm
import com.github.njuro.jard.rewrite.utils.getClientIp
import com.github.njuro.jard.utils.validation.PropertyValidationException
import com.jfilter.filter.FieldFilterSetting
import com.jfilter.filter.FilterBehaviour.KEEP_FIELDS
import mu.KLogging
import org.springframework.http.HttpHeaders.USER_AGENT
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping(API_ROOT_USERS)
class UserRestController(private val userFacade: UserFacade) {
    @PostMapping
    @HasAuthorities(MANAGE_USERS)
    fun createUser(@RequestBody @Valid userForm: UserForm, request: HttpServletRequest): ResponseEntity<UserDto> {
        userForm.registrationIp = request.remoteAddr
        return ResponseEntity.status(CREATED).body(userFacade.createUser(userForm))
    }

    @HasAuthorities(MANAGE_USERS)
    @FieldFilterSetting(
        className = UserDto::class,
        fields = ["username", "email", "enabled", "role", "authorities"],
        behaviour = KEEP_FIELDS
    )
    @GetMapping
    fun getAllUsers(): List<UserDto> = userFacade.getAllUsers()

    @FieldFilterSetting(
        className = UserDto::class,
        fields = ["username", "email", "role", "authorities"],
        behaviour = KEEP_FIELDS
    )
    @GetMapping("/current")
    fun getCurrentUser(): UserDto? = userFacade.getCurrentUser()

    @PutMapping(PATH_VARIABLE_USER)
    @HasAuthorities(MANAGE_USERS)
    fun editUser(oldUser: UserDto, @RequestBody userForm: UserForm): UserDto =
        userFacade.editUser(oldUser, userForm)

    @PatchMapping("/current")
    @FieldFilterSetting(
        className = UserDto::class,
        fields = ["username", "email", "role", "authorities"],
        behaviour = KEEP_FIELDS
    )
    fun editCurrentUser(@RequestBody @Valid userChange: CurrentUserEditDto) =
        userFacade.editCurrentUser(userChange)

    @PatchMapping("/current/password")
    fun editCurrentUserPassword(
        @RequestBody @Valid passwordChange: CurrentUserPasswordEditDto
    ): ResponseEntity<Any> {
        userFacade.editCurrentUserPassword(passwordChange)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/forgot-password")
    fun forgotPassword(
        @RequestBody forgotRequest: ForgotPasswordDto, httpRequest: HttpServletRequest
    ): ResponseEntity<Any> {
        forgotRequest.apply {
            ip = httpRequest.getClientIp()
            userAgent = httpRequest.getHeader(USER_AGENT)
        }

        try {
            userFacade.sendPasswordResetLink(forgotRequest)
        } catch (ex: CaptchaVerificationException) {
            throw ex
        } catch (ex: PropertyValidationException) {
            // exception is silenced and not propagated to client for security reasons.
            logger.error(ex) { "Request for password reset failed" }
        } catch (ex: UserNotFoundException) {
            // exception is silenced and not propagated to client for security reasons.
            logger.error(ex) { "Request for password reset failed" }
        }

        return ResponseEntity.ok().build()
    }

    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody @Valid resetRequest: ResetPasswordDto): ResponseEntity<Any> {
        userFacade.resetPassword(resetRequest)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping(PATH_VARIABLE_USER)
    @HasAuthorities(MANAGE_USERS)
    fun deleteUser(user: UserDto): ResponseEntity<Any> {
        userFacade.deleteUser(user)
        return ResponseEntity.ok().build()
    }

    companion object: KLogging()
}
