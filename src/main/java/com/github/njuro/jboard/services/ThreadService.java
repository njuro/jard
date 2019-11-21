package com.github.njuro.jboard.services;

import com.github.njuro.jboard.exceptions.ThreadNotFoundException;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.repositories.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service methods for manipulating {@link Thread threads}
 *
 * @author njuro
 */
@Service
@Transactional(noRollbackFor = ThreadNotFoundException.class)
public class ThreadService {

    private final ThreadRepository threadRepository;

    private final PostService postService;

    @Autowired
    public ThreadService(ThreadRepository threadRepository, PostService postService) {
        this.threadRepository = threadRepository;
        this.postService = postService;
    }

    public Thread saveThread(Thread thread) {
        thread.setOriginalPost(postService.savePost(thread.getOriginalPost()));
        return threadRepository.save(thread);
    }

    public Thread resolveThread(String boardLabel, Long threadNumber) {
        return threadRepository.findByBoardLabelAndOriginalPostPostNumber(boardLabel, threadNumber)
                .orElseThrow(ThreadNotFoundException::new);
    }

    public Thread updateThread(Thread thread) {
        return threadRepository.save(thread);
    }

    public Thread updateLastReplyTimestamp(Thread thread) {
        thread.setLastReplyAt(LocalDateTime.now());
        return threadRepository.save(thread);
    }

    public void deleteThread(Thread thread) {
        threadRepository.delete(thread);
        postService.deletePosts(thread.getPosts());
    }


}
