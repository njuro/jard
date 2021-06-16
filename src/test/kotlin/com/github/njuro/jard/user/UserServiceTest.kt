package com.github.njuro.jard.user

import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.WithMockJardUser
import com.github.njuro.jard.user
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@WithContainerDatabase
@Transactional
internal class UserServiceTest {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `save user`() {
        val user = user(username = "Anonymous")

        userService.saveUser(user).username shouldBe user.username
    }

    @Test
    fun `get all users`() {
        repeat(3) { userRepository.save(user(username = "User $it", email = "user$it@email.com")) }

        userService.allUsers shouldHaveSize 3
    }

    @Test
    fun `check that user exists`() {
        val user = userRepository.save(user(username = "Anonymous"))

        userService.doesUserExists(user.username).shouldBeTrue()
        userService.doesUserExists("other").shouldBeFalse()
    }

    @Test
    @WithMockJardUser(username = "Anonymous")
    fun `get current user`() {
        userService.currentUser.username shouldBe "Anonymous"
    }

    @Test
    @WithMockJardUser(UserAuthority.MANAGE_BOARDS, UserAuthority.MANAGE_USERS)
    fun `check if current user has authority`() {
        userService.hasCurrentUserAuthority(UserAuthority.MANAGE_BOARDS).shouldBeTrue()
        userService.hasCurrentUserAuthority(UserAuthority.MANAGE_USERS).shouldBeTrue()
        userService.hasCurrentUserAuthority(UserAuthority.MANAGE_BANS).shouldBeFalse()
    }

    @Test
    fun `delete user`() {
        val user = userRepository.save(user(username = "Anonymous"))

        userRepository.findById(user.id).shouldBePresent()
        userService.deleteUser(user)
        userRepository.findById(user.id).shouldBeEmpty()
    }
}