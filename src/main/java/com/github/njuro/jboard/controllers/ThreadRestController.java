package com.github.njuro.jboard.controllers;

import com.github.njuro.jboard.config.security.methods.HasAuthorities;
import com.github.njuro.jboard.controllers.validation.RequestValidator;
import com.github.njuro.jboard.controllers.validation.ValidationException;
import com.github.njuro.jboard.facades.ThreadFacade;
import com.github.njuro.jboard.helpers.Mappings;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.models.dto.PostForm;
import com.github.njuro.jboard.models.dto.ThreadForm;
import com.github.njuro.jboard.models.enums.UserAuthority;
import com.jfilter.filter.FieldFilterSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @GetMapping(Mappings.PATH_VARIABLE_THREAD)
    public Thread showThread(Thread thread) {
        return thread;
    }

    @PostMapping("/submit")
    public Thread submitNewThread(Board board, @RequestPart ThreadForm threadForm, @RequestPart(required = false) MultipartFile attachment, HttpServletRequest request) throws ValidationException {
        threadForm.setBoard(board);
        threadForm.getPostForm().setAttachment(attachment);
        threadForm.getPostForm().setIp(request.getRemoteAddr());
        requestValidator.validate(threadForm);

        return threadFacade.submitNewThread(threadForm);
    }

    @PostMapping(Mappings.PATH_VARIABLE_THREAD + "/reply")
    @FieldFilterSetting(className = Post.class, fields = "thread")
    public Post replyToThread(Thread thread, @RequestPart PostForm postForm, @RequestPart(required = false) MultipartFile attachment, HttpServletRequest request) throws ValidationException {
        postForm.setAttachment(attachment);
        postForm.setIp(request.getRemoteAddr());
        requestValidator.validate(postForm);

        return threadFacade.replyToThread(postForm, thread);
    }

    @GetMapping(Mappings.PATH_VARIABLE_THREAD + "/update")
    @FieldFilterSetting(className = Post.class, fields = "thread")
    public List<Post> findNewPosts(Thread thread, @RequestParam(name = "lastPost") Long lastPostNumber) {
        return threadFacade.findNewPosts(thread, lastPostNumber);
    }

    @PostMapping(Mappings.PATH_VARIABLE_THREAD + "/sticky")
    @HasAuthorities(UserAuthority.TOGGLE_STICKY_THREAD)
    public ResponseEntity<?> toggleStickyThread(Thread thread) {
        threadFacade.toggleSticky(thread);
        return ResponseEntity.ok().build();
    }

    @PostMapping(Mappings.PATH_VARIABLE_THREAD + "/lock")
    @HasAuthorities(UserAuthority.TOGGLE_LOCK_THREAD)
    public ResponseEntity<?> toggleLockThread(Thread thread) {
        threadFacade.toggleLock(thread);
        return ResponseEntity.ok().build();
    }

    @PostMapping(Mappings.PATH_VARIABLE_THREAD + "/delete" + Mappings.PATH_VARIABLE_POST)
    @HasAuthorities(UserAuthority.DELETE_POST)
    public ResponseEntity<?> deletePost(Thread thread, Post post) {
        threadFacade.deletePost(thread, post);
        return ResponseEntity.ok().build();
    }

}
