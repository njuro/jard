package com.github.njuro.jard.rewrite.user


import com.github.njuro.jard.rewrite.base.BaseFacade
import com.github.njuro.jard.rewrite.config.security.captcha.CaptchaProvider
import com.github.njuro.jard.rewrite.config.security.captcha.CaptchaVerificationException
import com.github.njuro.jard.rewrite.user.dto.CurrentUserEditDto
import com.github.njuro.jard.rewrite.user.dto.CurrentUserPasswordEditDto
import com.github.njuro.jard.rewrite.user.dto.ForgotPasswordDto
import com.github.njuro.jard.rewrite.user.dto.ResetPasswordDto
import com.github.njuro.jard.rewrite.user.dto.UserDto
import com.github.njuro.jard.rewrite.user.dto.UserForm
import com.github.njuro.jard.rewrite.user.token.UserTokenService
import com.github.njuro.jard.rewrite.user.token.UserTokenType
import com.github.njuro.jard.rewrite.utils.EmailService
import com.github.njuro.jard.rewrite.utils.TemplateService
import com.github.njuro.jard.rewrite.utils.validation.PropertyValidationException
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.OffsetDateTime


@Component
class UserFacade(
    @Lazy private val passwordEncoder: PasswordEncoder,
    private val userService: UserService,
    private val userTokenService: UserTokenService,
    @Lazy private val emailService: EmailService,
    private val templateService: TemplateService,
    private val captchaProvider: CaptchaProvider,
    @Value("\${client.base.url:localhost}") private val clientBaseUrl: String
) : BaseFacade<User, UserDto>(), UserDetailsService {


    /**
     * Creates and save new user. Password of the user is encoded by [.passwordEncoder] before
     * storing in database.
     *
     * @param userForm form with user data
     * @return created user
     * @throws PropertyValidationException if user with such name or e-mail already exists
     */
    fun createUser(userForm: UserForm): UserDto {
        if (userService.doesUserExists(userForm.username)) {
            throw PropertyValidationException("User with this name already exists")
        }
        if (userService.doesEmailExists(userForm.email)) {
            throw PropertyValidationException("User with this e-mail already exists")
        }
        val user = userForm.toUser().apply { password = passwordEncoder.encode(password) }
        return toDto(userService.saveUser(user))
    }

    /** [UserService.resolveUser]  */
    fun resolveUser(username: String): UserDto = toDto(userService.resolveUser(username))

    override fun loadUserByUsername(username: String): UserDetails = userService.resolveUser(username)

    /** [UserService.getAllUsers]  */
    fun getAllUsers(): List<UserDto> = toDtoList(userService.getAllUsers())

    /** [UserService.getCurrentUser]  */
    fun getCurrentUser(): UserDto? {
        val currentUser = userService.getCurrentUser() ?: return null
        return toDto(currentUser)
    }

    /** [UserService.hasCurrentUserAuthority]  */
    fun hasCurrentUserAuthority(authority: UserAuthority): Boolean = userService.hasCurrentUserAuthority(authority)

    /**
     * Edits an user. Only e-mail, active role (and therefore default authorities) can be edited.
     *
     * @param oldUser user to be edited
     * @param updatedUser form with new values
     * @return edited user
     */
    fun editUser(oldUser: UserDto, updatedUser: UserForm): UserDto {
        val oldUserEntity = toEntity(oldUser).apply {
            email = updatedUser.email
            role = updatedUser.role
            authorities = updatedUser.role.defaultAuthorities
        }
        if (updatedUser.password.isNotBlank()) {
            if (updatedUser.isPasswordMatching()) {
                oldUserEntity.password = passwordEncoder.encode(updatedUser.password)
            } else {
                throw PropertyValidationException("Passwords do not match")
            }
        }
        return toDto(userService.saveUser(oldUserEntity))
    }

    /**
     * Edits information of current user.
     *
     * @param userChange object with updated information.
     * @throws PropertyValidationException when no user is logged in or updated email is already in
     * use
     * @return updated user
     */
    fun editCurrentUser(userChange: CurrentUserEditDto): UserDto {
        val currentUser = userService.getCurrentUser()
            ?: throw PropertyValidationException("No user is authenticated")
        if (userChange.email.equals(currentUser.email, ignoreCase = true)) {
            return toDto(currentUser)
        }
        if (userService.doesEmailExists(userChange.email)) {
            throw PropertyValidationException("E-mail already in use by different user")
        }
        currentUser.email = userChange.email
        return toDto(userService.saveUser(currentUser))
    }

    /**
     * Edits password of current user.
     *
     * @param passwordChange object with new password
     * @throws PropertyValidationException when no user is logged in or given current password is
     * incorrect
     */
    fun editCurrentUserPassword(passwordChange: CurrentUserPasswordEditDto) {
        val currentUser = userService.getCurrentUser()
            ?: throw PropertyValidationException("No user is authenticated")
        if (!passwordEncoder.matches(passwordChange.currentPassword, currentUser.password)) {
            throw PropertyValidationException("Incorrect current password")
        }
        currentUser.password = passwordEncoder.encode(passwordChange.newPassword)
        userService.saveUser(currentUser)
    }

    /**
     * Sends e-mail with link for password reset to given user.
     *
     * @param forgotRequest - metadata about reset request
     * @throws PropertyValidationException if provided captcha token is invalid, user is not found,
     * user has already valid reset token, user doesn't have e-mail set or sending an e-mail
     * failed.
     */
    fun sendPasswordResetLink(forgotRequest: ForgotPasswordDto) {
        logger.info { "Password reset link requested by user ${forgotRequest.username}" }
        verifyCaptcha(forgotRequest.captchaToken)
        val user: User = userService.resolveUser(forgotRequest.username)
        if (userTokenService.doesTokenForUserExists(user, UserTokenType.PASSWORD_RESET)) {
            throw PropertyValidationException("User ${user.username} has already valid password reset token issued")
        }
        if (user.email == null) {
            throw PropertyValidationException("User ${user.username} does not have e-mail address set")
        }
        val token = userTokenService.generateToken(user, UserTokenType.PASSWORD_RESET)
        val message = templateService.resolveTemplate("forgot_password", mapOf(
          "username" to user.username,
          "clientUrl" to clientBaseUrl,
          "token" to token.value,
          "ip" to forgotRequest.ip,
          "userAgent" to (forgotRequest.userAgent ?: "Unknown"), "timestamp" to OffsetDateTime.now()
        ))
        emailService.sendMail(user.email!!, "Reset your password", message)
    }

    /**
     * Resets password for user.
     *
     * @param resetRequest - metadata about reset request
     * @throws PropertyValidationException if provided token is invalid or sending the mail failed.
     */
    fun resetPassword(resetRequest: ResetPasswordDto) {
        val token = userTokenService.resolveToken(resetRequest.token, UserTokenType.PASSWORD_RESET)
            ?: throw PropertyValidationException("Invalid token")
        val user = token.user
        logger.info { "Resetting password of user ${user.username}" }
        user.password = passwordEncoder.encode(resetRequest.password)
        userService.saveUser(user)
        userTokenService.deleteToken(user, UserTokenType.PASSWORD_RESET)
        val message = templateService.resolveTemplate("reset_password", mapOf(
                "username" to user.username,
                "clientUrl" to clientBaseUrl,
                "timestamp" to OffsetDateTime.now()
            )
        )
        emailService.sendMail(user.email!!, "Your password has been updated", message)
    }

    /** [UserService.deleteUser]  */
    fun deleteUser(user: UserDto) {
        userService.deleteUser(toEntity(user))
    }

    /**
     * Verifies CAPTCHA response token.
     *
     * @param captchaToken CAPTCHA response token to verify
     * @throws CaptchaVerificationException if verification of token failed
     */
    private fun verifyCaptcha(captchaToken: String) {
        val result = captchaProvider.verifyCaptchaToken(captchaToken)
        if (!result.isVerified) {
            logger.warn { "Captcha verification failed: [${result.errors.joinToString(", ")}]" }
            throw CaptchaVerificationException()
        }
    }

    companion object: KLogging()
}
