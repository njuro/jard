package com.github.njuro.jard.post.decorators

import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.board
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardRepository
import com.github.njuro.jard.common.Constants
import com.github.njuro.jard.post
import com.github.njuro.jard.post.Post
import com.github.njuro.jard.post.PostRepository
import com.github.njuro.jard.thread
import com.github.njuro.jard.thread.Thread
import com.github.njuro.jard.thread.ThreadRepository
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@DataJpaTest(
    includeFilters = [
        ComponentScan.Filter(Service::class), ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            value = [CrosslinkDecorator::class]
        )
    ]
)
@WithContainerDatabase
@Transactional
internal class CrosslinkDecoratorTest : PostDecoratorTest() {
    @Autowired
    private lateinit var decorator: CrosslinkDecorator

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var threadRepository: ThreadRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    private lateinit var postRandom: Post

    private lateinit var postFitness: Post

    override fun initDecorator() = decorator

    @BeforeEach
    @Suppress("UNUSED_VARIABLE")
    fun `initialize data`() {
        val boardRandom = saveBoard(board(label = "r"))
        val boardFitness = saveBoard(board(label = "fit"))
        val threadRandom1 = saveThread(thread(boardRandom).apply { originalPost.postNumber = 1L })
        val threadRandom2 = saveThread(thread(boardRandom).apply { originalPost.postNumber = 2L })
        val threadRandom3 = saveThread(thread(boardRandom).apply { originalPost.postNumber = 3L })
        val threadFitness1 = saveThread(thread(boardFitness).apply { originalPost.postNumber = 1L })
        val replyRandom3 = saveReply(post(threadRandom3, postNumber = 4))

        postRandom = threadRandom1.originalPost
        postFitness = threadFitness1.originalPost
    }

    @Test
    fun `valid cross thread link`() {
        decorate(postRandom, ">>1").shouldContainInOrder("/boards/r/thread/1", Constants.CROSSLINK_CLASS_VALID)
    }

    @Test
    fun `invalid cross thread link`() {
        decorate(postRandom, ">>42").shouldContain(Constants.CROSSLINK_CLASS_INVALID)
    }

    @Test
    fun `multiple cross thread links`() {
        decorate(postRandom, "To different thread >>3 more text\n >>1 and to OP").shouldContainInOrder(
            "/boards/r/thread/3#3",
            Constants.CROSSLINK_CLASS_VALID,
            Constants.CROSSLINK_DIFF_THREAD,
            "/boards/r/thread/1#1",
            Constants.CROSSLINK_CLASS_VALID,
            Constants.CROSSLINK_OP
        )
    }

    @Test
    fun `valid cross board link`() {
        decorate(postRandom, "Some text  >>>/fit/1").should {
            it.shouldContain(Constants.CROSSLINK_CLASS_VALID)
            it.shouldContain("/boards/fit/thread/1")
        }

        decorate(postFitness, ">>>/r/4 some text").should {
            it.shouldContain(Constants.CROSSLINK_CLASS_VALID)
            it.shouldContain("/boards/r/thread/3#4")
        }
    }

    @Test
    fun `invalid cross board link`() {
        decorate(postFitness, "This points to >>>/r/42").shouldContain(Constants.CROSSLINK_CLASS_INVALID)
        decorate(postRandom, "And this to >>>/a/1").shouldContain(Constants.CROSSLINK_CLASS_INVALID)
    }

    @Test
    fun `valid pure cross board link`() {
        decorate(postFitness, "This is pure valid link to >>>/fit/  ").should {
            it.shouldContain(Constants.CROSSLINK_CLASS_VALID)
            it.shouldContain("/boards/fit")
        }
    }

    @Test
    fun `invalid pure cross board link`() {
        decorate(postRandom, "And this is invalid link to >>>/q/").shouldContain(Constants.CROSSLINK_CLASS_INVALID)
    }

    @ParameterizedTest
    @ValueSource(strings = [">>", " >>>", ">> 1", ">>/r/1", ">>> /fit/1", ">>abc", ">>>abc", ">>>//123", ">>/fit/"])
    fun `invalid crosslink pattern`(input: String) {
        decorate(postRandom, input).should {
            it.shouldNotContain(Constants.CROSSLINK_CLASS_VALID)
            it.shouldNotContain(Constants.CROSSLINK_CLASS_INVALID)
        }
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
