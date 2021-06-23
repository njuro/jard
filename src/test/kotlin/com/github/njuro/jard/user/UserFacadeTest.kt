package com.github.njuro.jard.user

import com.github.njuro.jard.MapperTest
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.toForm
import com.github.njuro.jard.user
import com.github.njuro.jard.utils.validation.FormValidationException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeBlank
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@WithContainerDatabase
@Transactional
internal class UserFacadeTest : MapperTest() {

    @Autowired
    private lateinit var userFacade: UserFacade

    @Autowired
    private lateinit var userRepository: UserRepository

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
            val originalUser = user(username = "Anonymous")
            val updatedUser = originalUser.toForm().apply { password = "newPassword"; passwordRepeated = "newPassword" }

            userFacade.editUser(userRepository.save(originalUser).toDto(), updatedUser)
            userRepository.findByUsernameIgnoreCase(originalUser.username).shouldBePresent {
                it.password.shouldNotBeBlank()
                it.password shouldNotBe updatedUser.password
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
}