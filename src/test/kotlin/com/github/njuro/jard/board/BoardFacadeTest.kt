package com.github.njuro.jard.board

import com.github.njuro.jard.MapperTest
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.attachment.AttachmentCategory
import com.github.njuro.jard.board
import com.github.njuro.jard.boardSettings
import com.github.njuro.jard.post
import com.github.njuro.jard.post.PostFacade
import com.github.njuro.jard.thread
import com.github.njuro.jard.thread.ThreadFacade
import com.github.njuro.jard.toForm
import com.github.njuro.jard.utils.validation.PropertyValidationException
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType

@WithContainerDatabase
internal class BoardFacadeTest : MapperTest() {

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @MockkBean
    private lateinit var threadFacade: ThreadFacade

    @MockkBean
    private lateinit var postFacade: PostFacade

    @Autowired
    private lateinit var boardFacade: BoardFacade

    @Test
    fun `create board`() {
        val boardForm = board(label = "r").toForm()

        val created = boardFacade.createBoard(boardForm)
        created.label shouldBe boardForm.label
    }

    @Test
    fun `don't create board which already exists`() {
        val boardForm = board(label = "r").toForm()

        boardFacade.createBoard(boardForm)
        shouldThrow<PropertyValidationException> {
            boardFacade.createBoard(boardForm)
        }
    }

    @Test
    fun `get board with threads`() {
        val board = boardRepository.save(board(label = "r"))
        val thread = thread(board)
        val replies = listOf(post(thread).toDto(), post(thread).toDto())

        every {
            threadFacade.getThreadsFromBoard(
                board.toDto(),
                ofType(Pageable::class)
            )
        } returns listOf(thread.toDto())
        every { postFacade.getLatestRepliesForThread(thread.toDto()) } returns replies

        val actual = boardFacade.getBoard(board.toDto(), Pageable.unpaged())
        actual.threads shouldHaveSize 1
        actual.threads.first().replies shouldHaveSize 2
    }

    @Test
    fun `get board catalog`() {
        val board = board(label = "r")
        every { threadFacade.getAllThreadsFromBoard(board.toDto()) } returns listOf(
            thread(board).toDto(),
            thread(board).toDto()
        )

        boardFacade.getBoardCatalog(board.toDto()).threads shouldHaveSize 2
    }

    @Test
    fun `get previews of attachment categories`() {
        boardFacade.attachmentCategories.shouldNotBeEmpty()
    }

    @Test
    fun `determine if mime type of attachment is allowed on board`() {
        val board = boardRepository.save(
            board(
                label = "r",
                settings = boardSettings(attachmentCategories = setOf(AttachmentCategory.IMAGE))
            )
        ).toDto()

        boardFacade.isMimeTypeSupported(board, MediaType.IMAGE_JPEG_VALUE) shouldBe true
        boardFacade.isMimeTypeSupported(board, MediaType.IMAGE_PNG_VALUE) shouldBe true
        boardFacade.isMimeTypeSupported(board, MediaType.APPLICATION_PDF_VALUE) shouldBe false
        boardFacade.isMimeTypeSupported(board, null) shouldBe false
    }

    @Test
    fun `edit board`() {
        val original =
            boardRepository.save(board(label = "r", postCounter = 1L, settings = boardSettings(nsfw = false)))
        val editForm =
            original.toForm().apply { name = "Updated name"; boardSettingsForm = boardSettings(nsfw = true).toForm() }

        val updated = boardFacade.editBoard(original.toDto(), editForm)
        updated.label shouldBe original.label
        updated.name shouldBe editForm.name
        updated.settings.isNsfw shouldBe true
    }

    @Test
    fun `delete board`() {
        val board = boardRepository.save(board(label = "r"))

        boardRepository.findByLabel(board.label).shouldBePresent()
        boardFacade.deleteBoard(board.toDto())
        boardRepository.findByLabel(board.label).shouldBeEmpty()
    }
}
