package com.github.njuro.jard.thread;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.board.BoardFacade;
import com.github.njuro.jard.board.dto.BoardDto;
import com.github.njuro.jard.board.dto.BoardForm;
import com.github.njuro.jard.board.dto.BoardSettingsDto;
import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.common.MockRequestTest;
import com.github.njuro.jard.common.WithMockUserAuthorities;
import com.github.njuro.jard.post.PostFacade;
import com.github.njuro.jard.post.PostNotFoundException;
import com.github.njuro.jard.post.dto.PostDto;
import com.github.njuro.jard.post.dto.PostForm;
import com.github.njuro.jard.thread.dto.ThreadDto;
import com.github.njuro.jard.thread.dto.ThreadForm;
import com.github.njuro.jard.user.UserAuthority;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ThreadIntegrationTest extends MockRequestTest {

  private static final String API_ROOT = Mappings.API_ROOT_THREADS;

  @Autowired private EntityManager entityManager;
  @Autowired private BoardFacade boardFacade;
  @Autowired private ThreadFacade threadFacade;
  @Autowired private PostFacade postFacade;

  private BoardDto board;
  private ThreadForm threadForm;

  @BeforeEach
  void setUp() {
    board =
        boardFacade.createBoard(
            BoardForm.builder()
                .label("r")
                .name("Random")
                .boardSettingsForm(
                    BoardSettingsDto.builder()
                        .defaultPosterName("Anonymous")
                        .threadLimit(200)
                        .bumpLimit(300)
                        .attachmentCategories(Collections.singleton(AttachmentCategory.IMAGE))
                        .build())
                .build());

    threadForm =
        ThreadForm.builder()
            .subject("Test thread")
            .locked(false)
            .stickied(true)
            .postForm(
                PostForm.builder()
                    .body("Test post")
                    .name("Anonymous")
                    .password("Password")
                    .ip("127.0.0.1")
                    .sage(true)
                    .build())
            .build();
  }

  @Test
  void testCreateThread() throws Exception {
    performMockMultipartRequest(
            HttpMethod.PUT,
            API_ROOT,
            buildMultipartParam("threadForm", threadForm),
            buildMultipartFile("attachment", "test_attachment_1.png"))
        .andExpect(status().isOk())
        .andExpect(nonEmptyBody());

    assertThat(threadFacade.resolveThread(board.getLabel(), 1L)).isNotNull();
  }

  @Test
  void testReplyToThread() throws Exception {
    var thread = createThreadDirectly();

    PostForm reply = PostForm.builder().body("Reply").build();

    performMockMultipartRequest(
            HttpMethod.PUT,
            buildUri(API_ROOT + Mappings.PATH_VARIABLE_THREAD, thread.getThreadNumber()),
            buildMultipartParam("postForm", reply),
            buildMultipartFile("attachment", "test_attachment_2.png"))
        .andExpect(status().isOk())
        .andExpect(nonEmptyBody());

    assertThat(threadFacade.getThread(thread).getReplies()).hasSize(1);
  }

  @Test
  void testGetThread() throws Exception {
    var thread = createThreadDirectly();

    var result =
        performMockRequest(
                HttpMethod.GET,
                buildUri(API_ROOT + Mappings.PATH_VARIABLE_THREAD, thread.getThreadNumber()))
            .andExpect(status().isOk())
            .andExpect(nonEmptyBody())
            .andReturn();

    assertThat(getResponse(result, ThreadDto.class).getThreadNumber())
        .isEqualTo(thread.getThreadNumber());
  }

  @Test
  void testGetNewReplies() throws Exception {
    var thread = createThreadDirectly();
    var reply1 = createReplyDirectly(thread);
    var reply2 = createReplyDirectly(thread);
    var reply3 = createReplyDirectly(thread);

    var result =
        performMockRequest(
                HttpMethod.GET,
                buildUri(
                    API_ROOT
                        + Mappings.PATH_VARIABLE_THREAD
                        + "/new-replies?lastPost="
                        + reply1.getPostNumber(),
                    thread.getThreadNumber()))
            .andExpect(status().isOk())
            .andExpect(nonEmptyBody())
            .andReturn();

    assertThat(getResponseCollection(result, List.class, PostDto.class))
        .extracting(PostDto::getPostNumber)
        .containsExactly(reply2.getPostNumber(), reply3.getPostNumber());
  }

  @Test
  @WithMockUserAuthorities(UserAuthority.TOGGLE_STICKY_THREAD)
  void testToggleStickyOnThread() throws Exception {
    var thread = createThreadDirectly();

    boolean originalSticky = threadForm.isStickied();

    performMockRequest(
            HttpMethod.POST,
            buildUri(
                API_ROOT + Mappings.PATH_VARIABLE_THREAD + "/sticky", thread.getThreadNumber()))
        .andExpect(status().isOk());

    assertThat(threadFacade.resolveThread(board.getLabel(), thread.getThreadNumber()).isStickied())
        .isNotEqualTo(originalSticky);
  }

  @Test
  @WithMockUserAuthorities(UserAuthority.TOGGLE_LOCK_THREAD)
  void testToggleLockOnThread() throws Exception {
    var thread = createThreadDirectly();

    boolean originalLock = thread.isLocked();

    performMockRequest(
            HttpMethod.POST,
            buildUri(API_ROOT + Mappings.PATH_VARIABLE_THREAD + "/lock", thread.getThreadNumber()))
        .andExpect(status().isOk());

    assertThat(threadFacade.resolveThread(board.getLabel(), thread.getThreadNumber()).isLocked())
        .isNotEqualTo(originalLock);
  }

  @Test
  @WithMockUserAuthorities(UserAuthority.DELETE_POST)
  void testDeleteThread() throws Exception {
    var thread = createThreadDirectly();

    assertThat(postFacade.resolvePost(board.getLabel(), thread.getOriginalPost().getPostNumber()))
        .isNotNull();

    entityManager.clear();

    performMockRequest(
            HttpMethod.DELETE,
            buildUri(
                API_ROOT + Mappings.PATH_VARIABLE_THREAD + "/" + Mappings.PATH_VARIABLE_POST,
                thread.getThreadNumber(),
                thread.getOriginalPost().getPostNumber()))
        .andExpect(status().isOk());

    assertThatThrownBy(
            () ->
                postFacade.resolvePost(board.getLabel(), thread.getOriginalPost().getPostNumber()))
        .isInstanceOf(PostNotFoundException.class);
    assertThatThrownBy(() -> threadFacade.resolveThread(board.getLabel(), thread.getThreadNumber()))
        .isInstanceOf(ThreadNotFoundException.class);
  }

  @Test
  @WithMockUserAuthorities(UserAuthority.DELETE_POST)
  void testDeletePost() throws Exception {
    var thread = createThreadDirectly();
    var reply = createReplyDirectly(thread);

    assertThat(postFacade.resolvePost(board.getLabel(), reply.getPostNumber())).isNotNull();
    assertThat(threadFacade.getThread(thread).getReplies()).hasSize(1);

    performMockRequest(
            HttpMethod.DELETE,
            buildUri(
                API_ROOT + Mappings.PATH_VARIABLE_THREAD + "/" + Mappings.PATH_VARIABLE_POST,
                thread.getThreadNumber(),
                reply.getPostNumber()))
        .andExpect(status().isOk());

    assertThatThrownBy(() -> postFacade.resolvePost(board.getLabel(), reply.getPostNumber()))
        .isInstanceOf(PostNotFoundException.class);
    assertThat(threadFacade.getThread(thread).getReplies()).isEmpty();
  }

  @Override
  protected URI buildUri(String url, Object... pathVariables) {
    return super.buildUri(url, ArrayUtils.addFirst(pathVariables, board.getLabel()));
  }

  private ThreadDto createThreadDirectly() throws Exception {
    threadForm
        .getPostForm()
        .setAttachment(buildMultipartFile("attachment", "test_attachment_1.png"));
    return threadFacade.createThread(threadForm, board);
  }

  private PostDto createReplyDirectly(ThreadDto thread) throws Exception {
    return threadFacade.replyToThread(
        PostForm.builder().body("Reply").ip("127.0.0.1").build(), thread);
  }
}
