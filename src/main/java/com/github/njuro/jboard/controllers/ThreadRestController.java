package com.github.njuro.jboard.controllers;

import com.github.njuro.jboard.controllers.validation.RequestValidator;
import com.github.njuro.jboard.controllers.validation.ValidationException;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.models.dto.PostForm;
import com.github.njuro.jboard.models.dto.ThreadForm;
import com.github.njuro.jboard.models.enums.UserRole;
import com.github.njuro.jboard.services.PostService;
import com.github.njuro.jboard.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/boards/{board}")
public class ThreadRestController {

    private final ThreadService threadService;
    private final PostService postService;
    private final RequestValidator requestValidator;

    @Autowired
    public ThreadRestController(ThreadService threadService, PostService postService, RequestValidator requestValidator) {
        this.threadService = threadService;
        this.postService = postService;
        this.requestValidator = requestValidator;
    }

    @GetMapping("/{threadNo}")
    public Thread showThread(Thread thread) {
        return thread;
    }

    @PostMapping(value = "/submit")
    public Thread submitNewThread(Board board, @RequestPart ThreadForm threadForm, @RequestPart(required = false) MultipartFile attachment) throws ValidationException {
        threadForm.getPost().setAttachment(attachment);
        requestValidator.validate(threadForm);
        Thread thread = threadService.createThread(threadForm, board);
        return threadService.saveThread(thread);
    }

    /**
     * Attempts to reply to thread
     */
    @PostMapping("/{threadNo}/reply")
    public Post replyToThread(Board board, Thread thread, @RequestPart PostForm postForm, @RequestPart(required = false) MultipartFile attachment) throws ValidationException {
        postForm.setAttachment(attachment);
        requestValidator.validate(postForm);
        Post post = postService.createPost(postForm, board);
        post.setThread(thread);
        post = postService.savePost(post, board);
        post.setThread(null);
        return post;
    }

    @Secured(UserRole.Roles.MODERATOR_ROLE)
    @PostMapping("/{threadNo}/sticky")
    public Thread toggleStickyThread(Thread thread) {

        thread.toggleSticky();
        thread = threadService.updateThread(thread);

        return thread;
    }

    @Secured(UserRole.Roles.JANITOR_ROLE)
    @PostMapping("/{threadNo}/lock")
    public Thread toggleLockThread(Thread thread) {

        thread.toggleLock();
        thread = threadService.updateThread(thread);

        return thread;
    }

    @Secured(UserRole.Roles.MODERATOR_ROLE)
    @PostMapping("/{threadNo}/delete/{postNo}")
    public Thread deletePost(Thread thread, Post post) {
        if (thread.getOriginalPost().equals(post)) {
            // delete whole thread
            threadService.deleteThread(thread);
            return null;
        } else {
            // delete post
            postService.deletePost(post);
            return threadService.refreshThread(thread);
        }
    }

}
