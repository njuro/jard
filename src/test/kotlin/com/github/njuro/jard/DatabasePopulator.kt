package com.github.njuro.jard

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.njuro.jard.attachment.AttachmentCategory
import com.github.njuro.jard.board.BoardFacade
import com.github.njuro.jard.board.BoardNotFoundException
import com.github.njuro.jard.post.dto.PostForm
import com.github.njuro.jard.thread.ThreadFacade
import com.github.njuro.jard.thread.dto.ThreadForm
import io.kotest.matchers.collections.shouldNotBeEmpty
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.annotation.Commit
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.DisabledIf
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.transaction.Transactional
import kotlin.streams.toList

val log = KotlinLogging.logger { }

/**
 * Populates database with sample of real data.
 * */
@DisabledIf(
    expression = "#{ systemProperties['populate'] == null }",
    reason = "Database populator must be enabled with -Dpopulate flag"
)
@SpringBootTest
@ActiveProfiles(profiles = ["dev"], inheritProfiles = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
internal class DatabasePopulator {

    @Autowired
    private lateinit var threadFacade: ThreadFacade

    @Autowired
    private lateinit var boardFacade: BoardFacade

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        private val DATA_PATH = Paths.get("src", "test", "resources", "datasets", "realdata")
        private val IMAGES_PATH = DATA_PATH.resolve("images")
    }

    @Test
    @Commit
    @Transactional
    fun `populate real data`() {
        val board = createBoardIfNecessary()
        val files = Files.list(DATA_PATH).map(Path::toFile).filter(File::isFile).toList()
        files.forEachIndexed { index, file ->
            val thread = objectMapper.readTree(file)

            val threadForm = buildThreadForm(thread)
            if (threadForm.postForm.attachment == null) {
                log.warn { "Skipping thread ${index + 1} of ${files.size} - missing OP attachment" }
                return@forEachIndexed
            }
            val createdThread = threadFacade.createThread(threadForm, board)

            thread["posts"].drop(1).forEach {
                threadFacade.replyToThread(buildPostForm(it), createdThread)
            }

            log.info { "Populated thread ${index + 1} of ${files.size}" }
        }

        boardFacade.getBoardCatalog(board).threads.shouldNotBeEmpty()
    }

    private fun createBoardIfNecessary() = try {
        boardFacade.resolveBoard("fit")
    } catch (ex: BoardNotFoundException) {
        boardFacade.createBoard(
            board(
                label = "fit", name = "Fitness",
                settings = boardSettings(
                    attachmentCategories = setOf(AttachmentCategory.IMAGE, AttachmentCategory.VIDEO),
                    threadLimit = 200, bumpLimit = 300
                )
            ).toForm()
        )
    }

    private fun buildThreadForm(thread: JsonNode): ThreadForm {
        return ThreadForm.builder()
            .stickied(thread.has("stickied"))
            .subject(if (thread.has("subject")) thread["subject"].asText() else null)
            .postForm(buildPostForm(thread["posts"].first()))
            .build()
    }

    private fun buildPostForm(post: JsonNode): PostForm {
        val postForm = PostForm.builder()
            .body(if (post.has("body")) post["body"].asText() else null)
            .ip("127.0.0.1")
            .name(if (post.has("name")) post["name"].asText() else null)
            .build()

        if (post.has("attachment")) {
            val attachment = post["attachment"]
            postForm.attachment = createAttachment(
                attachment["filename"].asText(), attachment["originalFilename"].asText()
            )
        }

        return postForm
    }

    private fun createAttachment(filename: String, originalFilename: String): MultipartFile {
        val imagePath = IMAGES_PATH.resolve(filename)

        return MockMultipartFile(
            originalFilename,
            originalFilename,
            Files.probeContentType(imagePath),
            Files.readAllBytes(imagePath)
        )
    }
}
