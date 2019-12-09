package com.github.njuro.jboard.controllers;

import com.github.njuro.jboard.config.security.SensitiveDataFilter;
import com.github.njuro.jboard.config.security.methods.HasAuthorities;
import com.github.njuro.jboard.controllers.validation.RequestValidator;
import com.github.njuro.jboard.facades.ThreadFacade;
import com.github.njuro.jboard.helpers.Mappings;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.models.dto.forms.PostForm;
import com.github.njuro.jboard.models.dto.forms.ThreadForm;
import com.github.njuro.jboard.models.enums.UserAuthority;
import com.jfilter.filter.DynamicFilter;
import com.jfilter.filter.FieldFilterSetting;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(Mappings.API_ROOT_THREADS)
public class ThreadRestController {

  private final ThreadFacade threadFacade;
  private final RequestValidator requestValidator;

  @Autowired
  public ThreadRestController(
      final ThreadFacade threadFacade, final RequestValidator requestValidator) {
    this.threadFacade = threadFacade;
    this.requestValidator = requestValidator;
  }

  @GetMapping(Mappings.PATH_VARIABLE_THREAD)
  @DynamicFilter(SensitiveDataFilter.class)
  public Thread showThread(final Thread thread) {
    return this.threadFacade.getFullThread(thread);
  }

  @PostMapping("/submit")
  public Thread submitNewThread(
      final Board board,
      @RequestPart final ThreadForm threadForm,
      @RequestPart(required = false) final MultipartFile attachment,
      final HttpServletRequest request) {
    threadForm.setBoard(board);
    threadForm.getPostForm().setAttachment(attachment);
    threadForm.getPostForm().setIp(request.getRemoteAddr());
    this.requestValidator.validate(threadForm);

    return this.threadFacade.submitNewThread(threadForm);
  }

  @PostMapping(Mappings.PATH_VARIABLE_THREAD + "/reply")
  @FieldFilterSetting(className = Post.class, fields = "thread")
  public Post replyToThread(
      final Thread thread,
      @RequestPart final PostForm postForm,
      @RequestPart(required = false) final MultipartFile attachment,
      final HttpServletRequest request) {
    postForm.setAttachment(attachment);
    postForm.setIp(request.getRemoteAddr());
    this.requestValidator.validate(postForm);

    return this.threadFacade.replyToThread(postForm, thread);
  }

  @GetMapping(Mappings.PATH_VARIABLE_THREAD + "/update")
  @FieldFilterSetting(className = Post.class, fields = "thread")
  @DynamicFilter(SensitiveDataFilter.class)
  public List<Post> findNewPosts(
      final Thread thread, @RequestParam(name = "lastPost") final Long lastPostNumber) {
    return this.threadFacade.findNewPosts(thread, lastPostNumber);
  }

  @PostMapping(Mappings.PATH_VARIABLE_THREAD + "/sticky")
  @HasAuthorities(UserAuthority.TOGGLE_STICKY_THREAD)
  public ResponseEntity<?> toggleStickyThread(final Thread thread) {
    this.threadFacade.toggleSticky(thread);
    return ResponseEntity.ok().build();
  }

  @PostMapping(Mappings.PATH_VARIABLE_THREAD + "/lock")
  @HasAuthorities(UserAuthority.TOGGLE_LOCK_THREAD)
  public ResponseEntity<?> toggleLockThread(final Thread thread) {
    this.threadFacade.toggleLock(thread);
    return ResponseEntity.ok().build();
  }

  @PostMapping(Mappings.PATH_VARIABLE_THREAD + "/delete" + Mappings.PATH_VARIABLE_POST)
  @HasAuthorities(UserAuthority.DELETE_POST)
  public ResponseEntity<?> deletePost(final Thread thread, final Post post) {
    this.threadFacade.deletePost(thread, post);
    return ResponseEntity.ok().build();
  }
}
