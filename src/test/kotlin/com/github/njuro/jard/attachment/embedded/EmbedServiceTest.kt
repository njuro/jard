package com.github.njuro.jard.attachment.embedded

import com.github.njuro.jard.WithContainerDatabase
import com.github.njuro.jard.attachment.Attachment
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@SpringBootTest
@WithContainerDatabase
internal class EmbedServiceTest {

    companion object {
        private val mockServer = MockWebServer()

        @JvmStatic
        @DynamicPropertySource
        fun injectProperties(registry: DynamicPropertyRegistry) {
            registry.add("app.test.mockserver.hostname", mockServer::hostName)
            registry.add("app.test.mockserver.port", mockServer::port)
        }
    }

    @Autowired
    private lateinit var embedService: EmbedService


    @Test
    fun `get handler existing embedded provider`() {
        embedService.getHandlerForProvider(MockEmbeddedAttachmentHandler.PROVIDER_NAME)
            .shouldBeInstanceOf<MockEmbeddedAttachmentHandler>()
    }

    @Test
    fun `don't get handler non-existing embedded provider`() {
        shouldThrow<IllegalArgumentException> {
            embedService.getHandlerForProvider("xxx")
        }
    }

    @Test
    fun `correctly set embedded data`() {
        mockServer.enqueue(MockResponse().setBody(MockEmbeddedAttachmentHandler.RESPONSE))
        val attachment = Attachment.builder().build()

        embedService.processEmbedded(MockEmbeddedAttachmentHandler.SUPPORTED_URL, attachment)
        attachment.embedData.should {
            it.providerName shouldBe MockEmbeddedAttachmentHandler.PROVIDER_NAME
            it.embedUrl shouldBe MockEmbeddedAttachmentHandler.SUPPORTED_URL
            it.uploaderName shouldBe "mock-uploader"
            it.thumbnailUrl shouldBe "mock-thumbnail-url"
            it.renderedHtml shouldBe "<b>mock-data</b>"
        }
        attachment.originalFilename shouldBe "mock-title"
    }

    @Test
    fun `throw exception when oembed response is incorrect`() {
        mockServer.enqueue(MockResponse().setBody("error"))
        val attachment = Attachment.builder().build()

        shouldThrow<IllegalArgumentException> {
            embedService.processEmbedded(MockEmbeddedAttachmentHandler.SUPPORTED_URL, attachment)
        }
    }

}