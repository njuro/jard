package com.github.njuro.jboard.controllers.rest;

import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/boards/{board}")
public class ThreadRestController {

    private final ThreadService threadService;

    @Autowired
    public ThreadRestController(ThreadService threadService) {
        this.threadService = threadService;
    }

    @GetMapping("/threads")
    public List<Thread> getThreadsFromBoard(Board board) {
        return threadService.getSortedThreadsFromBoard(board);
    }

    /**
     * Shows thread
     */
    @GetMapping("/threads/{threadNo}")
    public Thread showThread(Thread thread) {
        return thread;
    }
}
