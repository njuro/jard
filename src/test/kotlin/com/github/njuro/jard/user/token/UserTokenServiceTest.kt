package com.github.njuro.jard.user.token

import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.common.Constants
import com.github.njuro.jard.user
import com.github.njuro.jard.user.UserRepository
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.scheduling.config.FixedRateTask
import org.springframework.scheduling.config.ScheduledTask
import org.springframework.scheduling.config.ScheduledTaskHolder
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@SpringBootTest
@WithContainerDatabase
@Transactional
internal class UserTokenServiceTest {

    @Autowired
    private lateinit var userTokenService: UserTokenService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var scheduledTaskHolder: ScheduledTaskHolder

    @Test
    fun `generate user token`() {
        val user = userRepository.save(user(username = "user"))

        userTokenService.generateToken(user, UserTokenType.PASSWORD_RECOVERY).should {
            it.value.shouldNotBeBlank()
            it.type shouldBe UserTokenType.PASSWORD_RECOVERY
            it.issuedAt.shouldNotBeNull()
            it.expirationAt.shouldNotBeNull()
            it.expirationAt shouldBeAfter it.issuedAt
            it.user.username shouldBe user.username
        }
    }

    @Test
    fun `removal of expired tokens is scheduled`() {
        val interval = Duration.parse(Constants.EXPIRED_USER_TOKENS_CHECK_PERIOD).toMillis()
        scheduledTaskHolder.scheduledTasks.map(ScheduledTask::getTask)
            .filterIsInstance<FixedRateTask>()
            .any { it.interval == interval }.shouldBeTrue()
    }
}
