package com.github.njuro.jard.user

import com.github.njuro.jard.*
import com.github.njuro.jard.common.InputConstraints.MAX_USERNAME_LENGTH
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.user.dto.PasswordEditDto
import com.github.njuro.jard.user.dto.UserDto
import com.github.njuro.jard.user.dto.UserForm
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.optional.shouldNotBePresent
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.*
import org.springframework.transaction.annotation.Transactional

@WithContainerDatabase
@Transactional
internal class UserIntegrationTest : MockMvcTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Nested
    @DisplayName("create user")
    @WithMockJardUser(UserAuthority.MANAGE_USERS)
    inner class CreateUser {
        private fun createUser(userForm: UserForm) = mockMvc.post(Mappings.API_ROOT_USERS) { body(userForm) }

        @Test
        fun `create valid user`() {
            createUser(user().toForm()).andExpect { status { isCreated() } }.andReturnConverted<UserDto>()
                .shouldNotBeNull()
        }

        @Test
        fun `don't create invalid user`() {
            createUser(user(username = randomString(MAX_USERNAME_LENGTH + 1)).toForm()).andExpect { status { isBadRequest() } }
        }
    }

    @Test
    @WithMockJardUser(UserAuthority.MANAGE_USERS)
    fun `get all users`() {
        (1..3).forEach { userRepository.save(user(username = "User $it", email = "user$it@mail.com")) }

        mockMvc.get(Mappings.API_ROOT_USERS) { setUp() }.andExpect { status { isOk() } }
            .andReturnConverted<List<UserDto>>() shouldHaveSize 3
    }

    @Nested
    @DisplayName("create user")
    inner class GetCurrentUser {
        private fun getCurrentUser() = mockMvc.get("${Mappings.API_ROOT_USERS}/current") { setUp() }

        @Test
        @WithMockJardUser
        fun `get current user when someone is logged in`() {
            getCurrentUser().andExpect { status { isOk() } }.andReturnConverted<UserDto>().shouldNotBeNull()
        }

        @Test
        fun `get current user when nobody is logged in`() {
            getCurrentUser().andExpect { status { isOk() } }.andReturn().response.contentLength shouldBe 0
        }
    }

    @Nested
    @DisplayName("edit user")
    @WithMockJardUser(UserAuthority.MANAGE_USERS)
    inner class EditUser {
        private fun editUser(username: String, editForm: UserForm) =
            mockMvc.put("${Mappings.API_ROOT_USERS}/$username") { body(editForm) }

        @Test
        fun `edit user`() {
            val user = userRepository.save(user(role = UserRole.MODERATOR))

            editUser(user.username, user.toForm().apply { role = UserRole.ADMIN }).andExpect { status { isOk() } }
                .andReturnConverted<UserDto>().role shouldBe UserRole.ADMIN
        }

        @Test
        fun `don't edit non-existing user`() {
            editUser("xxx", user().toForm()).andExpect { status { isNotFound() } }
        }
    }

    @Nested
    @DisplayName("edit current user password")
    inner class EditUserCurrentUserPassword {
        private fun editCurrentUserPassword(passwordEdit: PasswordEditDto) =
            mockMvc.patch("${Mappings.API_ROOT_USERS}/current/password") { body(passwordEdit) }

        @Test
        @WithMockJardUser(password = "\$2b\$31\$Pr0po9XrlgIzkeUMCeheFOXJiVd/K.ISm0ra4SGAHgpWxY6b4CZaS") // nasty hack
        fun `edit user password when user is authenticated`() {
            editCurrentUserPassword(passwordEdit("oldPassword", "newPassword")).andExpect { status { isOk() } }
        }

        @Test
        fun `don't edit user password when user is not authenticated`() {
            editCurrentUserPassword(
                passwordEdit(
                    "oldPassword",
                    "newPassword"
                )
            ).andExpect { status { isBadRequest() } }
        }
    }


    @Test
    @WithMockJardUser(UserAuthority.MANAGE_USERS)
    fun `delete user`() {
        val user = userRepository.save(user())

        mockMvc.delete("${Mappings.API_ROOT_USERS}/${user.username}") { setUp() }.andExpect { status { isOk() } }
        userRepository.findById(user.id).shouldNotBePresent()
    }


}