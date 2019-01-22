package com.github.njuro.jboard.controllers;

import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.models.dto.PostForm;
import com.github.njuro.jboard.models.dto.ThreadForm;
import com.github.njuro.jboard.models.enums.UserRole;
import com.github.njuro.jboard.services.BanService;
import com.github.njuro.jboard.services.PostService;
import com.github.njuro.jboard.services.ThreadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controller for manipulating threads
 *
 * @author njuro
 */

@Controller
@RequestMapping(value = "/board/{board}")
@Slf4j
public class ThreadController {

    private final PostService postService;

    private final ThreadService threadService;

    private final BanService banService;

    @Autowired
    public ThreadController(PostService postService, ThreadService threadService, BanService banService) {
        this.postService = postService;
        this.threadService = threadService;
        this.banService = banService;
    }

    /**
     * Attempts to submit new thread
     */
    @PostMapping("/submit")
    public String submitNewThread(Board board,
                                  @Valid @ModelAttribute(name = "threadForm") ThreadForm threadForm,
                                  BindingResult result, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if (banService.hasActiveBan(request.getRemoteAddr())) {
            result.reject("user.banned", "You are banned");
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.threadForm", result);
            redirectAttributes.addFlashAttribute("threadForm", threadForm);

            log.debug("Thread submission failed {}", result.getAllErrors());
            return "redirect:/board/" + board.getLabel();
        }

        Thread thread = threadService.createThread(threadForm, board);
        threadService.saveThread(thread);

        log.debug("Created new thread {}", thread);
        return "redirect:/board/" + board.getLabel() + "/" + thread.getPostNumber();
    }

    /**
     * Shows thread
     */
    @GetMapping("/{threadNo}")
    public String showThread(Thread thread, Model model) {

        model.addAttribute("thread", thread);

        return "fragments/thread";
    }


    /**
     * Attempts to reply to thread
     */
    @PostMapping("/{threadNo}/reply")
    public String replyToThread(Board board, Thread thread,
                                @Valid @ModelAttribute(name = "postForm") PostForm postForm,
                                BindingResult result, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        if (banService.hasActiveBan(request.getRemoteAddr())) {
            result.reject("user.banned", "You are banned");
        }

        if (thread.isLocked()) {
            result.reject("thread.locked", "Thread is locked");
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.postForm", result);
            redirectAttributes.addFlashAttribute("postForm", postForm);
            redirectAttributes.addFlashAttribute("threadNo", thread.getPostNumber());

            log.debug("Replying failed {}", result.getAllErrors());
            return "redirect:/board/" + board.getLabel() + "/" + thread.getPostNumber();
        }


        Post post = postService.createPost(postForm, board);
        post.setThread(thread);
        postService.savePost(post, board);

        log.debug("Created new reply {}", post);
        return "redirect:/board/" + board.getLabel() + "/" + thread.getPostNumber();
    }

    /**
     * Toggles thread's sticky status
     */
    @Secured(UserRole.Roles.MODERATOR_ROLE)
    @PostMapping("/{threadNo}/sticky")
    public String toggleStickyThread(Thread thread) {

        thread.toggleSticky();
        threadService.updateThread(thread);

        log.debug("Toggled sticky status on thread {}", thread.getPostNumber());
        return "redirect:/board/" + thread.getBoard().getLabel();
    }

    /**
     * Toggles threads' lock status
     */
    @Secured(UserRole.Roles.JANITOR_ROLE)
    @PostMapping("/{threadNo}/lock")
    public String toggleLockThread(Thread thread) {

        thread.toggleLock();
        threadService.updateThread(thread);

        log.debug("Toggled lock status on thread {}", thread.getPostNumber());
        return "redirect:/board/" + thread.getBoard().getLabel();
    }

    /**
     * Deletes post. If the post is original post, whole thread is deleted.
     */
    @Secured(UserRole.Roles.MODERATOR_ROLE)
    @PostMapping("/{threadNo}/delete/{postNo}")
    public String deletePost(Thread thread, Post post) {
        if (thread.getOriginalPost().equals(post)) {
            // delete whole thread
            threadService.deleteThread(thread);
            log.info("Deleted thread " + thread.getPostNumber());
        } else {
            // delete post
            postService.deletePost(post);
            log.info("Deleted post " + post.getPostNumber());
        }

        return "redirect:/board/" + thread.getBoard().getLabel();
    }


}
