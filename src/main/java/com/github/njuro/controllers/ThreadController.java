package com.github.njuro.controllers;

import com.github.njuro.models.Post;
import com.github.njuro.models.Thread;
import com.github.njuro.models.dto.ThreadForm;
import com.github.njuro.services.BoardService;
import com.github.njuro.services.PostService;
import com.github.njuro.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for threads
 *
 * @author njuro
 */

@Controller
@RequestMapping(value = "/board/{board}")
public class ThreadController {

    private final PostService postService;

    private final ThreadService threadService;

    private final BoardService boardService;

    @Autowired
    public ThreadController(PostService postService, ThreadService threadService, BoardService boardService) {
        this.postService = postService;
        this.threadService = threadService;
        this.boardService = boardService;
    }

    @PostMapping("/submit")
    public String submitThread(@ModelAttribute(name = "threadForm") ThreadForm threadForm, BindingResult result) {

        Thread thread = new Thread(threadForm.getSubject(), boardService.getBoard(threadForm.getBoard()));
        threadService.createThread(thread);

        Post post = new Post(threadForm.getName(), threadForm.getTripcode(), threadForm.getComment());
        post.setThread(thread);
        postService.createPost(post);

        return "redirect:/board/" + threadForm.getBoard();
    }

    @PostMapping("/{threadNo}/reply")
    public String replyToThread(@PathVariable("board") String board,
                                @PathVariable("threadNo") Long threadNumber, @ModelAttribute(name = "post") Post post) {
        post.setThread(threadService.getThread(threadNumber));
        postService.createPost(post);

        return "redirect:/board/" + board + "/" + threadNumber;
    }

    @GetMapping("/{threadNo}")
    public String viewThread(@PathVariable("threadNo") Long threadNumber, Model model) {
        Thread thread = threadService.getThread(threadNumber);
        model.addAttribute("thread", thread);

        return "fragments/thread";
    }


}
