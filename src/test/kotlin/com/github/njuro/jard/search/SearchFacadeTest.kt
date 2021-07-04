package com.github.njuro.jard.search

import com.github.njuro.jard.TestDataRepository
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.board
import com.github.njuro.jard.common.Constants
import com.github.njuro.jard.post
import com.github.njuro.jard.post.dto.PostDto
import com.github.njuro.jard.thread
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest
@WithContainerDatabase
@EnableSearch
internal class SearchFacadeTest {

    @Autowired
    private lateinit var searchFacade: SearchFacade

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @Autowired
    private lateinit var db: TestDataRepository

    @Test
    fun `rebuild indexes`() {
        searchFacade.rebuildIndexes().shouldBeTrue()
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun `search posts`() {
        transactionTemplate.executeWithoutResult {
            val board = db.insert(board(label = "r"))
            val thread1 = db.insert(
                thread(board).apply {
                    originalPost.postNumber = 1L; originalPost.body = "This is the very first post"
                },
                threadNumber = 1L
            )
            val thread2 = db.insert(
                thread(board).apply {
                    originalPost.postNumber = 2L; originalPost.body = "Second post"
                },
                threadNumber = 2L
            )
            val reply1 = db.insert(post(thread1, postNumber = 3L, body = "Unrelated reply"))
            val reply2 = db.insert(post(thread2, postNumber = 4L, body = "Forst (misspelled)"))
            val reply3 = db.insert(post(thread2, postNumber = 5L, body = "FIRST FIRST FIRST (upper-case)"))
            val reply4 = db.insert(post(thread1, postNumber = 6L, body = "Another unrelated reply"))
        }

        searchFacade.searchPosts("first~1").should {
            it.totalResultsCount shouldBe 3
            it.resultsCount shouldBe 3
            it.resultList.map(PostDto::getPostNumber).shouldContainExactlyInAnyOrder(1L, 4L, 5L)
            it.resultList.map(PostDto::getBody)
                .all { body -> body.contains(Constants.SEARCH_RESULT_HIGHLIGHT_START) && body.contains(Constants.SEARCH_RESULT_HIGHLIGHT_END) }
                .shouldBeTrue()
        }
    }
}
