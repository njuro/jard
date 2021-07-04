package com.github.njuro.jard.user

import com.github.njuro.jard.MapperTest
import com.github.njuro.jard.TestDataRepository
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.config.security.captcha.CaptchaProvider
import com.github.njuro.jard.config.security.captcha.CaptchaVerificationException
import com.github.njuro.jard.forgotPasswordRequest
import com.github.njuro.jard.passwordEdit
import com.github.njuro.jard.resetPasswordRequest
import com.github.njuro.jard.security.captcha.MockCaptchaVerificationResult
import com.github.njuro.jard.toForm
import com.github.njuro.jard.user
import com.github.njuro.jard.user.token.UserTokenService
import com.github.njuro.jard.user.token.UserTokenType
import com.github.njuro.jard.userEdit
import com.github.njuro.jard.userToken
import com.github.njuro.jard.utils.EmailService
import com.github.njuro.jard.utils.validation.PropertyValidationException
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.optional.shouldNotBePresent
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeBlank
import io.mockk.Called
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional

@WithContainerDatabase
@Transactional
internal class UserFacadeTest : MapperTest() {

    @Autowired
    private lateinit var userFacade: UserFacade

    @SpykBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userTokenService: UserTokenService

    @MockkBean
    private lateinit var emailService: EmailService

    @MockkBean
    private lateinit var captchaProvider: CaptchaProvider

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var db: TestDataRepository

    @Nested
    @DisplayName("create user")
    inner class CreateUser {
        @Test
        fun `creat valid user`() {
            val userForm = user(username = "Anonymous", password = "secret").toForm()

            val created = userFacade.createUser(userForm)
            created.username shouldBe userForm.username
            db.select(created).shouldBePresent {
                it.password.shouldNotBeBlank()
                it.password shouldNotBe userForm.password
            }
        }

        @Test
        fun `don't create user with duplicate username`() {
            val user = db.insert(user(username = "John"))

            shouldThrow<PropertyValidationException> {
                userFacade.createUser(user(username = user.username).toForm())
            }
        }

        @Test
        fun `don't create user with duplicate email`() {
            val user = db.insert(user(email = "john@mail.com"))

            shouldThrow<PropertyValidationException> {
                userFacade.createUser(user(email = user.email).toForm())
            }
        }
    }

    @Test
    fun `load user by username`() {
        val user = db.insert(user(username = "John"))

        userFacade.loadUserByUsername(user.username).shouldNotBeNull()
    }

    @Test
    fun `don't load non-existing user by username`() {
        shouldThrow<UserNotFoundException> {
            userFacade.loadUserByUsername("John")
        }
    }

    @Nested
    @DisplayName("edit user")
    inner class EditUser {
        @Test
        fun `edit user details without password`() {
            val originalUser = user(
                username = "Anonymous",
                role = UserRole.MODERATOR,
                email = "old@mail.com",
                authorities = setOf(UserAuthority.MANAGE_BANS)
            )
            val updatedUser =
                originalUser.toForm().apply { username = "John"; role = UserRole.ADMIN; email = "new@mail.com" }

            userFacade.editUser(db.insert(originalUser).toDto(), updatedUser).should {
                it.username shouldBe originalUser.username
                it.email shouldBe updatedUser.email
                it.role shouldBe updatedUser.role
                it.authorities.shouldContainExactly(updatedUser.role.defaultAuthorites)
            }
        }

        @Test
        fun `edit user password`() {
            val originalUser = db.insert(user(username = "Anonymous", password = "oldPassword"))
            val updatedUser = originalUser.toForm().apply { password = "newPassword"; passwordRepeated = "newPassword" }

            userFacade.editUser(originalUser.toDto(), updatedUser)
            db.select(originalUser).shouldBePresent {
                passwordEncoder.matches("newPassword", it.password).shouldBeTrue()
            }
        }

        @Test
        fun `don't edit user when password don't match`() {
            val originalUser = user(username = "Anonymous")
            val updatedUser =
                originalUser.toForm().apply { password = "newPassword"; passwordRepeated = "anotherPassword" }

            shouldThrow<PropertyValidationException> {
                userFacade.editUser(db.insert(originalUser).toDto(), updatedUser)
            }
        }
    }

    @Nested
    @DisplayName("edit current user")
    inner class EditCurrentUser {

        @Test
        fun `edit email if user is authenticated and new email is not used yet`() {
            val user = db.insert(user(username = "user", email = "old@mail.com"))
            every { userService.currentUser } returns user

            userFacade.editCurrentUser(userEdit("new@mail.com")).email shouldBe "new@mail.com"
        }

        @Test
        fun `do nothing if user is authenticated and new email is the same as old`() {
            val user = db.insert(user(username = "user", email = "new@mail.com"))
            every { userService.currentUser } returns user

            userFacade.editCurrentUser(userEdit("NEW@MAIL.COM")).email shouldBe "new@mail.com"
        }

        @Test
        fun `don't edit if user is not authenticated`() {
            every { userService.currentUser } returns null

            shouldThrow<PropertyValidationException> {
                userFacade.editCurrentUser(userEdit("new@mail.com"))
            }
        }

        @Test
        fun `don't edit if new email is already in use`() {
            val user1 = db.insert(user(username = "user1", email = "old@mail.com"))
            val user2 = db.insert(user(username = "user2", email = "new@mail.com"))
            every { userService.currentUser } returns user1

            shouldThrow<PropertyValidationException> {
                userFacade.editCurrentUser(userEdit(user2.email))
            }
        }
    }

