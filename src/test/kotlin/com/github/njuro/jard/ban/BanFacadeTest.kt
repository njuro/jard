package com.github.njuro.jard.ban

import com.github.njuro.jard.*
import com.github.njuro.jard.ban.dto.BanDto
import com.github.njuro.jard.common.Constants
import com.github.njuro.jard.user.User
import com.github.njuro.jard.user.UserFacade
import com.github.njuro.jard.user.UserRepository
import com.github.njuro.jard.utils.validation.FormValidationException
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.config.FixedRateTask
import org.springframework.scheduling.config.ScheduledTask
import org.springframework.scheduling.config.ScheduledTaskHolder
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.OffsetDateTime

@WithContainerDatabase
@Transactional
internal class BanFacadeTest : MapperTest() {

    @Autowired
    private lateinit var banFacade: BanFacade

    @Autowired
    private lateinit var banRepository: BanRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var scheduledTaskHolder: ScheduledTaskHolder

    @MockkBean
    private lateinit var userFacade: UserFacade

    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        user = userRepository.save(user(username = "user"))
        every { userFacade.currentUser } returns user.toDto()
    }

    @Nested
    @DisplayName("create ban")
    inner class CreateBan {
        @Test
        fun `create valid ban`() {
            val banForm = ban().toForm()

            banFacade.createBan(banForm).should {
                it.bannedBy.shouldNotBeNull()
                it.validFrom.shouldNotBeNull()
            }
        }

        @Test
        fun `create valid warning`() {
            val banForm = ban(status = BanStatus.WARNING, validTo = OffsetDateTime.now().plusDays(1)).toForm()

            banFacade.createBan(banForm).validTo.shouldBeNull()
        }

        @Test
        fun `don't create ban when no user is logged in`() {
            val banForm = ban().toForm()
            every { userFacade.currentUser } returns null

            shouldThrow<FormValidationException> {
                banFacade.createBan(banForm)
            }
        }

        @Test
        fun `don't create duplicate ban`() {
            banRepository.save(ban(ip = "127.0.0.1"))
            val banForm = ban(ip = "127.0.0.1").toForm()

            shouldThrow<FormValidationException> {
                banFacade.createBan(banForm)
            }
        }
    }

    @Test
    fun `get all bans sorted by start date`() {
        val baseDate = OffsetDateTime.now()
        val ban1 = banRepository.save(ban(validFrom = baseDate.minusDays(2)))
        val ban2 = banRepository.save(ban(validFrom = baseDate.plusDays(1)))
        val ban3 = banRepository.save(ban(validFrom = baseDate.minusDays(1)))

        banFacade.allBans.map(BanDto::getId).shouldContainExactly(ban2.id, ban3.id, ban1.id)
    }

    @Test
    fun `edit ban`() {
        val ban = banRepository.save(ban(ip = "127.0.0.1", reason = "Spam", validTo = OffsetDateTime.now().plusDays(1)))
        val updatedReason = "Offtopic"
        val updatedExpiration = OffsetDateTime.now().plusDays(10)
        val editForm = ban.toForm().apply { reason = updatedReason; validTo = updatedExpiration; ip = "127.0.0.2" }

        banFacade.editBan(ban.toDto(), editForm)
        banRepository.findById(ban.id).shouldBePresent {
            it.ip shouldBe ban.ip
            it.reason shouldBe updatedReason
            it.validTo shouldBe updatedExpiration
        }
    }

    @Nested
    @DisplayName("unban")
    inner class Unban {
        @Test
        fun `valid unban`() {
            val ban = banRepository.save(ban(status = BanStatus.ACTIVE))
            val unbanForm = ban.toUnbanForm(unbanReason = "Mistake")

            banFacade.unban(ban.toDto(), unbanForm).should {
                it.status shouldBe BanStatus.UNBANNED
                it.unbannedBy.shouldNotBeNull()
                it.unbanReason shouldBe unbanForm.reason
            }
        }

        @Test
        fun `don't unban when no user is logged in`() {
            val ban = banRepository.save(ban(status = BanStatus.ACTIVE))
            val unbanForm = ban.toUnbanForm(unbanReason = "Mistake")
            every { userFacade.currentUser } returns null

            shouldThrow<FormValidationException> {
                banFacade.unban(ban.toDto(), unbanForm)
            }
        }

        @Test
        fun `don't unban when there is no active ban on ip`() {
            val ban = banRepository.save(ban(status = BanStatus.EXPIRED))
            val unbanForm = ban.toUnbanForm(unbanReason = "Mistake")

            shouldThrow<FormValidationException> {
                banFacade.unban(ban.toDto(), unbanForm)
            }
        }

        @Test
        fun `unban expired bans`() {
            val ban = banRepository.save(ban(status = BanStatus.ACTIVE, validTo = OffsetDateTime.now().minusDays(1L)))

            banFacade.unbanExpired()
            banRepository.findById(ban.id).shouldBePresent { it.status shouldBe BanStatus.EXPIRED }
        }

        @Test
        fun `check that unban of expired bans is scheduled`() {
            val interval = Duration.parse(Constants.EXPIRED_BANS_CHECK_PERIOD).toMillis()
            scheduledTaskHolder.scheduledTasks.map(ScheduledTask::getTask)
                .filterIsInstance<FixedRateTask>()
                .any { it.interval == interval }.shouldBeTrue()
        }
    }
}
