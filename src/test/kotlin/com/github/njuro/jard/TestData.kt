package com.github.njuro.jard

import com.github.njuro.jard.attachment.Attachment
import com.github.njuro.jard.attachment.AttachmentCategory
import com.github.njuro.jard.attachment.AttachmentMetadata
import com.github.njuro.jard.attachment.EmbedData
import com.github.njuro.jard.ban.Ban
import com.github.njuro.jard.ban.BanStatus
import com.github.njuro.jard.ban.UnbanForm
import com.github.njuro.jard.ban.dto.BanForm
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardSettings
import com.github.njuro.jard.board.dto.BoardForm
import com.github.njuro.jard.board.dto.BoardSettingsDto
import com.github.njuro.jard.common.Constants
import com.github.njuro.jard.config.security.JsonUsernamePasswordAuthenticationFilter
import com.github.njuro.jard.post.Post
import com.github.njuro.jard.post.dto.PostForm
import com.github.njuro.jard.thread.Thread
import com.github.njuro.jard.thread.dto.ThreadForm
import com.github.njuro.jard.user.User
import com.github.njuro.jard.user.UserAuthority
import com.github.njuro.jard.user.UserRole
import com.github.njuro.jard.user.dto.CurrentUserEditDto
import com.github.njuro.jard.user.dto.CurrentUserPasswordEditDto
import com.github.njuro.jard.user.dto.UserForm
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.mock.web.MockMultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.OffsetDateTime

val TEST_RESOURCES_FOLDER: Path = Paths.get("src", "test", "resources")
val TEST_ATTACHMENTS_FOLDER: Path = TEST_RESOURCES_FOLDER.resolve("attachments")

const val TEST_FOLDER_NAME = "test"
const val TEST_ATTACHMENT_PNG = "test_attachment.png"
const val TEST_ATTACHMENT_AVI = "test_attachment.avi"
const val TEST_ATTACHMENT_DOCX = "test_attachment.docx"
const val TEST_ATTACHMENT_PDF_PORTRAIT = "test_attachment_portrait.pdf"
const val TEST_ATTACHMENT_PDF_LANDSCAPE = "test_attachment_landscape.pdf"
const val TEST_ATTACHMENT_GIF_CORRUPTED = "test_attachment_corrupted.gif"
const val TEST_ATTACHMENT_MP3 = "test_attachment.mp3"
const val TEST_OEMBED_RESPONSE = "test_oembed_response.json"

fun board(
    label: String,
    name: String = label,
    createdAt: OffsetDateTime = OffsetDateTime.now(),
    pageCount: Int = 1,
    postCounter: Long = 1L,
    settings: BoardSettings = boardSettings()
): Board = Board.builder()
    .label(label)
    .name(name)
    .createdAt(createdAt)
    .pageCount(pageCount)
    .postCounter(postCounter)
    .settings(settings)
    .build().also { it.settings.board = it; it.settings.boardId = it.id }

fun boardSettings(
    attachmentCategories: Set<AttachmentCategory> = emptySet(),
    defaultPosterName: String? = null,
    forceDefaultPosterName: Boolean = false,
    bumpLimit: Int = 300,
    threadLimit: Int = 200,
    captchaEnabled: Boolean = false,
    countryFlags: Boolean = false,
    posterThreadIds: Boolean = false,
    nsfw: Boolean = false
): BoardSettings = BoardSettings.builder()
    .attachmentCategories(attachmentCategories)
    .defaultPosterName(defaultPosterName)
    .forceDefaultPosterName(forceDefaultPosterName)
    .bumpLimit(bumpLimit)
    .threadLimit(threadLimit)
    .captchaEnabled(captchaEnabled)
    .countryFlags(countryFlags)
    .posterThreadIds(posterThreadIds)
    .nsfw(nsfw)
    .build()

