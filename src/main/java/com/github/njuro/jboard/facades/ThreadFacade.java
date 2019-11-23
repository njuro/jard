package com.github.njuro.jboard.facades;

import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.models.dto.PostForm;
import com.github.njuro.jboard.models.dto.ThreadForm;
import com.github.njuro.jboard.services.BanService;
import com.github.njuro.jboard.services.PostService;
import com.github.njuro.jboard.services.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ThreadFacade {

    private final ThreadService threadService;
    private final PostService postService;
    private final BanService banService;

    private final PostFacade postFacade;

    @Autowired
    public ThreadFacade(ThreadService threadService, PostService postService, BanService banService, PostFacade postFacade) {
        this.threadService = threadService;
        this.postService = postService;
        this.banService = banService;
        this.postFacade = postFacade;
    }


    public Thread submitNewThread(@NotNull ThreadForm threadForm) {
        if (banService.hasActiveBan(threadForm.getPostForm().getIp())) {
            throw new ValidationException("Your IP address is banned");
        }

        Thread thread = threadForm.toThread();
        thread.setOriginalPost(postFacade.createPost(threadForm.getPostForm(), thread));
        thread.setLastReplyAt(LocalDateTime.now());

        return threadService.saveThread(thread);
    }

    public Post replyToThread(@NotNull PostForm postForm, Thread thread) {
        if (banService.hasActiveBan(postForm.getIp())) {
            throw new ValidationException("Your IP address is banned");
        }

        if (thread.isLocked()) {
            throw new ValidationException("Thread is locked");
        }

        Post post = postFacade.createPost(postForm, thread);
        post = postService.savePost(post);
        threadService.updateLastReplyTimestamp(thread);

        post.setThread(null);
        return post;
    }

    public List<Post> findNewPosts(Thread thread, Long lastPostNumber) {
        List<Post> posts = postService.findNewPostsInThread(thread, lastPostNumber);
        posts.forEach(post -> post.setThread(null));
        return posts;
    }

    public Thread toggleSticky(Thread thread) {
        thread.toggleSticky();
        return threadService.updateThread(thread);
    }

    public Thread toggleLock(Thread thread) {
        thread.toggleLock();
        return threadService.updateThread(thread);
    }

    public void deletePost(Thread thread, Post post) {
        if (thread.getOriginalPost().equals(post)) {
            // delete whole thread
            threadService.deleteThread(thread);
        } else {
            // delete post
            postService.deletePost(post);
        }
    }
}

