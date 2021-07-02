package com.github.njuro.jard.user

import com.github.njuro.jard.MockMvcTest
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.WithMockJardUser
import com.github.njuro.jard.common.InputConstraints.MAX_USERNAME_LENGTH
import com.github.njuro.jard.common.InputConstraints.MIN_PASSWORD_LENGTH
import com.github.njuro.jard.common.InputConstraints.MIN_USERNAME_LENGTH
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.forgotPasswordRequest
import com.github.njuro.jard.passwordEdit
import com.github.njuro.jard.randomString
import com.github.njuro.jard.resetPasswordRequest
import com.github.njuro.jard.toForm
import com.github.njuro.jard.user
import com.github.njuro.jard.user.dto.CurrentUserEditDto
import com.github.njuro.jard.user.dto.CurrentUserPasswordEditDto
import com.github.njuro.jard.user.dto.ForgotPasswordDto
import com.github.njuro.jard.user.dto.ResetPasswordDto
import com.github.njuro.jard.user.dto.UserDto
import com.github.njuro.jard.user.dto.UserForm
import com.github.njuro.jard.userEdit
import com.github.njuro.jard.utils.validation.PropertyValidationException
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.slot
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@WithContainerDatabase
internal class UserControllerTest : MockMvcTest() {

    @MockkBean
    private lateinit var userFacade: UserFacade

    @Nested
    @DisplayName("create user")
    @WithMockJardUser(UserAuthority.MANAGE_USERS)
    inner class CreateUser {
        private fun createUser(userForm: UserForm, ip: String = "1.2.3.4") = mockMvc.post(Mappings.API_ROOT_USERS) {
            body(userForm)
            with { it.apply { remoteAddr = ip } }
        }

        @Test
        fun `create valid user`() {
            val user = user(username = "John", registrationIp = "127.0.0.1")
            val userForm = slot<UserForm>()
            every { userFacade.createUser(capture(userForm)) } returns user.toDto()

            val response = createUser(user.toForm()).andExpect { status { isCreated() } }.andReturnConverted<UserDto>()
            response.username shouldBe user.username
            userForm.captured.registrationIp shouldBe "1.2.3.4"
        }

        @Test
        fun `don't create user with invalid username`() {
            createUser(user(username = randomString(MIN_USERNAME_LENGTH - 1)).toForm()).andExpectValidationError("username")
            createUser(user(username = randomString(MAX_USERNAME_LENGTH + 1)).toForm()).andExpectValidationError("username")
        }

        @Test
        fun `don't create user with invalid password`() {
            createUser(user(password = randomString(MIN_PASSWORD_LENGTH - 1)).toForm()).andExpectValidationError("password")
        }

        @Test
        fun `don't create user with invalid email`() {
            createUser(user(email = "abcdefgh").toForm()).andExpectValidationError("email")
        }

        @Test
        fun `don't create user with invalid registration ip`() {
            createUser(user(registrationIp = "1234").toForm()).andExpectValidationError("registrationIp")
        }

        @Test
        fun `don't create user with invalid role`() {
            createUser(user(role = null).toForm()).andExpectValidationError("role")
        }

        @Test
        fun `don't create user with non-matching password`() {
            createUser(
                user().toForm()
                    .apply { password = "firstpass"; passwordRepeated = "secondpass" }
            )
                .andExpectValidationError("passwordMatching")
        }
    }

    @Test
    @WithMockJardUser(UserAuthority.MANAGE_USERS)
    fun `get all users`() {
        every { userFacade.allUsers } returns (1..3).map { user(username = "User $it").toDto() }

        mockMvc.get(Mappings.API_ROOT_USERS) { setUp() }.andExpect {
            status { isOk() }
            jsonPath("$[*].username") { exists() }
            jsonPath("$[*].email") { exists() }
            jsonPath("$[*].role") { exists() }
            jsonPath("$[*].lastLoginIp") { doesNotExist() }
            jsonPath("$[*].registrationIp") { doesNotExist() }
        }.andReturnConverted<List<UserDto>>() shouldHaveSize 3
    }

    @Test
    fun `get current user`() {
        every { userFacade.currentUser } returns user(username = "Anonymous", role = UserRole.ADMIN).toDto()

        val response = mockMvc.get("${Mappings.API_ROOT_USERS}/current") { setUp() }.andExpect {
            status { isOk() }
            jsonPath("$.username") { exists() }
            jsonPath("$.registrationIp") { doesNotExist() }
        }.andReturnConverted<UserDto>()
        response.username shouldBe "Anonymous"
        response.role shouldBe UserRole.ADMIN
    }

    @Nested
    @DisplayName("edit user")
    @WithMockJardUser(UserAuthority.MANAGE_USERS)
    inner class EditUser {
        private fun editUser(username: String, editForm: UserForm) =
            mockMvc.put("${Mappings.API_ROOT_USERS}/$username") { body(editForm) }

        @Test
        fun `edit user`() {
            val user = user(username = "Anonymous")
            every { userFacade.resolveUser(user.username) } returns user.toDto()
            every { userFacade.editUser(ofType(UserDto::class), ofType(UserForm::class)) } returns user.toDto()

            val response = editUser(user.username, user.toForm())
                .andExpect { status { isOk() } }
                .andReturnConverted<UserDto>()
            response.username shouldBe user.username
        }

        @Test
        fun `don't edit non-existing user`() {
            every { userFacade.resolveUser(ofType(String::class)) } throws UserNotFoundException()
            editUser("xxx", user().toForm()).andExpect { status { isNotFound() } }
        }
    }

