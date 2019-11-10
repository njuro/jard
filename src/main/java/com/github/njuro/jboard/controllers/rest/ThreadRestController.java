package com.github.njuro.jboard.controllers.rest;

import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.models.dto.PostForm;
import com.github.njuro.jboard.models.dto.ThreadForm;
import com.github.njuro.jboard.services.PostService;
import com.github.njuro.jboard.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/boards/{board}")
public class ThreadRestController {

    private final ThreadService threadService;
    private final PostService postService;

    @Autowired
    public ThreadRestController(ThreadService threadService, PostService postService) {
        this.threadService = threadService;
        this.postService = postService;
    }

    @GetMapping("/{threadNo}")
    public Thread showThread(Thread thread) {
        return thread;
    }

    @PostMapping(value = "/submit")
    public Thread submitNewThread(Board board, @RequestPart ThreadForm threadForm, @RequestPart(required = false) MultipartFile attachment) {
        threadForm.getPost().setAttachment(attachment);
        Thread thread = threadService.createThread(threadForm, board);
        return threadService.saveThread(thread);
    }

    /**
     * Attempts to reply to thread
     */
    @PostMapping("/{threadNo}/reply")
    public Post replyToThread(Board board, Thread thread, @RequestPart PostForm postForm, @RequestPart(required = false) MultipartFile attachment) {
        postForm.setAttachment(attachment);
        Post post = postService.createPost(postForm, board);
        post.setThread(thread);
        postService.savePost(post, board);

        return post;
    }
}
