package com.github.njuro.jard.board

import com.github.njuro.jard.*
import com.github.njuro.jard.attachment.AttachmentCategory
import com.github.njuro.jard.board.dto.BoardDto
import com.github.njuro.jard.board.dto.BoardForm
import com.github.njuro.jard.common.InputConstraints.MAX_BOARD_NAME_LENGTH
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.post.PostRepository
import com.github.njuro.jard.thread.ThreadRepository
import com.github.njuro.jard.user.UserAuthority
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional

@WithContainerDatabase
@Transactional
internal class BoardIntegrationTest : MockMvcTest() {

    @Autowired
    private lateinit var boardRepository: BoardRepository

    @Autowired
    private lateinit var threadRepository: ThreadRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    @Nested
    @DisplayName("create board")
    @WithMockJardUser(UserAuthority.MANAGE_BOARDS)
    inner class CreateBoard {
        private fun createBoard(boardForm: BoardForm) =
            mockMvc.post(Mappings.API_ROOT_BOARDS) { setUp(); body(boardForm) }

        @Test
        fun `create valid board`() {
            val boardForm = board(label = "r").toForm()

            val response = createBoard(boardForm).andExpect { status { isCreated() } }.andReturnConverted<BoardDto>()
            response.label shouldBe boardForm.label
            boardRepository.findByLabel(boardForm.label).shouldBePresent()
        }

        @Test
        fun `don't create invalid board`() {
            val boardForm = board(label = "r", name = randomString(MAX_BOARD_NAME_LENGTH + 1)).toForm()

            createBoard(boardForm).andExpect { status { isBadRequest() } }
            boardRepository.findByLabel(boardForm.label).shouldBeEmpty()
        }
    }

    @Test
    @WithMockJardUser(UserAuthority.MANAGE_BOARDS)
    fun `get attachment categories`() {
        mockMvc.get("${Mappings.API_ROOT_BOARDS}/attachment-categories") { setUp() }.andExpect { status { isOk() } }
            .andReturnConverted<Set<AttachmentCategory.Preview>>().shouldNotBeEmpty()
    }

    @Test
    fun `get all boards`() {
        boardRepository.saveAll(listOf(board(label = "r"), board(label = "fit"), board(label = "sp")))
        mockMvc.get(Mappings.API_ROOT_BOARDS) { setUp() }.andExpect { status { isOk() } }
            .andReturnConverted<List<BoardDto>>() shouldHaveSize 3
    }

    @Nested
    @DisplayName("get board")
    inner class GetBoard {
        private fun getBoard(label: String) = mockMvc.get("${Mappings.API_ROOT_BOARDS}/$label") { setUp() }

        @Test
        fun `get existing board`() {
            val board = boardRepository.save(board(label = "r"))

            getBoard(board.label).andExpect { status { isOk() } }
                .andReturnConverted<BoardDto>().label shouldBe board.label
        }

        @Test
        fun `don't get non-existing board`() {
            getBoard("xxx").andExpect { status { isNotFound() } }
        }
    }

    @Nested
    @DisplayName("get board catalog")
    inner class GetBoardCatalog {
        private fun getBoardCatalog(label: String) =
            mockMvc.get("${Mappings.API_ROOT_BOARDS}/$label/catalog") { setUp() }

        @Test
        fun `get existing board catalog`() {
            val board = boardRepository.save(board(label = "r"))
            val thread = thread(board)
            val post = postRepository.save(thread.originalPost)
            threadRepository.save(thread.apply { originalPost = post })

            val response = getBoardCatalog(board.label).andExpect { status { isOk() } }.andReturnConverted<BoardDto>()
            response.label shouldBe board.label
            response.threads shouldHaveSize 1
        }

        @Test
        fun `don't get non-existing board`() {
            getBoardCatalog("xxx").andExpect { status { isNotFound() } }
        }
    }

    @Nested
    @DisplayName("edit board")
    @WithMockJardUser(UserAuthority.MANAGE_BOARDS)
    inner class EditBoard {
        private fun editBoard(boardForm: BoardForm) =
            mockMvc.put("${Mappings.API_ROOT_BOARDS}/${boardForm.label}") { setUp(); body(boardForm) }

        @Test
        fun `edit valid board`() {
            val board = board(label = "r", name = "random")
            boardRepository.save(board)

            val updatedName = "Updated"
            val response = editBoard(board.apply { name = updatedName }.toForm()).andExpect { status { isOk() } }
                .andReturnConverted<BoardDto>()
            response.name shouldBe updatedName
            boardRepository.findByLabel(board.label).shouldBePresent { it.name shouldBe updatedName }
        }

        @Test
        fun `don't edit non-existing board`() {
            editBoard(board(label = "r", name = "random").toForm()).andExpect { status { isNotFound() } }
        }

        @Test
        fun `don't edit invalid board`() {
            val board = board(label = "r", name = "random")
            boardRepository.save(board)

            editBoard(
                board(label = board.label, name = randomString(MAX_BOARD_NAME_LENGTH + 1))
                    .toForm()
            ).andExpect { status { isBadRequest() } }
            boardRepository.findByLabel(board.label).shouldBePresent { it.name shouldBe board.name }
        }
    }

    @Nested
    @DisplayName("delete board")
    @WithMockJardUser(UserAuthority.MANAGE_BOARDS)
    inner class DeleteBoard {
        private fun deleteBoard(label: String) = mockMvc.delete("${Mappings.API_ROOT_BOARDS}/$label") { setUp() }

        @Test
        fun `delete existing board`() {
            val board = boardRepository.save(board(label = "r"))

            deleteBoard(board.label).andExpect { status { isOk() } }
            boardRepository.findByLabel(board.label).shouldBeEmpty()
        }
    }


}