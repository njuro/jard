package com.github.njuro.jard.attachment

import ac.simons.oembed.OembedException
import com.github.njuro.jard.MapperTest
import com.github.njuro.jard.TEST_ATTACHMENT_PNG
import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.attachment.embedded.EmbedService
import com.github.njuro.jard.board
import com.github.njuro.jard.board.BoardFacade
import com.github.njuro.jard.board.dto.BoardDto
import com.github.njuro.jard.boardSettings
import com.github.njuro.jard.multipartFile
import com.github.njuro.jard.utils.validation.PropertyValidationException
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@WithContainerDatabase
@Transactional
internal class AttachmentFacadeTest : MapperTest() {

    @Autowired
    private lateinit var attachmentFacade: AttachmentFacade

    @MockkBean
    private lateinit var attachmentService: AttachmentService

    @MockkBean
    private lateinit var boardFacade: BoardFacade

    @MockkBean
    private lateinit var embedService: EmbedService

    @Nested
    @DisplayName("create attachment")
    inner class CreateAttachment {
        private val board = board(label = "r").toDto()

        @BeforeEach
        private fun setUpMocks() {
            every {
                attachmentService.saveAttachment(
                    ofType(Attachment::class),
                    ofType(MultipartFile::class)
                )
            } answers { firstArg() }
            every { boardFacade.isMimeTypeSupported(ofType(BoardDto::class), ofType(String::class)) } returns true
        }

        @Test
        fun `create valid attachment`() {
            val file = multipartFile("attachment.png", TEST_ATTACHMENT_PNG)
            attachmentFacade.createAttachment(file, board).should {
                it.filename shouldMatch Regex("\\d+\\.png")
                it.originalFilename shouldBe TEST_ATTACHMENT_PNG
                it.metadata.mimeType shouldBe MediaType.IMAGE_PNG_VALUE
            }
        }

        @Test
        fun `don't create attachment with unsupported mime type`() {
            val file = multipartFile("attachment.png", TEST_ATTACHMENT_PNG)
            every { boardFacade.isMimeTypeSupported(board, ofType(String::class)) } returns false

            shouldThrow<PropertyValidationException> {
                attachmentFacade.createAttachment(file, board)
            }
        }

        @Test
        fun `don't create attachment without extension`() {
            val file = multipartFile("attachment", TEST_ATTACHMENT_PNG, originalFilename = "attachment")

            shouldThrow<PropertyValidationException> {
                attachmentFacade.createAttachment(file, board)
            }
        }

        @Test
        fun `don't create invalid attachment`() {
            val file = multipartFile("attachment.png", TEST_ATTACHMENT_PNG)
            every { attachmentService.saveAttachment(any(), any()) } throws IOException()

            shouldThrow<PropertyValidationException> {
                attachmentFacade.createAttachment(file, board)
            }
        }
    }

    @Nested
    @DisplayName("create embedded attachment")
    inner class CreateEmbeddedAttachment {
        @BeforeEach
        fun setUpMocks() {
            every { embedService.processEmbedded(ofType(String::class), ofType(Attachment::class)) } just Runs
            every { attachmentService.saveEmbeddedAttachment(ofType(Attachment::class)) } answers { firstArg() }
        }

        @Test
        fun `create valid embedded attachment`() {
            val board =
                board(
                    label = "r",
                    settings = boardSettings(attachmentCategories = setOf(AttachmentCategory.EMBED))
                ).toDto()

            attachmentFacade.createEmbeddedAttachment("remote-url", board).should {
                it.category shouldBe AttachmentCategory.EMBED
            }
            verify { embedService.processEmbedded("remote-url", ofType(Attachment::class)) }
        }

        @Test
        fun `don't create embedded attachment when embedded content is not allowed on board`() {
            val board = board(label = "r", settings = boardSettings(attachmentCategories = emptySet())).toDto()

            shouldThrow<PropertyValidationException> {
                attachmentFacade.createEmbeddedAttachment("remote-url", board)
            }
        }

        @Test
        fun `don't create embedded attachment when oembed exception is thrown`() {
            every {
                embedService.processEmbedded(
                    ofType(String::class),
                    ofType(Attachment::class)
                )
            } throws OembedException("")
            val board = board(
                label = "r",
                settings = boardSettings(attachmentCategories = setOf(AttachmentCategory.EMBED))
            ).toDto()

            shouldThrow<PropertyValidationException> {
                attachmentFacade.createEmbeddedAttachment("remote-url", board)
            }
        }
    }
}
