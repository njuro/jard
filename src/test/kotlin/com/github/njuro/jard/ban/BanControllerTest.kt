package com.github.njuro.jard.ban

import com.github.njuro.jard.*
import com.github.njuro.jard.ban.dto.BanDto
import com.github.njuro.jard.ban.dto.BanForm
import com.github.njuro.jard.common.InputConstraints.MAX_BAN_REASON_LENGTH
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.user.UserAuthority
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.*

@WithContainerDatabase
@Transactional
internal class BanControllerTest : MockMvcTest() {

    @MockkBean
    private lateinit var banFacade: BanFacade

    @Nested
    @DisplayName("create ban")
    @WithMockJardUser(UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP)
    inner class CreateBan {
        private fun createBan(banForm: BanForm) = mockMvc.post(Mappings.API_ROOT_BANS) { body(banForm) }

        @Test
        fun `create valid ban`() {
            val ban = ban()
            every { banFacade.createBan(ban.toForm()) } returns ban.toDto()

            createBan(ban.toForm()).andExpect { status { isCreated() } }.andReturnConverted<BanDto>().shouldNotBeNull()
        }

        @Test
        fun `don't create ban with invalid ip`() {
            createBan(ban(ip = "123.456").toForm()).andExpectValidationError("ip")
        }

        @Test
        fun `don't create ban with invalid reason`() {
            createBan(ban(reason = randomString(MAX_BAN_REASON_LENGTH + 1)).toForm()).andExpectValidationError("reason")
        }

        @Test
        fun `don't create ban with expiration date in the past`() {
            createBan(ban(validTo = OffsetDateTime.now().minusDays(1)).toForm()).andExpectValidationError("validTo")
        }
    }

    @Test
    fun `get own ban`() {
        val ban = ban(ip = "127.0.0.1")
        every { banFacade.getActiveBan(ban.ip) } returns ban.toDto()

        mockMvc.get("${Mappings.API_ROOT_BANS}/me") { setUp(); with { it.apply { remoteAddr = ban.ip } } }
            .andExpect { status { isOk() } }.andReturnConverted<BanDto>().shouldNotBeNull()
    }

    @Test
    @WithMockJardUser(UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP)
    fun `get all bans`() {
        every { banFacade.allBans } returns listOf(ban().toDto(), ban().toDto(), ban().toDto())

        mockMvc.get(Mappings.API_ROOT_BANS) { setUp() }.andExpect { status { isOk() } }
            .andReturnConverted<List<BanDto>>() shouldHaveSize 3
    }

    @Nested
    @DisplayName("edit ban")
    @WithMockJardUser(UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP)
    inner class EditBan {
        private fun editBan(editForm: BanForm) =
            mockMvc.put("${Mappings.API_ROOT_BANS}/${UUID.randomUUID()}") { body(editForm) }

        @BeforeEach
        fun setUp() {
            every { banFacade.resolveBan(ofType(UUID::class)) } returns ban().toDto()
        }

        @Test
        fun `edit valid ban`() {
            val ban = ban()
            every { banFacade.editBan(ofType(BanDto::class), ofType(BanForm::class)) } returns ban.toDto()

            editBan(ban.toForm()).andExpect { status { isOk() } }.andReturnConverted<BanDto>().shouldNotBeNull()
        }

        @Test
        fun `don't edit non-existing ban`() {
            every { banFacade.resolveBan(ofType(UUID::class)) } throws BanNotFoundException()

            editBan(ban().toForm()).andExpect { status { isNotFound() } }
        }

        @Test
        fun `don't edit invalid ban`() {
            editBan(ban().toForm().apply { ip = null }).andExpect { status { isBadRequest() } }
        }
    }

    @Nested
    @DisplayName("unban")
    @WithMockJardUser(UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP)
    inner class Unban {
        private fun unban(unbanForm: UnbanForm) =
            mockMvc.put("${Mappings.API_ROOT_BANS}/${UUID.randomUUID()}/unban") { body(unbanForm) }

        @BeforeEach
        fun setUp() {
            every { banFacade.resolveBan(ofType(UUID::class)) } returns ban().toDto()
        }

        @Test
        fun `valid unban`() {
            val ban = ban()
            every { banFacade.unban(ofType(BanDto::class), ofType(UnbanForm::class)) } returns ban.toDto()

            unban(ban.toUnbanForm()).andExpect { status { isOk() } }.andReturnConverted<BanDto>().shouldNotBeNull()
        }

        @Test
        fun `non-existing ban`() {
            every { banFacade.resolveBan(ofType(UUID::class)) } throws BanNotFoundException()

            unban(ban().toUnbanForm()).andExpect { status { isNotFound() } }
        }


        @Test
        fun `unban with invalid ip`() {
            unban(ban(ip = "123.456").toUnbanForm()).andExpectValidationError("ip")
        }

        @Test
        fun `unban with invalid reason`() {
            unban(ban().toUnbanForm(unbanReason = randomString(MAX_BAN_REASON_LENGTH + 1))).andExpectValidationError("reason")
        }
    }
}