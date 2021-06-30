package com.github.njuro.jard.attachment.embedded

import com.github.njuro.jard.attachment.Attachment
import com.github.njuro.jard.attachment.embedded.handlers.EmbeddedAttachmentHandler
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldNotBeBlank
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.DisabledIf
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Checks availability of various supported oEmbed endpoints.
 * As this requires calling external services, it must be enabled
 * with special flag so it doesn't randomly crash build pipelines.
 * */
@DisabledIf(
    expression = "#{ systemProperties['oembed'] == null }",
    reason = "This test must be enabled with -Doembed flag"
)
@ExtendWith(SpringExtension::class)
@ContextConfiguration
internal class OEmbedEndpointsAvailabilityTest {

    @Configuration
    @ComponentScan(
        includeFilters = [
            ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                value = [EmbeddedAttachmentHandler::class]
            )
        ],
        useDefaultFilters = false
    )
    @Import(
        EmbedService::class
    )
    internal class EmbedConfiguration

    @Autowired
    private lateinit var embedService: EmbedService

    @Test
    fun `code pen`() = testEmbed("https://codepen.io/lynnandtonic/pen/dyGjvLB")

    @Test
    fun `code sandbox`() = testEmbed("https://codesandbox.io/s/j0y0vpz59")

    @Test
    fun reddit() = testEmbed(
        "https://reddit.com/r/java/comments/hyb41c/what_is_your_favourite_java_libraryframework/",
        requiredThumbnail = false
    )

    @Test
    fun scribd() = testEmbed("https://www.scribd.com/document/357546412/Opinion-on-Sarah-Palin-lawsuit")

    @Test
    fun soundcloud() = testEmbed("https://soundcloud.com/pslwave/drowning")

    @Test
    fun spotify() = testEmbed(
        "https://open.spotify.com/album/0YvYmLBFFwYxgI4U9KKgUm?highlight=spotify:track:2qToAcex0ruZfbEbAy9OhW"
    )

    @Test
    fun `tik tok`() = testEmbed("https://www.tiktok.com/@scout2015/video/6718335390845095173")

    @Test
    fun twitter() = testEmbed("https://twitter.com/elonmusk/status/1284291528328790016", requiredThumbnail = false)

    @Test
    fun vimeo() = testEmbed("https://vimeo.com/437808118", requiredThumbnail = false)

    @Test
    fun youtube() = testEmbed("https://www.youtube.com/watch?v=kJQP7kiw5Fk")

    private fun testEmbed(url: String, requiredThumbnail: Boolean = false) {
        val attachment = Attachment.builder().build()
        embedService.processEmbedded(url, attachment)

        attachment.embedData.shouldNotBeNull()
        attachment.embedData.should {
            it.renderedHtml.shouldNotBeBlank()
            it.uploaderName.shouldNotBeBlank()
            it.embedUrl.shouldNotBeBlank()
            it.providerName.shouldNotBeBlank()
            if (requiredThumbnail) {
                it.thumbnailUrl.shouldNotBeBlank()
            }
        }
    }
}
