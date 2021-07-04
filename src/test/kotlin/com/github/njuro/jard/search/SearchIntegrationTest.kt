package com.github.njuro.jard.search

import com.github.njuro.jard.MockMvcTest
import com.github.njuro.jard.TestDataRepository
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.WithMockJardUser
import com.github.njuro.jard.board
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.post.dto.PostDto
import com.github.njuro.jard.search.dto.SearchResultsDto
import com.github.njuro.jard.thread
import com.github.njuro.jard.user.UserAuthority
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.support.TransactionTemplate

@WithContainerDatabase
@EnableSearch
internal class SearchIntegrationTest : MockMvcTest() {

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @Autowired
    private lateinit var db: TestDataRepository

    @Test
    @WithMockJardUser(UserAuthority.ACTUATOR_ACCESS)
    fun `rebuild indexes`() {
        mockMvc.post("${Mappings.API_ROOT_SEARCH}/rebuild-indexes") { setUp() }.andExpect { status { isOk() } }
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun `search posts`() {
        transactionTemplate.executeWithoutResult {
            val board = db.insert(board(label = "r"))
            val thread = db.insert(
                thread(board).apply {
                    originalPost.body = "initial post"
                }
            )
        }

        mockMvc.get("${Mappings.API_ROOT_SEARCH}?query=initial") { setUp() }.andExpect { status { isOk() } }
            .andReturnConverted<SearchResultsDto<PostDto>>().should {
                it.resultList shouldHaveSize 1
            }
    }
}
