package com.github.njuro.jard.board

import com.github.njuro.jard.MockMvcTest
import com.github.njuro.jard.TestDataRepository
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.WithMockJardUser
import com.github.njuro.jard.attachment.AttachmentCategory
import com.github.njuro.jard.board
import com.github.njuro.jard.board.dto.BoardDto
import com.github.njuro.jard.board.dto.BoardForm
import com.github.njuro.jard.common.InputConstraints.MAX_BOARD_NAME_LENGTH
import com.github.njuro.jard.common.Mappings
import com.github.njuro.jard.randomString
import com.github.njuro.jard.thread
import com.github.njuro.jard.toForm
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
    private lateinit var db: TestDataRepository

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
        }

        @Test
        fun `don't create invalid board`() {
            val boardForm = board(label = "r", name = randomString(MAX_BOARD_NAME_LENGTH + 1)).toForm()

            createBoard(boardForm).andExpect { status { isBadRequest() } }
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
        db.insert(board(label = "r"))
        db.insert(board(label = "fit"))
        db.insert(board(label = "sp"))
        mockMvc.get(Mappings.API_ROOT_BOARDS) { setUp() }.andExpect { status { isOk() } }
            .andReturnConverted<List<BoardDto>>() shouldHaveSize 3
    }

    @Nested
    @DisplayName("get board")
    inner class GetBoard {
        private fun getBoard(label: String) = mockMvc.get("${Mappings.API_ROOT_BOARDS}/$label") { setUp() }

        @Test
        fun `get existing board`() {
            val board = db.insert(board(label = "r"))

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
        @Suppress("UNUSED_VARIABLE")
        fun `get existing board catalog`() {
            val board = db.insert(board(label = "r"))
            val thread = db.insert(thread(board))

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
            val board = db.insert(board(label = "r", name = "random"))
            val updatedName = "Updated"

            val response = editBoard(board.apply { name = updatedName }.toForm()).andExpect { status { isOk() } }
                .andReturnConverted<BoardDto>()
            response.name shouldBe updatedName
            db.select(board).shouldBePresent { it.name shouldBe updatedName }
        }

        @Test
        fun `don't edit non-existing board`() {
            editBoard(board(label = "r", name = "random").toForm()).andExpect { status { isNotFound() } }
        }

        @Test
        fun `don't edit invalid board`() {
            val board = db.insert(board(label = "r", name = "random"))

            editBoard(
                board(label = board.label, name = randomString(MAX_BOARD_NAME_LENGTH + 1))
                    .toForm()
            ).andExpect { status { isBadRequest() } }
            db.select(board).shouldBePresent { it.name shouldBe board.name }
        }
    }

    @Nested
    @DisplayName("delete board")
    @WithMockJardUser(UserAuthority.MANAGE_BOARDS)
    inner class DeleteBoard {
        private fun deleteBoard(label: String) = mockMvc.delete("${Mappings.API_ROOT_BOARDS}/$label") { setUp() }

        @Test
        fun `delete existing board`() {
            val board = db.insert(board(label = "r"))

            deleteBoard(board.label).andExpect { status { isOk() } }
            db.select(board).shouldBeEmpty()
        }
    }
}
