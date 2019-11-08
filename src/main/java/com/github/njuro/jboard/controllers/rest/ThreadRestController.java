package com.github.njuro.jboard.controllers.rest;

import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.models.dto.ThreadForm;
import com.github.njuro.jboard.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/boards/{board}")
public class ThreadRestController {

    private final ThreadService threadService;

    @Autowired
    public ThreadRestController(ThreadService threadService) {
        this.threadService = threadService;
    }

    @GetMapping("/{threadNo}")
    public Thread showThread(Thread thread) {
        return thread;
    }

    @PostMapping(value = "/submit")
    public Thread submitNewThread(Board board, @RequestPart ThreadForm threadForm, @RequestPart MultipartFile attachment) {
        threadForm.getPost().setAttachment(attachment);
        Thread thread = threadService.createThread(threadForm, board);
        return threadService.saveThread(thread);
    }
}
