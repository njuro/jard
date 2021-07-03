package com.github.njuro.jard.security

import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.user.UserRepository
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.optional.shouldNotBePresent
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(properties = ["app.user.root.username=root", "app.user.root.password=root"])
internal sealed class RootUserCreationTest {

    @Autowired
    protected lateinit var userRepository: UserRepository

    @TestPropertySource(properties = ["app.user.root.enabled=true"])
    @WithContainerDatabase
    internal class RootUserAllowedTest : RootUserCreationTest() {
        @Test
        fun `root user created`() {
            userRepository.findByUsernameIgnoreCase("root").shouldBePresent()
        }
    }

    @TestPropertySource(properties = ["app.user.root.enabled=false"])
    @WithContainerDatabase
    internal class RootUserNotAllowedTest : RootUserCreationTest() {
        @Test
        fun `root user not created`() {
            userRepository.findByUsernameIgnoreCase("root").shouldNotBePresent()
        }
    }
}
