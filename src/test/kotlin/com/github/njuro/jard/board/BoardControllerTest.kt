package com.github.njuro.jard.board

import com.github.njuro.jard.*
import com.github.njuro.jard.attachment.AttachmentCategory
import com.github.njuro.jard.board.dto.BoardDto
import com.github.njuro.jard.board.dto.BoardForm
import com.github.njuro.jard.common.Constants.MAX_THREADS_PER_PAGE
import com.github.njuro.jard.common.InputConstraints.*
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.common.WithMockUserAuthorities
import com.github.njuro.jard.user.UserAuthority
import com.ninjasquad.springmockk.SpykBean
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.slot
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Pageable
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.io.IOException


@UseMockDatabase
internal class BoardControllerTest : MockMvcTest() {

    @SpykBean
    private lateinit var boardFacade: BoardFacade

    @Nested
    @DisplayName("create board")
    @WithMockUserAuthorities(UserAuthority.MANAGE_BOARDS)
    inner class CreateBoard {
        private fun createBoard(board: Board) =
            mockMvc.put(Mappings.API_ROOT_BOARDS) { body(board.toForm()) }

        private fun expectValidationError(board: Board, field: String) =
            createBoard(board).andExpectValidationError(field)

        @Test
        fun `create valid board`() {
            val board = board(label = "r")
            every { boardFacade.createBoard(ofType(BoardForm::class)) } returns board.toDto()

            val response = createBoard(board).andExpect { status { isOk() } }.andReturnConverted<BoardDto>()
            response shouldBe board.toDto()
        }

        @Test
        fun `don't create board with invalid label`() {
            expectValidationError(board(label = ""), "label")
            expectValidationError(board(label = randomString(MAX_BOARD_LABEL_LENGTH + 1)), "label")
        }

        @Test
        fun `don't create board with invalid name`() {
            expectValidationError(board(label = "r", name = ""), "name")
            expectValidationError(
                board(label = "r", name = randomString(MAX_BOARD_NAME_LENGTH + 1)),
                "name"
            )
        }

        @Test
        fun `don't create board with invalid thread limit`() {
            expectValidationError(
                board(label = "r", settings = boardSettings(threadLimit = 0)),
                "boardSettingsForm.threadLimit"
            )
            expectValidationError(
                board(label = "r", settings = boardSettings(threadLimit = MAX_THREAD_LIMIT + 1)),
                "boardSettingsForm.threadLimit"
            )
        }

        @Test
        fun `don't create board with invalid bump limit`() {
            expectValidationError(
                board(label = "r", settings = boardSettings(bumpLimit = 0)),
                "boardSettingsForm.bumpLimit"
            )
            expectValidationError(
                board(label = "r", settings = boardSettings(bumpLimit = MAX_BUMP_LIMIT + 1)),
                "boardSettingsForm.bumpLimit"
            )
        }

        @Test
        fun `don't create board with invalid default poster name`() {
            expectValidationError(
                board(
                    label = "r", settings = boardSettings(
                        defaultPosterName = randomString(
                            MAX_NAME_LENGTH + 1
                        )
                    )
                ), "boardSettingsForm.defaultPosterName"
            )
        }

    }

    @Test
    @WithMockUserAuthorities(UserAuthority.MANAGE_BOARDS)
    fun `get attachment categories`() {
        mockMvc.get("${Mappings.API_ROOT_BOARDS}/attachment-categories") { setUp() }.andExpect { status { isOk() } }
            .andReturnConverted<Set<AttachmentCategory.Preview>>().shouldNotBeEmpty()
    }

    @Test
    fun `get all boards`() {
        every { boardFacade.allBoards } returns listOf(board(label = "r").toDto(), board(label = "fit").toDto())

        mockMvc.get(Mappings.API_ROOT_BOARDS) { setUp() }.andExpect {
            status { isOk() }
            jsonPath("$[*].threads") { doesNotExist() }
        }.andReturnConverted<List<BoardDto>>() shouldHaveSize 2
    }

