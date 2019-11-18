package com.github.njuro.jboard.controllers.rest;

import com.github.njuro.jboard.controllers.rest.validation.RequestValidator;
import com.github.njuro.jboard.controllers.rest.validation.ValidationException;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.models.dto.PostForm;
import com.github.njuro.jboard.models.dto.ThreadForm;
import com.github.njuro.jboard.services.PostService;
import com.github.njuro.jboard.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public Thread replyToThread(Board board, Thread thread, @RequestPart PostForm postForm, @RequestPart(required = false) MultipartFile attachment) throws ValidationException {
        postForm.setAttachment(attachment);
        requestValidator.validate(postForm);
        Post post = postService.createPost(postForm, board);
        post.setThread(thread);
        post = postService.savePost(post, board);
        thread.getPosts().add(post);
        return thread;
    }

    @PostMapping("/{threadNo}/sticky")
    public Thread toggleStickyThread(Thread thread) {

        thread.toggleSticky();
        thread = threadService.updateThread(thread);

        return thread;
    }

    @PostMapping("/{threadNo}/lock")
    public Thread toggleLockThread(Thread thread) {

        thread.toggleLock();
        thread = threadService.updateThread(thread);

        return thread;
    }

    @PostMapping("/{threadNo}/delete/{postNo}")
    public ResponseEntity<?> deletePost(Thread thread, Post post) {
        if (thread.getOriginalPost().equals(post)) {
            // delete whole thread
            threadService.deleteThread(thread);
        } else {
            // delete post
            postService.deletePost(post);
        }

        return ResponseEntity.ok().build();
    }

}
