package com.github.njuro.jard.attachment.helpers

import com.github.njuro.jard.*
import com.github.njuro.jard.attachment.Attachment
import com.github.njuro.jard.attachment.AttachmentCategory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldNotBeBlank
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal class AttachmentMetadataUtilsTest {

    companion object {
        private val testFolder = attachmentPath(TEST_FOLDER_NAME).toFile()
        private val DURATION_PATTERN = Regex("\\d{2}:[0-5][0-9]:[0-5][0-9]")
    }

    @BeforeEach
    fun `create test folder if does not exist`() {
        testFolder.mkdirs()
    }

    @AfterEach
    fun `delete test folder if exists`() {
        if (testFolder.exists()) {
            testFolder.deleteRecursively()
        }
    }

    @Test
    fun `set image metadata`() {
        val attachment = prepareAttachment(TEST_ATTACHMENT_PNG, "image/png")

        AttachmentMetadataUtils.setMetadata(attachment)
        attachment.category shouldBe AttachmentCategory.IMAGE
        attachment.metadata.height shouldBeGreaterThan 0
        attachment.metadata.width shouldBeGreaterThan 0
        attachment.shouldHaveFileMetadata()
    }

    @Test
    fun `set video metadata`() {
        val attachment = prepareAttachment(TEST_ATTACHMENT_AVI, "video/avi")

        AttachmentMetadataUtils.setMetadata(attachment)
        attachment.category shouldBe AttachmentCategory.VIDEO
        attachment.metadata.duration.shouldMatch(DURATION_PATTERN)
        attachment.metadata.height shouldBeGreaterThan 0
        attachment.metadata.width shouldBeGreaterThan 0
        attachment.shouldHaveFileMetadata()
    }

    @Test
    fun `set audio metadata`() {
        val attachment = prepareAttachment(TEST_ATTACHMENT_MP3, "audio/mpeg3")

        AttachmentMetadataUtils.setMetadata(attachment)
        attachment.category shouldBe AttachmentCategory.AUDIO
        attachment.metadata.duration.shouldMatch(DURATION_PATTERN)
        attachment.shouldHaveFileMetadata()
    }

    @Test
    fun `set other metadata`() {
        val attachment = prepareAttachment(TEST_ATTACHMENT_PDF_PORTRAIT, "application/pdf")

        AttachmentMetadataUtils.setMetadata(attachment)
        attachment.category shouldBe AttachmentCategory.PDF
        attachment.shouldHaveFileMetadata()
    }

    @Test
    fun `don't set metadata when mime type is unknown`() {
        val attachment = prepareAttachment(TEST_ATTACHMENT_PNG, "image/foo")

        shouldThrow<IllegalArgumentException> {
            AttachmentMetadataUtils.setMetadata(attachment)
        }
    }

    private fun prepareAttachment(filename: String, mimeType: String): Attachment {
        Files.copy(
            testAttachmentPath(filename),
            attachmentPath(TEST_FOLDER_NAME, filename),
            StandardCopyOption.REPLACE_EXISTING
        )

        return attachment(
            filename = filename,
            folder = TEST_FOLDER_NAME,
            metadata = metadata(mimeType = mimeType)
        )
    }

    private fun Attachment.shouldHaveFileMetadata() = metadata.should {
        it.checksum.shouldNotBeBlank()
        it.fileSize.shouldNotBeBlank()
    }
}