    @Nested
    @DisplayName("edit current user's password")
    inner class EditCurrentUserPassword {

        @Test
        fun `edit password if user is authenticated and current password is correct`() {
            val user = db.insert(user(username = "user", password = passwordEncoder.encode("oldPassword")))
            every { userService.currentUser } returns user

            userFacade.editCurrentUserPassword(passwordEdit("oldPassword", "newPassword"))
            db.select(user).shouldBePresent {
                passwordEncoder.matches("newPassword", it.password).shouldBeTrue()
            }
        }

        @Test
        fun `don't edit if user is not authenticated`() {
            every { userService.currentUser } returns null

            shouldThrow<PropertyValidationException> {
                userFacade.editCurrentUserPassword(passwordEdit("oldPassword", "newPassword"))
            }
        }

        @Test
        fun `don't edit if current password is incorrect`() {
            val user = db.insert(user(username = "user", password = passwordEncoder.encode("oldPassword")))
            every { userService.currentUser } returns user

            shouldThrow<PropertyValidationException> {
                userFacade.editCurrentUserPassword(passwordEdit("wrongPassword", "newPassword"))
            }
        }
    }

    @Nested
    @DisplayName("send password reset link")
    inner class SendPasswordResetLink {
        @BeforeEach
        fun setUp() {
            every { captchaProvider.verifyCaptchaToken(any()) } returns MockCaptchaVerificationResult.VALID
        }

        @Test
        fun `send reset link if user exists and has valid email`() {
            val user = db.insert(user(username = "user", email = "user@mail.com"))

            val email = slot<String>()
            val message = slot<String>()
            every { emailService.sendMail(capture(email), ofType(String::class), capture(message)) } just Runs

            val forgotRequest = forgotPasswordRequest(user.username, ip = "127.0.0.1", userAgent = "test-user-agent")
            userFacade.sendPasswordResetLink(forgotRequest)

            val token = userTokenService.resolveToken(user, UserTokenType.PASSWORD_RESET).shouldNotBeNull()
            email.captured shouldBe user.email
            message.captured.should {
                it shouldContain forgotRequest.username
                it shouldContain forgotRequest.ip
                it shouldContain forgotRequest.userAgent
                it shouldContain token.value
            }
        }

        @Test
        fun `don't send reset link if user doesn't exist`() {
            shouldThrow<UserNotFoundException> {
                userFacade.sendPasswordResetLink(forgotPasswordRequest(username = "xxx"))
            }
            verify {
                emailService wasNot Called
            }
        }

        @Test
        fun `don't send reset link if reset token already exists for user`() {
            val user = db.insert(user(username = "user", email = "user@mail.com"))
            db.insert(userToken(user, "xxx", UserTokenType.PASSWORD_RESET))

            shouldThrow<PropertyValidationException> {
                userFacade.sendPasswordResetLink(forgotPasswordRequest(username = user.username))
            }
            verify {
                emailService wasNot Called
            }
        }

        @Test
        fun `don't send reset link if user doesn't have email`() {
            val user = db.insert(user(username = "user", email = null))

            shouldThrow<PropertyValidationException> {
                userFacade.sendPasswordResetLink(forgotPasswordRequest(username = user.username))
            }
            verify {
                emailService wasNot Called
            }
        }

        @Test
        fun `don't send reset link if captcha is invalid`() {
            val user = db.insert(user(username = "user", email = "user@mail.com"))
            every { captchaProvider.verifyCaptchaToken(any()) } returns MockCaptchaVerificationResult.INVALID

            shouldThrow<CaptchaVerificationException> {
                userFacade.sendPasswordResetLink(forgotPasswordRequest(username = user.username))
            }
            verify {
                emailService wasNot Called
            }
        }
    }

    @Nested
    @DisplayName("reset user password")
    inner class ResetUserPassword {

        @Test
        fun `reset user password if token is valid`() {
            val user = db.insert(user(username = "user", password = passwordEncoder.encode("oldPassword")))
            val token = db.insert(userToken(user, "abcdef", UserTokenType.PASSWORD_RESET))
            val email = slot<String>()
            val message = slot<String>()
            every { emailService.sendMail(capture(email), ofType(String::class), capture(message)) } just Runs

            userFacade.resetPassword(resetPasswordRequest(password = "newPassword", token = token.value))
            db.select(user).shouldBePresent {
                passwordEncoder.matches("newPassword", it.password).shouldBeTrue()
            }
            db.select(token).shouldNotBePresent()
            email.captured shouldBe user.email
            message.captured shouldContain user.username
        }

        @Test
        fun `don't reset password if token is of invalid type`() {
            val user = db.insert(user(username = "user", password = passwordEncoder.encode("oldPassword")))
            val token = db.insert(userToken(user, "abcdef", UserTokenType.EMAIL_VERIFICATION))

            shouldThrow<PropertyValidationException> {
                userFacade.resetPassword(resetPasswordRequest(user.username, "newPassword", token = token.value))
            }
            verify {
                emailService wasNot Called
            }
        }

        @Test
        fun `don't reset password if token is missing`() {
            val user = db.insert(user(username = "user", password = passwordEncoder.encode("oldPassword")))

            shouldThrow<PropertyValidationException> {
                userFacade.resetPassword(resetPasswordRequest(user.username, "newPassword", token = "xxx"))
            }
            verify {
                emailService wasNot Called
            }
        }
    }
}
