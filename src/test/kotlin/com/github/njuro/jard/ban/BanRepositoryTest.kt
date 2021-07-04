@file:Suppress("UNUSED_VARIABLE")

package com.github.njuro.jard.ban

import com.github.njuro.jard.TestDataRepository
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.WithTestDataRepository
import com.github.njuro.jard.ban
import com.github.njuro.jard.user
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.optional.shouldNotBePresent
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@DataJpaTest
@WithTestDataRepository
@WithContainerDatabase
@Transactional
internal class BanRepositoryTest {

    @Autowired
    private lateinit var banRepository: BanRepository

    @Autowired
    private lateinit var db: TestDataRepository

    @Test
    fun `find by ip`() {
        val ban1 = banRepository.save(ban(ip = "127.0.0.1"))
        val ban2 = banRepository.save(ban(ip = "127.0.0.1"))

        banRepository.findByIp(ban1.ip).map(Ban::getId).shouldContainExactlyInAnyOrder(ban1.id, ban2.id)
    }

    @Test
    fun `find by status and expiration date before given date`() {
        val baseDate = OffsetDateTime.now()
        val ban1 = banRepository.save(ban(status = BanStatus.ACTIVE, validTo = baseDate.minusDays(1L)))
        val ban2 = banRepository.save(ban(status = BanStatus.ACTIVE, validTo = baseDate.plusDays(1L)))
        val ban3 = banRepository.save(ban(status = BanStatus.ACTIVE, validTo = baseDate.minusDays(2L)))
        val ban4 = banRepository.save(ban(status = BanStatus.UNBANNED, validTo = baseDate.minusDays(2L)))

        banRepository.findByStatusAndValidToBefore(BanStatus.ACTIVE, baseDate).map(Ban::getId)
            .shouldContainExactlyInAnyOrder(ban1.id, ban3.id)
    }

    @Test
    fun `find by ip and status`() {
        val ban = banRepository.save(ban(status = BanStatus.WARNING, ip = "127.0.0.1"))

        banRepository.findByIpAndStatus(ban.ip, ban.status).shouldBePresent()
        banRepository.findByIpAndStatus(ban.ip, BanStatus.UNBANNED).shouldNotBePresent()
    }

    @Test
    fun `find by banned by`() {
        val user1 = db.insert(user(username = "First", email = "first@mail.com"))
        val user2 = db.insert(user(username = "Second", email = "second@mail.com"))
        val ban1 = banRepository.save(ban(bannedBy = user1))
        val ban2 = banRepository.save(ban(bannedBy = user1))
        val ban3 = banRepository.save(ban(bannedBy = user2))

        banRepository.findByBannedBy(user1).map(Ban::getId).shouldContainExactlyInAnyOrder(ban1.id, ban2.id)
    }
}
