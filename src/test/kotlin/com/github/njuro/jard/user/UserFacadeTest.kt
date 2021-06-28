package com.github.njuro.jard.user

import com.github.njuro.jard.*
import com.github.njuro.jard.utils.validation.FormValidationException
import com.ninjasquad.springmockk.SpykBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeBlank
import io.mockk.every
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
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Nested
    @DisplayName("create user")
    inner class CreateUser {
        @Test
        fun `creat valid user`() {
            val userForm = user(username = "Anonymous", password = "secret").toForm()

            val created = userFacade.createUser(userForm)
            created.username shouldBe userForm.username
            userRepository.findById(created.id).shouldBePresent {
                it.password.shouldNotBeBlank()
                it.password shouldNotBe userForm.password
            }
        }

        @Test
        fun `don't create user with duplicate username`() {
            val user = userRepository.save(user(username = "John"))

            shouldThrow<FormValidationException> {
                userFacade.createUser(user(username = user.username).toForm())
            }
        }

        @Test
        fun `don't create user with duplicate email`() {
            val user = userRepository.save(user(email = "john@mail.com"))

            shouldThrow<FormValidationException> {
                userFacade.createUser(user(email = user.email).toForm())
            }
        }
    }

    @Test
    fun `load user by username`() {
        val user = userRepository.save(user(username = "John"))

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

            userFacade.editUser(userRepository.save(originalUser).toDto(), updatedUser).should {
                it.username shouldBe originalUser.username
                it.email shouldBe updatedUser.email
                it.role shouldBe updatedUser.role
                it.authorities.shouldContainExactly(updatedUser.role.defaultAuthorites)
            }
        }

        @Test
        fun `edit user password`() {
            val originalUser = user(username = "Anonymous", password = "oldPassword")
            val updatedUser = originalUser.toForm().apply { password = "newPassword"; passwordRepeated = "newPassword" }

            userFacade.editUser(userRepository.save(originalUser).toDto(), updatedUser)
            userRepository.findByUsernameIgnoreCase(originalUser.username).shouldBePresent {
                passwordEncoder.matches("newPassword", it.password).shouldBeTrue()
            }
        }

        @Test
        fun `don't edit user when password don't match`() {
            val originalUser = user(username = "Anonymous")
            val updatedUser =
                originalUser.toForm().apply { password = "newPassword"; passwordRepeated = "anotherPassword" }

            shouldThrow<FormValidationException> {
                userFacade.editUser(userRepository.save(originalUser).toDto(), updatedUser)
            }
        }
    }

    @Nested
    @DisplayName("edit current user's password")
    inner class EditCurrentUserPassword {

        @Test
        fun `edit password if user is authenticated and current password is correct`() {
            every { userService.currentUser } returns user(password = passwordEncoder.encode("oldPassword"))

            userFacade.editCurrentUserPassword(passwordEdit("oldPassword", "newPassword"))
            userRepository.findByUsernameIgnoreCase("user").shouldBePresent {
                passwordEncoder.matches("newPassword", it.password).shouldBeTrue()
            }
        }

        @Test
        fun `don't edit if user is not authenticated`() {
            every { userService.currentUser } returns null

            shouldThrow<FormValidationException> {
                userFacade.editCurrentUserPassword(passwordEdit("oldPassword", "newPassword"))
            }
        }

        @Test
        fun `don't edit if current password is incorrect`() {
            every { userService.currentUser } returns user(password = passwordEncoder.encode("oldPassword"))

            shouldThrow<FormValidationException> {
                userFacade.editCurrentUserPassword(passwordEdit("wrongPassword", "newPassword"))
            }
        }


    }
}