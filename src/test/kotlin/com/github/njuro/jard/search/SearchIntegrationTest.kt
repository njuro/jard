package com.github.njuro.jard.search

import com.github.njuro.jard.*
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardRepository
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.post.Post
import com.github.njuro.jard.post.PostRepository
import com.github.njuro.jard.post.dto.PostDto
import com.github.njuro.jard.search.dto.SearchResultsDto
import com.github.njuro.jard.thread.Thread
import com.github.njuro.jard.thread.ThreadRepository
import com.github.njuro.jard.user.UserAuthority
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@WithContainerDatabase
@Transactional
@Disabled("Search is not working - upgrade to newer version / maybe issue with docker?")
internal class SearchIntegrationTest : MockMvcTest() {

    @Autowired
    private lateinit var searchFacade: SearchFacade

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var threadRepository: ThreadRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    @PersistenceContext
    private lateinit var entityManager: EntityManager


    @Test
    @WithMockJardUser(UserAuthority.ACTUATOR_ACCESS)
    fun `rebuild indexes`() {
        mockMvc.post("${Mappings.API_ROOT_SEARCH}/rebuild-indexes") { setUp() }.andExpect { status { isOk() } }
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun `search posts`() {
        val board = saveBoard(board(label = "r"))
        val thread1 = saveThread(thread(board).apply {
            originalPost.postNumber = 1L; originalPost.body = "Something first something"
        })
        val thread2 =
            saveThread(thread(board).apply { originalPost.postNumber = 2L; originalPost.body = "Something irrelevant" })
        val reply1 = saveReply(post(thread1, postNumber = 3L, body = "OK mate"))
        val reply2 = saveReply(post(thread2, postNumber = 4L, body = "Whatever forst ist "))
        val reply3 = saveReply(post(thread2, postNumber = 5L, body = "first first first"))
        val reply4 = saveReply(post(thread1, postNumber = 6L, body = "and last one"))
        entityManager.flush()
        entityManager.clear()

        searchFacade.rebuildIndexes()


        val results = mockMvc.get("${Mappings.API_ROOT_SEARCH}?query=first") { setUp() }.andExpect { status { isOk() } }
            .andReturnConverted<SearchResultsDto<PostDto>>().shouldNotBeNull()
    }

    private fun saveBoard(board: Board): Board {
        return boardRepository.save(board)
    }

    private fun saveThread(thread: Thread): Thread {
        val post = postRepository.save(thread.originalPost)
        return threadRepository.save(thread.apply { originalPost = post })
    }

    private fun saveReply(post: Post): Post {
        return postRepository.save(post)
    }
}