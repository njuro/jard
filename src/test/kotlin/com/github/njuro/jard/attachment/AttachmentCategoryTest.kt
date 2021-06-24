package com.github.njuro.jard.attachment

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class AttachmentCategoryTest {

    @Test
    fun `generate preview of attachment category`() {
        AttachmentCategory.IMAGE.preview.should {
            it.shouldNotBeNull()
            it.isHasThumbnail.shouldBeTrue()
            it.name shouldBe "IMAGE"
            it.mimeTypes.shouldContainAll("image/jpeg", "image/gif", "image/png", "image/bmp", "image/x-bmp")
            it.extensions.shouldContainAll(".jpg", ".gif", ".png", ".bmp")
        }
    }

    @Test
    fun `determine attachment category from mime type`() {
        AttachmentCategory.determineAttachmentCategory("image/jpeg") shouldBe AttachmentCategory.IMAGE
        AttachmentCategory.determineAttachmentCategory("application/msword") shouldBe AttachmentCategory.TEXT
        AttachmentCategory.determineAttachmentCategory("application/foo").shouldBeNull()
    }
}