    @Nested
    @DisplayName("get board")
    inner class GetBoard {
        private fun getBoard(label: String, page: Int? = null) =
            mockMvc.get("${Mappings.API_ROOT_BOARDS}/$label${if (page == null) "" else "?page=${page}"}") { setUp() }

        @Test
        fun `get existing board`() {
            val board = board(label = "r")
            val thread = thread(board)
            val pagination = slot<Pageable>()
            every { boardFacade.resolveBoard(board.label) } returns board.toDto()
            every { boardFacade.getBoard(board.toDto(), capture(pagination)) } returns board.toDto()
                .apply { threads = listOf(thread.toDto()) }

            val response = getBoard(board.label).andExpect {
                status { isOk() }
                jsonPath("$.threads") { exists() }
                jsonPath("$.threads[*].board") { doesNotExist() }
                jsonPath("$.threads[*].originalPost") { exists() }
                jsonPath("$.threads[*].originalPost.ip") { doesNotExist() }
            }.andReturnConverted<BoardDto>()
            response.label shouldBe board.label
            response.threads shouldHaveSize 1
            with(pagination.captured) {
                pageNumber shouldBe 0
                pageSize shouldBe MAX_THREADS_PER_PAGE
            }
        }

        @Test
        fun `get board with thread pagination`() {
            val board = board(label = "r")
            val thread = thread(board)
            val pagination = slot<Pageable>()
            every { boardFacade.resolveBoard(board.label) } returns board.toDto()
            every { boardFacade.getBoard(board.toDto(), capture(pagination)) } returns board.toDto()
                .apply { threads = listOf(thread.toDto()) }

            getBoard(board.label, page = 3).andExpect { status { isOk() } }
            pagination.captured.pageNumber shouldBe 2
        }


        @Test
        fun `don't get non-existing board`() {
            val label = "xxx"
            every { boardFacade.resolveBoard(label) } throws BoardNotFoundException()
            getBoard(label).andExpect { status { isNotFound() } }
        }
    }

    @Test
    fun `get board catalog`() {
        val board = board(label = "r")
        val threads = listOf(thread(board).toDto(), thread(board).toDto())
        every { boardFacade.resolveBoard(board.label) } returns board.toDto()
        every { boardFacade.getBoardCatalog(board.toDto()) } returns board.toDto().apply { this.threads = threads }

        val response = mockMvc.get("${Mappings.API_ROOT_BOARDS}/${board.label}/catalog") { setUp() }.andExpect {
            status { isOk() }
            jsonPath("$.threads") { exists() }
            jsonPath("$.threads[*].board") { doesNotExist() }
            jsonPath("$.threads[*].originalPost") { exists() }
            jsonPath("$.threads[*].originalPost.ip") { doesNotExist() }
        }.andReturnConverted<BoardDto>()
        response.label shouldBe board.label
        response.threads shouldHaveSize 2

    }

    @Nested
    @DisplayName("edit board")
    @WithMockUserAuthorities(UserAuthority.MANAGE_BOARDS)
    inner class EditBoard {
        private fun editBoard(boardForm: BoardForm) =
            mockMvc.post("${Mappings.API_ROOT_BOARDS}/${boardForm.label}/edit") { setUp(); body(boardForm) }

        @Test
        fun `edit existing board`() {
            val board = board(label = "r")
            every { boardFacade.resolveBoard(board.label) } returns board.toDto()
            every { boardFacade.editBoard(ofType(BoardDto::class), ofType(BoardForm::class)) } returns board.toDto()

            val updated = editBoard(board.toForm()).andExpect { status { isOk() } }.andReturnConverted<BoardDto>()
            updated.label shouldBe board.label
        }

        @Test
        @Disabled
        fun `don't edit non-existing board`() {
            // TODO
        }

        @Test
        fun `don't edit board with invalid form`() {
            val board = board(label = "r", name = randomString(MAX_BOARD_NAME_LENGTH + 1))
            every { boardFacade.resolveBoard(board.label) } returns board.toDto()
            every { boardFacade.editBoard(ofType(BoardDto::class), ofType(BoardForm::class)) } returns board.toDto()

            editBoard(board.toForm()).andExpect { status { isBadRequest() } }
        }
    }


    @Nested
    @DisplayName("delete board")
    @WithMockUserAuthorities(UserAuthority.MANAGE_BOARDS)
    inner class DeleteBoard {
        private fun deleteBoard(label: String) = mockMvc.delete("${Mappings.API_ROOT_BOARDS}/$label") { setUp() }

        @Test
        fun `delete existing board`() {
            val board = board(label = "r")
            every { boardFacade.resolveBoard(board.label) } returns board.toDto()
            every { boardFacade.deleteBoard(ofType(BoardDto::class)) } just Runs

            deleteBoard(board.label).andExpect { status { isOk() } }
        }

        @Test
        fun `don't delete board when IO exception is thrown`() {
            val board = board(label = "r")
            every { boardFacade.resolveBoard(board.label) } returns board.toDto()
            every { boardFacade.deleteBoard(ofType(BoardDto::class)) } throws IOException()

            deleteBoard(board.label).andExpect { status { isBadRequest() } }
        }

    }


}