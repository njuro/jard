package com.github.njuro.jard.thread;

import com.github.njuro.jard.board.dto.BoardDto;
import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.config.security.methods.HasAuthorities;
import com.github.njuro.jard.post.Post;
import com.github.njuro.jard.post.dto.PostDto;
import com.github.njuro.jard.post.dto.PostForm;
import com.github.njuro.jard.thread.dto.ThreadDto;
import com.github.njuro.jard.thread.dto.ThreadForm;
import com.github.njuro.jard.user.UserAuthority;
import com.github.njuro.jard.utils.HttpUtils;
import com.github.njuro.jard.utils.SensitiveDataFilter;
import com.github.njuro.jard.utils.validation.RequestValidator;
import com.jfilter.filter.DynamicFilter;
import com.jfilter.filter.FieldFilterSetting;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(Mappings.API_ROOT_THREADS)
@Slf4j
public class ThreadRestController {

  private final ThreadFacade threadFacade;
  private final RequestValidator requestValidator;
  private final HttpUtils httpUtils;

  @Autowired
  public ThreadRestController(
      ThreadFacade threadFacade, RequestValidator requestValidator, HttpUtils httpUtils) {
    this.threadFacade = threadFacade;
    this.requestValidator = requestValidator;
    this.httpUtils = httpUtils;
  }

  @PutMapping
  public ThreadDto createThread(
      BoardDto board,
      @RequestPart ThreadForm threadForm,
      @RequestPart(required = false) MultipartFile attachment,
      HttpServletRequest request) {
    threadForm.getPostForm().setAttachment(attachment);
    threadForm.getPostForm().setIp(httpUtils.getClientIp(request));
    requestValidator.validate(threadForm);

    return threadFacade.createThread(threadForm, board);
  }

  @PutMapping(Mappings.PATH_VARIABLE_THREAD)
  @FieldFilterSetting(className = Post.class, fields = "thread")
  public PostDto replyToThread(
      ThreadDto thread,
      @RequestPart PostForm postForm,
      @RequestPart(required = false) MultipartFile attachment,
      HttpServletRequest request) {
    postForm.setAttachment(attachment);
    postForm.setIp(httpUtils.getClientIp(request));
    requestValidator.validate(postForm);

    return threadFacade.replyToThread(postForm, thread);
  }

  @GetMapping(Mappings.PATH_VARIABLE_THREAD)
  @FieldFilterSetting(className = Post.class, fields = "thread")
  @DynamicFilter(SensitiveDataFilter.class)
  public ThreadDto getThread(ThreadDto thread) {
    return threadFacade.getThread(thread);
  }

  @GetMapping(Mappings.PATH_VARIABLE_THREAD + "/new-replies")
  @FieldFilterSetting(className = Post.class, fields = "thread")
  @DynamicFilter(SensitiveDataFilter.class)
  public List<PostDto> getNewReplies(
      ThreadDto thread, @RequestParam(name = "lastPost") Long lastPostNumber) {
    return threadFacade.getNewReplies(thread, lastPostNumber);
  }

  @PostMapping(Mappings.PATH_VARIABLE_THREAD + "/sticky")
  @HasAuthorities(UserAuthority.TOGGLE_STICKY_THREAD)
  public ResponseEntity<Object> toggleStickyOnThread(ThreadDto thread) {
    threadFacade.toggleStickyOnThread(thread);
    return ResponseEntity.ok().build();
  }

  @PostMapping(Mappings.PATH_VARIABLE_THREAD + "/lock")
  @HasAuthorities(UserAuthority.TOGGLE_LOCK_THREAD)
  public ResponseEntity<Object> toggleLockOnThread(ThreadDto thread) {
    threadFacade.toggleLockOnThread(thread);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(Mappings.PATH_VARIABLE_THREAD + "/" + Mappings.PATH_VARIABLE_POST)
  @HasAuthorities(UserAuthority.DELETE_POST)
  public ResponseEntity<Object> deletePost(ThreadDto thread, PostDto post) {
    try {
      threadFacade.deletePost(thread, post);
      return ResponseEntity.ok().build();
    } catch (IOException ex) {
      log.error("Deleting post failed", ex);
      return ResponseEntity.badRequest().body("Deleting post failed");
    }
  }
}
