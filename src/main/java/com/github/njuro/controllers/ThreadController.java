package com.github.njuro.controllers;

import com.github.njuro.models.Board;
import com.github.njuro.models.Post;
import com.github.njuro.models.Thread;
import com.github.njuro.models.dto.PostForm;
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
    public String submitThread(@PathVariable("board") String boardLabel,
                               @ModelAttribute(name = "threadForm") ThreadForm threadForm, BindingResult result) {

        Board board = boardService.getBoard(boardLabel);
        Thread thread = threadService.createThread(threadForm, board);
        threadService.saveThread(thread);

        return "redirect:/board/" + boardLabel + "/" + thread.getOriginalPost().getPostNumber();
    }

    @PostMapping("/{threadNo}/reply")
    public String replyToThread(@PathVariable("board") String boardLabel,
                                @PathVariable("threadNo") Long threadNumber, @ModelAttribute(name = "postForm") PostForm postForm) {

        Board board = boardService.getBoard(boardLabel);

        Post post = postService.createPost(postForm, board);
        post.setThread(threadService.getThread(board, threadNumber));
        postService.savePost(post, board);

        return "redirect:/board/" + boardLabel + "/" + threadNumber;
    }

    @GetMapping("/{threadNo}")
    public String viewThread(@PathVariable("board") String boardLabel,
                             @PathVariable("threadNo") Long threadNumber, Model model) {
        Board board = boardService.getBoard(boardLabel);

        Thread thread = threadService.getThread(board, threadNumber);

        model.addAttribute("thread", thread);

        return "fragments/thread";
    }


}
