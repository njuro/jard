package com.github.njuro.jboard.controllers;

import com.github.njuro.jboard.controllers.validation.RequestValidator;
import com.github.njuro.jboard.controllers.validation.ValidationException;
import com.github.njuro.jboard.facades.ThreadFacade;
import com.github.njuro.jboard.helpers.Mappings;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.models.dto.PostForm;
import com.github.njuro.jboard.models.dto.ThreadForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

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
    public Post replyToThread(Thread thread, @RequestPart PostForm postForm, @RequestPart(required = false) MultipartFile attachment, HttpServletRequest request) throws ValidationException {
        postForm.setAttachment(attachment);
        postForm.setIp(request.getRemoteAddr());
        requestValidator.validate(postForm);

        return threadFacade.replyToThread(postForm, thread);
    }

//    @Secured(UserRole.Roles.MODERATOR_ROLE)
//    @PostMapping(Mappings.PATH_VARIABLE_THREAD + "/sticky")
//    public Thread toggleStickyThread(Thread thread) {
//
//        thread.toggleSticky();
//        thread = threadService.updateThread(thread);
//
//        return thread;
//    }
//
//    @Secured(UserRole.Roles.JANITOR_ROLE)
//    @PostMapping(Mappings.PATH_VARIABLE_THREAD + "/lock")
//    public Thread toggleLockThread(Thread thread) {
//
//        thread.toggleLock();
//        thread = threadService.updateThread(thread);
//
//        return thread;
//    }
//
//    @Secured(UserRole.Roles.MODERATOR_ROLE)
//    @PostMapping(Mappings.PATH_VARIABLE_THREAD + "/delete" + Mappings.PATH_VARIABLE_POST)
//    public Thread deletePost(Thread thread, Post post) {
//        if (thread.getOriginalPost().equals(post)) {
//            // delete whole thread
//            threadService.deleteThread(thread);
//            return null;
//        } else {
//            // delete post
//            postService.deletePost(post);
//            return threadService.refreshThread(thread);
//        }
//    }

}