    @Nested
    @DisplayName("edit current user")
    inner class EditUserCurrentUser {
        private fun editCurrentUser(userEdit: CurrentUserEditDto) =
            mockMvc.patch("${Mappings.API_ROOT_USERS}/current") { body(userEdit) }

        @Test
        fun `edit user email when new email is valid`() {
            every { userFacade.editCurrentUser(ofType(CurrentUserEditDto::class)) } answers { user(email = firstArg<CurrentUserEditDto>().email).toDto() }

            editCurrentUser(userEdit("new@mail.com")).andExpect {
                status { isOk() }
                jsonPath("$.username") { exists() }
                jsonPath("$.registrationIp") { doesNotExist() }
            }.andReturnConverted<UserDto>()
                .shouldNotBeNull()
        }

        @Test
        fun `don't edit user email when new email is not valid`() {
            every { userFacade.editCurrentUser(ofType(CurrentUserEditDto::class)) } answers { user(email = firstArg<CurrentUserEditDto>().email).toDto() }

            editCurrentUser(userEdit("xxx")).andExpect { status { isBadRequest() } }
        }
    }

    @Nested
    @DisplayName("edit current user password")
    inner class EditUserCurrentUserPassword {
        private fun editCurrentUserPassword(passwordEdit: CurrentUserPasswordEditDto) =
            mockMvc.patch("${Mappings.API_ROOT_USERS}/current/password") { body(passwordEdit) }

        @Test
        fun `edit user password when new password is valid`() {
            every { userFacade.editCurrentUserPassword(ofType(CurrentUserPasswordEditDto::class)) } just Runs

            editCurrentUserPassword(passwordEdit("oldPassword", "newPassword")).andExpect { status { isOk() } }
        }

        @Test
        fun `don't edit user password when new password is not valid`() {
            every { userFacade.editCurrentUserPassword(ofType(CurrentUserPasswordEditDto::class)) } just Runs

            editCurrentUserPassword(passwordEdit(null, "a")).andExpectValidationError("currentPassword")
            editCurrentUserPassword(passwordEdit("oldPassword", "a")).andExpectValidationError("newPassword")
            editCurrentUserPassword(
                passwordEdit(
                    "oldPassword",
                    "newPassword",
                    newPasswordRepeated = "otherPassword"
                )
            ).andExpectValidationError("passwordMatching")
        }
    }

    @Nested
    @DisplayName("forgot password")
    inner class ForgotPassword {
        private fun forgotPassword(request: ForgotPasswordDto) =
            mockMvc.post("${Mappings.API_ROOT_USERS}/forgot-password") {
                body(request)
                with { it.apply { remoteAddr = "127.0.0.1" } }
                header(HttpHeaders.USER_AGENT, "test-user-agent")
            }

        @Test
        fun `valid request`() {
            val request = slot<ForgotPasswordDto>()
            every { userFacade.sendPasswordResetLink(capture(request)) } just Runs

            forgotPassword(
                forgotPasswordRequest(
                    "user",
                    ip = "1.2.3.4",
                    userAgent = "fake-ua"
                )
            ).andExpect { status { isOk() } }

            request.captured.should {
                it.username shouldBe "user"
                it.ip shouldBe "127.0.0.1"
                it.userAgent shouldBe "test-user-agent"
            }
        }

        @Test
        fun `invalid request - validation exception`() {
            every { userFacade.sendPasswordResetLink(any()) } throws PropertyValidationException(
                ""
            )

            // we are expecting 200 despite exception (silenced for security reasons)
            forgotPassword(forgotPasswordRequest("user")).andExpect { status { isOk() } }
        }

        @Test
        fun `invalid request - user not found exception`() {
            every { userFacade.sendPasswordResetLink(any()) } throws UserNotFoundException()

            // we are expecting 200 despite exception (silenced for security reasons)
            forgotPassword(forgotPasswordRequest("user")).andExpect { status { isOk() } }
        }
    }

    @Nested
    @DisplayName("reset user password")
    inner class ResetUserPassword {
        private fun resetPassword(request: ResetPasswordDto) =
            mockMvc.post("${Mappings.API_ROOT_USERS}/reset-password") { body(request) }

        @BeforeEach
        fun setUp() {
            every { userFacade.resetPassword(ofType(ResetPasswordDto::class)) } just Runs
        }

        @Test
        fun `valid reset request`() {
            resetPassword(resetPasswordRequest(password = "newPassword"))
                .andExpect { status { isOk() } }
        }

        @Test
        fun `invalid reset request`() {
            resetPassword(resetPasswordRequest(password = "newPassword", passwordRepeated = "xxx"))
                .andExpect { status { isBadRequest() } }
            resetPassword(resetPasswordRequest(password = "xxx"))
                .andExpect { status { isBadRequest() } }
        }
    }

    @Test
    @WithMockJardUser(UserAuthority.MANAGE_USERS)
    fun `delete user`() {
        val user = user(username = "Anonymous")
        every { userFacade.resolveUser(user.username) } returns user.toDto()
        every { userFacade.deleteUser(ofType(UserDto::class)) } just Runs

        mockMvc.delete("${Mappings.API_ROOT_USERS}/${user.username}") { setUp() }.andExpect { status { isOk() } }
    }
}
