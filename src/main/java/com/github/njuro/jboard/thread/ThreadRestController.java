package com.github.njuro.jboard.thread;

import com.github.njuro.jboard.board.Board;
import com.github.njuro.jboard.common.Mappings;
import com.github.njuro.jboard.config.security.methods.HasAuthorities;
import com.github.njuro.jboard.post.Post;
import com.github.njuro.jboard.post.PostForm;
import com.github.njuro.jboard.user.UserAuthority;
import com.github.njuro.jboard.utils.SensitiveDataFilter;
import com.github.njuro.jboard.utils.validation.RequestValidator;
import com.jfilter.filter.DynamicFilter;
import com.jfilter.filter.FieldFilterSetting;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
  public ThreadRestController(ThreadFacade threadFacade, RequestValidator requestValidator) {
    this.threadFacade = threadFacade;
    this.requestValidator = requestValidator;
  }

  @PutMapping
  public Thread createThread(
      Board board,
      @RequestPart ThreadForm threadForm,
      @RequestPart(required = false) MultipartFile attachment,
      HttpServletRequest request) {
    threadForm.getPostForm().setAttachment(attachment);
    threadForm.getPostForm().setIp(request.getRemoteAddr());
    requestValidator.validate(threadForm);

    return threadFacade.createThread(threadForm, board);
  }

  @PutMapping(Mappings.PATH_VARIABLE_THREAD)
  @FieldFilterSetting(className = Post.class, fields = "thread")
  public Post replyToThread(
      Thread thread,
      @RequestPart PostForm postForm,
      @RequestPart(required = false) MultipartFile attachment,
      HttpServletRequest request) {
    postForm.setAttachment(attachment);
    postForm.setIp(request.getRemoteAddr());
    requestValidator.validate(postForm);

    return threadFacade.replyToThread(postForm, thread);
  }

  @GetMapping(Mappings.PATH_VARIABLE_THREAD)
  @FieldFilterSetting(className = Post.class, fields = "thread")
  @DynamicFilter(SensitiveDataFilter.class)
  public Thread getThread(Thread thread) {
    return threadFacade.getThread(thread);
  }

  @GetMapping(Mappings.PATH_VARIABLE_THREAD + "/new-replies")
  @FieldFilterSetting(className = Post.class, fields = "thread")
  @DynamicFilter(SensitiveDataFilter.class)
  public List<Post> getNewReplies(
      Thread thread, @RequestParam(name = "lastPost") Long lastPostNumber) {
    return threadFacade.getNewReplies(thread, lastPostNumber);
  }

  @PostMapping(Mappings.PATH_VARIABLE_THREAD + "/sticky")
  @HasAuthorities(UserAuthority.TOGGLE_STICKY_THREAD)
  public ResponseEntity<?> toggleStickyOnThread(Thread thread) {
    threadFacade.toggleStickyOnThread(thread);
    return ResponseEntity.ok().build();
  }

  @PostMapping(Mappings.PATH_VARIABLE_THREAD + "/lock")
  @HasAuthorities(UserAuthority.TOGGLE_LOCK_THREAD)
  public ResponseEntity<?> toggleLockOnThread(Thread thread) {
    threadFacade.toggleLockOnThread(thread);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(Mappings.PATH_VARIABLE_THREAD + "/" + Mappings.PATH_VARIABLE_POST)
  @HasAuthorities(UserAuthority.DELETE_POST)
  public ResponseEntity<?> deletePost(Thread thread, Post post) {
    threadFacade.deletePost(thread, post);
    return ResponseEntity.ok().build();
  }
}
