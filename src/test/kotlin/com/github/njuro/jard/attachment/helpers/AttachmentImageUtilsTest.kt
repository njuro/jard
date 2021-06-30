package com.github.njuro.jard.attachment.helpers

import com.github.njuro.jard.*
import com.github.njuro.jard.attachment.AttachmentCategory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.should
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.awt.image.RenderedImage
import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal class AttachmentImageUtilsTest {

    companion object {
        private val testFolder = attachmentPath(TEST_FOLDER_NAME).toFile()
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
    fun `create thumbnail for image attachment`() =
        createAndVerifyThumbnail(AttachmentCategory.IMAGE, "image/png", TEST_ATTACHMENT_PNG)

    @Test
    fun `create thumbnail for corrupted gif image attachment`() =
        createAndVerifyThumbnail(AttachmentCategory.IMAGE, "image/gif", TEST_ATTACHMENT_GIF_CORRUPTED)

    @Test
    fun `create thumbnail for video attachment`() =
        createAndVerifyThumbnail(AttachmentCategory.VIDEO, "video/avi", TEST_ATTACHMENT_AVI)

    @Test
    fun `create thumbnail for portrait pdf attachment`() =
        createAndVerifyThumbnail(AttachmentCategory.PDF, "application/x-pdf", TEST_ATTACHMENT_PDF_PORTRAIT) {
            it.height shouldBeGreaterThan it.width
        }

    @Test
    fun `create thumbnail for landscape pdf attachment`() =
        createAndVerifyThumbnail(AttachmentCategory.PDF, "application/x-pdf", TEST_ATTACHMENT_PDF_LANDSCAPE) {
            it.width shouldBeGreaterThan it.height
        }

    @Test
    fun `don't create thumbnail for other categories`() {
        shouldThrow<IllegalArgumentException> {
            createAndVerifyThumbnail(
                AttachmentCategory.TEXT,
                "vnd.openxmlformats-officedocument.wordprocessingml.document",
                TEST_ATTACHMENT_DOCX
            )
        }
    }

    private fun createAndVerifyThumbnail(
        attachmentCategory: AttachmentCategory,
        mimeType: String,
        filename: String,
        additionalCheck: ((RenderedImage) -> Unit)? = null
    ) {
        Files.copy(
            testAttachmentPath(filename),
            attachmentPath(TEST_FOLDER_NAME, filename),
            StandardCopyOption.REPLACE_EXISTING
        )

        val attachment = attachment(
            category = attachmentCategory,
            filename = filename,
            folder = TEST_FOLDER_NAME,
            metadata = metadata(mimeType = mimeType)
        )
        val thumbnail = AttachmentImageUtils.createThumbnail(attachment)

        thumbnail.should {
            it.height shouldBeGreaterThan 0
            it.width shouldBeGreaterThan 0

            if (attachment.metadata.height > 0) {
                it.height shouldBeLessThan attachment.metadata.height
            }
            if (attachment.metadata.width > 0) {
                it.width shouldBeLessThan attachment.metadata.width
            }
        }
        additionalCheck?.invoke(thumbnail)

        attachment.metadata.should {
            it.thumbnailHeight shouldBeInRange thumbnail.height - 1..thumbnail.height + 1
            it.thumbnailWidth shouldBeInRange thumbnail.width - 1..thumbnail.width + 1
        }
    }
}
