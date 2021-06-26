package com.github.njuro.jard.search

import com.github.njuro.jard.*
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardRepository
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.post.PostRepository
import com.github.njuro.jard.post.dto.PostDto
import com.github.njuro.jard.search.dto.SearchResultsDto
import com.github.njuro.jard.thread.Thread
import com.github.njuro.jard.thread.ThreadRepository
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
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var threadRepository: ThreadRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate


    @Test
    @WithMockJardUser(UserAuthority.ACTUATOR_ACCESS)
    fun `rebuild indexes`() {
        mockMvc.post("${Mappings.API_ROOT_SEARCH}/rebuild-indexes") { setUp() }.andExpect { status { isOk() } }
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun `search posts`() {
        transactionTemplate.executeWithoutResult {
            val board = saveBoard(board(label = "r"))
            val thread = saveThread(thread(board).apply {
                originalPost.body = "initial post"
            })
        }

        mockMvc.get("${Mappings.API_ROOT_SEARCH}?query=initial") { setUp() }.andExpect { status { isOk() } }
            .andReturnConverted<SearchResultsDto<PostDto>>().should {
                it.resultList shouldHaveSize 1
            }
    }

    private fun saveBoard(board: Board): Board {
        return boardRepository.save(board)
    }

    private fun saveThread(thread: Thread): Thread {
        val post = postRepository.save(thread.originalPost)
        return threadRepository.save(thread.apply { originalPost = post })
    }
}