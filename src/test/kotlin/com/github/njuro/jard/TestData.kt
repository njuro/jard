package com.github.njuro.jard

import com.github.njuro.jard.attachment.Attachment
import com.github.njuro.jard.attachment.AttachmentCategory
import com.github.njuro.jard.board.Board
import com.github.njuro.jard.board.BoardSettings
import com.github.njuro.jard.board.dto.BoardForm
import com.github.njuro.jard.board.dto.BoardSettingsDto
import com.github.njuro.jard.post.Post
import com.github.njuro.jard.post.dto.PostForm
import com.github.njuro.jard.thread.Thread
import com.github.njuro.jard.thread.dto.ThreadForm
import com.github.njuro.jard.user.UserRole
import java.time.OffsetDateTime

fun board(
    label: String,
    name: String = "Board $label",
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
    ip: String = "127.0.0.1",
    countryCode: String? = null,
    countryName: String? = null,
    postNumber: Long = 1L,
    deletionCode: String = "ABCDEF",
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
    .postForm(originalPost.toForm())
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