fun thread(
    board: Board,
    createdAt: OffsetDateTime = OffsetDateTime.now(),
    lastBumpAt: OffsetDateTime = OffsetDateTime.now(),
    lastReplyAt: OffsetDateTime = OffsetDateTime.now(),
    locked: Boolean = false,
    stickied: Boolean = false,
    subject: String? = null,
): Thread = Thread.builder()
    .board(board)
    .createdAt(createdAt)
    .lastBumpAt(lastBumpAt)
    .lastReplyAt(lastReplyAt)
    .locked(locked)
    .stickied(stickied)
    .subject(subject)
    .build().also { it.originalPost = post(it) }

fun post(
    thread: Thread,
    attachment: Attachment? = null,
    body: String? = null,
    capcode: UserRole? = null,
    name: String? = null,
    ip: String = "",
    countryCode: String? = null,
    countryName: String? = null,
    postNumber: Long = 1L,
    deletionCode: String = "",
    tripcode: String? = null,
    sage: Boolean = false,
    createdAt: OffsetDateTime = OffsetDateTime.now(),
    posterThreadId: String? = null
): Post = Post.builder()
    .thread(thread)
    .attachment(attachment)
    .body(body)
    .capcode(capcode)
    .name(name)
    .ip(ip)
    .countryCode(countryCode)
    .countryName(countryName)
    .postNumber(postNumber)
    .deletionCode(deletionCode)
    .tripcode(tripcode)
    .sage(sage)
    .createdAt(createdAt)
    .posterThreadId(posterThreadId)
    .build()

fun attachment(
    category: AttachmentCategory = AttachmentCategory.IMAGE,
    folder: String = "",
    originalFilename: String = "",
    filename: String = "",
    thumbnailFilename: String? = null,
    remoteStorageUrl: String? = null,
    remoteStorageThumbnailUrl: String? = null,
    metadata: AttachmentMetadata? = metadata(),
    embedData: EmbedData? = null,
): Attachment = Attachment.builder()
    .category(category)
    .folder(folder)
    .originalFilename(originalFilename)
    .filename(filename)
    .thumbnailFilename(thumbnailFilename)
    .remoteStorageUrl(remoteStorageUrl)
    .remoteStorageThumbnailUrl(remoteStorageThumbnailUrl)
    .metadata(metadata)
    .embedData(embedData)
    .build().also {
        if (it.metadata != null) {
            it.metadata.attachment = it
        }
        if (it.embedData != null) {
            it.embedData.attachment = it
        }
    }

fun metadata(
    mimeType: String = "",
    width: Int = 0,
    height: Int = 0,
    thumbnailWidth: Int = 0,
    thumbnailHeight: Int = 0,
    fileSize: String = "",
    duration: String? = null,
    checksum: String = ""
): AttachmentMetadata = AttachmentMetadata.builder()
    .mimeType(mimeType)
    .width(width)
    .height(height)
    .thumbnailWidth(thumbnailWidth)
    .thumbnailHeight(thumbnailHeight)
    .fileSize(fileSize)
    .duration(duration)
    .checksum(checksum)
    .build()

fun embedData(
    embedUrl: String? = null,
    thumbnailUrl: String? = null,
    providerName: String? = null,
    uploaderName: String? = null,
    renderedHtml: String? = null
): EmbedData = EmbedData.builder()
    .embedUrl(embedUrl)
    .thumbnailUrl(thumbnailUrl)
    .providerName(providerName)
    .uploaderName(uploaderName)
    .renderedHtml(renderedHtml)
    .build()

fun user(
    username: String = "user",
    email: String = "",
    password: String = "verysecurepassword",
    lastLogin: OffsetDateTime = OffsetDateTime.now(),
    lastLoginIp: String = "",
    createdAt: OffsetDateTime = OffsetDateTime.now(),
    registrationIp: String = "127.0.0.1",
    authorities: Set<UserAuthority> = emptySet(),
    role: UserRole? = UserRole.USER,
    enabled: Boolean = true
): User = User.builder()
    .username(username)
    .email(email)
    .password(password)
    .lastLogin(lastLogin)
    .lastLoginIp(lastLoginIp)
    .createdAt(createdAt)
    .registrationIp(registrationIp)
    .authorities(authorities)
    .role(role)
    .enabled(enabled)
    .build()

