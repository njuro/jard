package com.github.njuro.jard.user

import com.github.njuro.jard.MapperTest
import com.github.njuro.jard.database.UseMockDatabase
import com.github.njuro.jard.toForm
import com.github.njuro.jard.user
import com.github.njuro.jard.utils.validation.FormValidationException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeBlank
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@UseMockDatabase
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
    inner class EditUser
}