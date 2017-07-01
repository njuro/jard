package com.github.njuro.controllers;

import com.github.njuro.models.Post;
import com.github.njuro.models.Thread;
import com.github.njuro.models.dto.ThreadForm;
import com.github.njuro.services.BoardService;
import com.github.njuro.services.PostService;
import com.github.njuro.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

/**
 * Created by juro on 6/28/17.
 */

@Controller
@RequestMapping("/thread")
public class ThreadController {

    @Autowired
    private PostService postService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private BoardService boardService;

    @PostMapping("/submit")
    public String submitThread(@ModelAttribute(name = "threadForm") ThreadForm threadForm, BindingResult result) {
        LocalDateTime now = LocalDateTime.now();

        Thread thread = new Thread(threadForm.getSubject(), boardService.getBoard(threadForm.getBoard()));
        thread.setDateTime(now);
        threadService.createThread(thread);

        Post post = new Post(threadForm.getName(), threadForm.getTripcode(), threadForm.getComment());
        post.setDateTime(now);
        post.setThread(thread);
        postService.createPost(post);

        return "redirect:/" + threadForm.getBoard();
    }

}
