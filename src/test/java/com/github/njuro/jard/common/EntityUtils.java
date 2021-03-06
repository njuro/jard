package com.github.njuro.jard.common;

import static com.github.njuro.jard.common.InputConstraints.*;

import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.board.dto.BoardDto;
import com.github.njuro.jard.board.dto.BoardForm;
import com.github.njuro.jard.board.dto.BoardSettingsDto;
import com.github.njuro.jard.post.dto.PostDto;
import com.github.njuro.jard.post.dto.PostForm;
import com.github.njuro.jard.thread.dto.ThreadDto;
import com.github.njuro.jard.thread.dto.ThreadForm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class EntityUtils {

  private static final Random random = new Random();

  public static BoardForm randomBoardForm() {
    return BoardForm.builder()
        .label(randomString(MAX_BOARD_LABEL_LENGTH))
        .name(randomString(MAX_BOARD_NAME_LENGTH))
        .boardSettingsForm(
            BoardSettingsDto.builder()
                .attachmentCategories(
                    Collections.singleton(randomEnum(AttachmentCategory.values())))
                .nsfw(randomBoolean())
                .bumpLimit(randomPositiveInt(MAX_BUMP_LIMIT))
                .threadLimit(randomPositiveInt(MAX_THREAD_LIMIT))
                .build())
        .build();
  }

  public static ThreadForm randomThreadForm() {
    return ThreadForm.builder()
        .subject(randomString(MAX_SUBJECT_LENGTH))
        .locked(false)
        .stickied(randomBoolean())
        .postForm(randomPostForm())
        .build();
  }

  public static PostForm randomPostForm() {
    return PostForm.builder()
        .name(randomString(MAX_NAME_LENGTH))
        .password(randomString(MAX_TRIPCODE_PASSWORD_LENGTH))
        .body(randomString(MAX_POST_LENGTH))
        .attachment(randomMultipartFile())
        .build();
  }

  public static BoardDto randomBoard(int numberOfThreads) {
    BoardDto board = randomBoardForm().toDto();
    List<ThreadDto> threads = new ArrayList<>();
    for (int i = 0; i < numberOfThreads; i++) {
      threads.add(randomThread(0));
    }
    board.setThreads(threads);

    return board;
  }

  public static ThreadDto randomThread(int numberOfReplies) {
    ThreadDto thread = randomThreadForm().toDto();
    thread.setOriginalPost(randomPost());
    List<PostDto> replies = new ArrayList<>();
    for (int i = 0; i < numberOfReplies; i++) {
      replies.add(randomPost());
    }
    thread.setReplies(replies);

    return thread;
  }

  public static PostDto randomPost() {
    return randomPostForm().toDto();
  }

  private static String randomString(int max) {
    return RandomStringUtils.randomAlphabetic(1, max);
  }

  private static int randomPositiveInt(int max) {
    return 1 + random.nextInt(max - 1);
  }

  private static boolean randomBoolean() {
    return random.nextBoolean();
  }

  private static <E extends Enum<E>> E randomEnum(E[] values) {
    return values[random.nextInt(values.length)];
  }

  private static MultipartFile randomMultipartFile() {
    byte[] bytes = new byte[100];
    random.nextBytes(bytes);
    return new MockMultipartFile(randomString(32), bytes);
  }
}
