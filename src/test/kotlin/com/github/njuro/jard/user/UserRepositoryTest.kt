package com.github.njuro.jard.user

import com.github.njuro.jard.database.UseMockDatabase
import com.github.njuro.jard.user
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.transaction.annotation.Transactional

@DataJpaTest
@UseMockDatabase
@Transactional
internal class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `find by username`() {
        val user = userRepository.save(user(username = "Anonymous"))

        userRepository.findByUsernameIgnoreCase(user.username.uppercase()).shouldBePresent {
            it.username shouldBe user.username
        }
    }

    @Test
    fun `find by email`() {
        val user = userRepository.save(user(email = "anon@mail.com"))

        userRepository.findByEmailIgnoreCase(user.email.uppercase()).email shouldBe user.email
    }

}