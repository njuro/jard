package com.github.njuro.jard.ban

import com.github.njuro.jard.*
import com.github.njuro.jard.ban.dto.BanDto
import com.github.njuro.jard.ban.dto.BanForm
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.user.UserAuthority
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.*

@WithContainerDatabase
@Transactional
internal class BanIntegrationTest : MockMvcTest() {

    @Autowired
    private lateinit var banRepository: BanRepository

    @Nested
    @DisplayName("create ban")
    @WithMockJardUser(UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP)
    inner class CreateBan {
        private fun createBan(banForm: BanForm) = mockMvc.put(Mappings.API_ROOT_BANS) { body(banForm) }

        @Test
        fun `create valid ban`() {
            createBan(ban().toForm()).andExpect { status { isCreated() } }.andReturnConverted<BanDto>()
                .shouldNotBeNull()
        }

        @Test
        fun `don't create invalid ban`() {
            createBan(ban(ip = "123.456").toForm()).andExpect { status { isBadRequest() } }
        }
    }

    @Nested
    @DisplayName("get own ban")
    inner class GetOwnBan {
        private fun getOwnBan(ip: String) =
            mockMvc.get("${Mappings.API_ROOT_BANS}/me") { setUp(); with { it.apply { remoteAddr = ip } } }

        @Test
        fun `active ban exists`() {
            val ban = banRepository.save(ban(ip = "127.0.0.1"))
            getOwnBan(ban.ip).andExpect { status { isOk() } }.andReturnConverted<BanDto>().shouldNotBeNull()
        }

        @Test
        fun `active ban doesn't exists`() {
            getOwnBan("127.0.0.1").andExpect { status { isOk() } }.andReturn().response.contentLength shouldBe 0
        }
    }


    @Test
    @WithMockJardUser(UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP)
    fun `get all bans`() {
        (1..3).forEach { banRepository.save(ban(ip = "127.0.0.$it")) }
        mockMvc.get(Mappings.API_ROOT_BANS) { setUp() }.andExpect { status { isOk() } }
            .andReturnConverted<List<BanDto>>() shouldHaveSize 3
    }

    @Nested
    @DisplayName("edit ban")
    @WithMockJardUser(UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP)
    inner class EditBan {
        private fun editBan(id: UUID, editForm: BanForm) =
            mockMvc.post("${Mappings.API_ROOT_BANS}/$id/edit") { body(editForm) }

        @Test
        fun `edit valid ban`() {
            val ban = banRepository.save(ban())
            val editForm = ban.toForm().apply { reason = "Updated reason" }

            editBan(ban.id, editForm).andExpect { status { isOk() } }.andReturnConverted<BanDto>().shouldNotBeNull()
            banRepository.findById(ban.id).shouldBePresent { it.reason shouldBe editForm.reason }
        }

        @Test
        fun `don't edit invalid ban`() {
            val ban = banRepository.save(ban())
            val editForm = ban.toForm().apply { validTo = OffsetDateTime.now().minusDays(1) }

            editBan(ban.id, editForm).andExpect { status { isBadRequest() } }
        }
    }

    @Nested
    @DisplayName("unban")
    @WithMockJardUser(UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP)
    inner class Unban {
        private fun unban(id: UUID, unbanForm: UnbanForm) =
            mockMvc.post("${Mappings.API_ROOT_BANS}/$id/unban") { body(unbanForm) }


        @Test
        fun `valid unban`() {
            val ban = banRepository.save(ban(status = BanStatus.ACTIVE))

            unban(ban.id, ban.toUnbanForm(unbanReason = "OK")).andExpect { status { isOk() } }
                .andReturnConverted<BanDto>().shouldNotBeNull()
            banRepository.findById(ban.id).shouldBePresent { it.status shouldBe BanStatus.UNBANNED }
        }

        @Test
        fun `invalid unban`() {
            val ban = banRepository.save(ban(status = BanStatus.ACTIVE))

            unban(ban.id, ban.toUnbanForm().apply { ip = "123.456" }).andExpect { status { isBadRequest() } }
            banRepository.findById(ban.id).shouldBePresent { it.status shouldBe BanStatus.ACTIVE }
        }
    }

}