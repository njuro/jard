package com.github.njuro.jard.attachment

import com.github.njuro.jard.*
import com.github.njuro.jard.attachment.storage.RemoteStorageService
import com.github.njuro.jard.common.Constants.DEFAULT_THUMBNAIL_EXTENSION
import com.github.njuro.jard.common.Constants.THUMBNAIL_FOLDER_NAME
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.file.*
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.optional.shouldNotBePresent
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.io.File

@SpringBootTest
@WithContainerDatabase
@Transactional
internal class AttachmentServiceTest {

    @Autowired
    private lateinit var attachmentService: AttachmentService

    @Autowired
    private lateinit var attachmentRepository: AttachmentRepository

    @MockkBean
    private lateinit var remoteStorageService: RemoteStorageService

    @BeforeEach
    @AfterEach
    fun `delete test folder`() {
        val testFolder = attachmentPath(TEST_FOLDER_NAME).toFile()
        if (testFolder.exists()) {
            testFolder.deleteRecursively().shouldBeTrue()
        }
    }

    @Nested
    @DisplayName("save attachment")
    inner class SaveAttachment {
        private fun getRemoteUrl(folder: String, filename: String) = "https://remote-storage.com/$folder-$filename"

        @BeforeEach
        fun setUpMocks() {
            every {
                remoteStorageService.uploadFile(
                    ofType(String::class),
                    ofType(String::class),
                    ofType(File::class)
                )
            } answers { getRemoteUrl(firstArg(), secondArg()) }
        }

        @Test
        fun `save image attachment with thumbnail`() {
            val file = multipartFile("attachment.png", TEST_ATTACHMENT_PNG)
            val attachment =
                attachment(
                    category = AttachmentCategory.IMAGE,
                    filename = file.name,
                    folder = TEST_FOLDER_NAME,
                    metadata = metadata(mimeType = "image/png")
                )

            attachmentService.saveAttachment(attachment, file).should {
                it.metadata.shouldNotBeNull()
                it.remoteStorageUrl shouldBe getRemoteUrl(attachment.folder, attachment.filename)
                it.file.shouldMatchFile("attachment", "png")
                it.remoteStorageThumbnailUrl shouldBe getRemoteUrl(
                    "$TEST_FOLDER_NAME/$THUMBNAIL_FOLDER_NAME",
                    attachment.filename
                )
                it.thumbnailFile.shouldMatchFile("attachment", "png")
            }

        }

        @Test
        fun `save non-image attachment with thumbnail`() {
            val file = multipartFile("attachment.avi", TEST_ATTACHMENT_AVI)
            val attachment =
                attachment(
                    category = AttachmentCategory.VIDEO,
                    filename = file.name,
                    folder = TEST_FOLDER_NAME,
                    metadata = metadata(mimeType = "video/avi")
                )

            attachmentService.saveAttachment(attachment, file).should {
                it.remoteStorageUrl.shouldNotBeBlank()
                it.file.shouldMatchFile("attachment", "avi")
                it.thumbnailFile.shouldMatchFile("attachment", DEFAULT_THUMBNAIL_EXTENSION)
                it.remoteStorageThumbnailUrl.shouldNotBeBlank()
            }
        }

        @Test
        fun `save non-image attachment without thumbnail`() {
            val file = multipartFile("attachment.docx", TEST_ATTACHMENT_DOCX)
            val attachment =
                attachment(
                    category = AttachmentCategory.TEXT,
                    filename = file.name,
                    folder = TEST_FOLDER_NAME,
                    metadata = metadata(mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                )

            attachmentService.saveAttachment(attachment, file).should {
                it.file.shouldMatchFile("attachment", "docx")
                it.remoteStorageUrl.shouldNotBeBlank()
                it.thumbnailFile.shouldBeNull()
                it.remoteStorageThumbnailUrl.shouldBeNull()
            }
        }

        @Test
        fun `save embedded attachment`() {
            val attachment =
                attachment(category = AttachmentCategory.EMBED, embedData = embedData(embedUrl = "some-url"))
            attachmentService.saveEmbeddedAttachment(attachment).shouldNotBeNull()
        }
    }

    @Test
    fun `delete attachment`() {
        every { remoteStorageService.uploadFile(any(), any(), any()) } returns null
        every { remoteStorageService.deleteFile(ofType(String::class), ofType(String::class)) } just Runs

        val file = multipartFile("attachment.png", TEST_ATTACHMENT_PNG)
        val attachment =
            attachment(
                category = AttachmentCategory.IMAGE,
                filename = file.name,
                folder = TEST_FOLDER_NAME,
                metadata = metadata(mimeType = "image/png")
            )
        val saved = attachmentService.saveAttachment(attachment, file)


        attachmentService.deleteAttachment(saved)
        attachmentRepository.findById(saved.id).shouldNotBePresent()
        attachment.file.shouldNotExist()
        attachment.thumbnailFile.shouldNotExist()
    }


    private fun File.shouldMatchFile(name: String, extension: String) = should {
        it.shouldExist()
        it.shouldBeAFile()
        it.shouldHaveNameWithoutExtension(name)
        it.shouldHaveExtension(extension)
        it.shouldBeReadable()
        it.shouldNotBeEmpty()
    }
}