fun ban(
    ip: String = "127.0.0.1",
    status: BanStatus = BanStatus.ACTIVE,
    reason: String = "",
    bannedBy: User? = null,
    unbannedBy: User? = null,
    unbanReason: String? = null,
    validFrom: OffsetDateTime = OffsetDateTime.now(),
    validTo: OffsetDateTime? = null
): Ban = Ban.builder()
    .ip(ip)
    .status(status)
    .reason(reason)
    .bannedBy(bannedBy)
    .unbannedBy(unbannedBy)
    .unbanReason(unbanReason)
    .validFrom(validFrom)
    .validTo(validTo)
    .build()

fun loginRequest(
    username: String,
    password: String,
    rememberMe: Boolean = false
): JsonUsernamePasswordAuthenticationFilter.LoginRequest =
    JsonUsernamePasswordAuthenticationFilter.LoginRequest.builder()
        .username(username)
        .password(password)
        .rememberMe(rememberMe)
        .build()

fun userEdit(email: String): CurrentUserEditDto = CurrentUserEditDto.builder().email(email).build()

fun passwordEdit(
    currentPassword: String?,
    newPassword: String,
    newPasswordRepeated: String = newPassword
): CurrentUserPasswordEditDto = CurrentUserPasswordEditDto.builder()
    .currentPassword(currentPassword)
    .newPassword(newPassword)
    .newPasswordRepeated(newPasswordRepeated)
    .build()

fun Board.toForm(): BoardForm = BoardForm.builder()
    .label(label)
    .name(name)
    .boardSettingsForm(settings.toForm())
    .build()

fun BoardSettings.toForm(): BoardSettingsDto = BoardSettingsDto.builder()
    .attachmentCategories(attachmentCategories)
    .bumpLimit(bumpLimit)
    .captchaEnabled(isCaptchaEnabled)
    .countryFlags(isCountryFlags)
    .defaultPosterName(defaultPosterName)
    .forceDefaultPosterName(isForceDefaultPosterName)
    .nsfw(isNsfw)
    .posterThreadIds(isPosterThreadIds)
    .threadLimit(threadLimit)
    .build()

fun Thread.toForm(): ThreadForm = ThreadForm.builder()
    .locked(isLocked)
    .stickied(isStickied)
    .subject(subject)
    .postForm(originalPost?.toForm())
    .build()

fun Post.toForm(): PostForm = PostForm.builder()
    .attachment(null)
    .body(body)
    .capcode(capcode != null)
    .deletionCode(deletionCode)
    .captchaToken(null)
    .embedUrl(null)
    .ip(ip)
    .name(name)
    .password(null)
    .sage(isSage)
    .build()

fun User.toForm(): UserForm = UserForm.builder()
    .username(username)
    .email(email)
    .password(password)
    .passwordRepeated(password)
    .registrationIp(registrationIp)
    .role(role)
    .build()

fun Ban.toForm(): BanForm = BanForm.builder()
    .ip(ip)
    .reason(reason)
    .validTo(validTo)
    .warning(status == BanStatus.WARNING)
    .build()

fun Ban.toUnbanForm(unbanReason: String = ""): UnbanForm = UnbanForm.builder()
    .ip(ip)
    .reason(unbanReason)
    .build()

fun randomString(size: Int): String = RandomStringUtils.random(size)

fun multipartFile(name: String, filename: String, originalFilename: String = filename): MockMultipartFile {
    val path = TEST_ATTACHMENTS_FOLDER.resolve(filename)
    return MockMultipartFile(
        name,
        originalFilename,
        Files.probeContentType(path),
        Files.readAllBytes(path)
    )
}

fun multipartFile(name: String, size: Int): MockMultipartFile {
    return MockMultipartFile(
        name,
        "filename",
        null,
        ByteArray(size)
    )
}

fun attachmentPath(first: String, vararg more: String): Path =
    Constants.USER_CONTENT_PATH.resolve(Paths.get(first, *more))

fun testAttachmentPath(first: String, vararg more: String): Path =
    TEST_ATTACHMENTS_FOLDER.resolve(Paths.get(first, *more))
