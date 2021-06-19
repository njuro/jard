package com.github.njuro.jard.ban

import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.ban
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@WithContainerDatabase
@Transactional
internal class BanServiceTest {

    @Autowired
    private lateinit var banService: BanService

    @Autowired
    private lateinit var banRepository: BanRepository

    @Test
    fun `check if ip has active ban`() {
        banRepository.save(ban(ip = "127.0.0.1"))

        banService.hasActiveBan("127.0.0.1").shouldBeTrue()
        banService.hasActiveBan("127.0.0.2").shouldBeFalse()
    }
}