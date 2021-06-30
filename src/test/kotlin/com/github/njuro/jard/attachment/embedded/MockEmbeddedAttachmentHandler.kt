package com.github.njuro.jard.attachment.embedded

import ac.simons.oembed.OembedEndpoint
import ac.simons.oembed.OembedResponse
import com.github.njuro.jard.TEST_OEMBED_RESPONSE
import com.github.njuro.jard.attachment.embedded.handlers.EmbeddedAttachmentHandler
import com.github.njuro.jard.testAttachmentPath
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.nio.file.Files

@Component
@Profile("test")
internal class MockEmbeddedAttachmentHandler : EmbeddedAttachmentHandler {

    @Value("\${app.test.mockserver.hostname:localhost}")
    private val endpointHostname: String = ""

    @Value("\${app.test.mockserver.port:0}")
    private val endpointPort: Int = 0

    companion object {
        internal const val PROVIDER_NAME = "mock-embedded-attachment-handler"
        internal const val SUPPORTED_URL = "https://mock-service.com/"
        internal val RESPONSE = Files.readString(testAttachmentPath(TEST_OEMBED_RESPONSE))
    }

    override fun getProviderName() = PROVIDER_NAME

    @Suppress("HttpUrlsUsage")
    override fun registerEndpoint() = OembedEndpoint().apply {
        name = providerName
        format = OembedResponse.Format.json
        endpoint = "http://$endpointHostname:$endpointPort"
        urlSchemes = listOf(SUPPORTED_URL)
    }
}
