package com.github.njuro.jard.user

import com.github.njuro.jard.*
import com.github.njuro.jard.common.InputConstraints.*
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.user.dto.UserDto
import com.github.njuro.jard.user.dto.UserForm
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.slot
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
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
                    .apply { password = "firstpass"; passwordRepeated = "secondpass" })
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
            jsonPath("$.email") { doesNotExist() }
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


    @Test
    @WithMockJardUser(UserAuthority.MANAGE_USERS)
    fun `delete user`() {
        val user = user(username = "Anonymous")
        every { userFacade.resolveUser(user.username) } returns user.toDto()
        every { userFacade.deleteUser(ofType(UserDto::class)) } just Runs

        mockMvc.delete("${Mappings.API_ROOT_USERS}/${user.username}") { setUp() }.andExpect { status { isOk() } }
    }
}