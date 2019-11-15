package com.github.njuro.jboard.services;

import com.github.njuro.jboard.exceptions.ThreadNotFoundException;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.models.dto.ThreadForm;
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

    /**
     * Resolves thread
     *
     * @param boardLabel   - label of board where this thread was made
     * @param threadNumber - number of thread's original post
     * @return resolved thread
     * @throws ThreadNotFoundException when thread was not found
     */
    public Thread resolveThread(String boardLabel, Long threadNumber) {
        return threadRepository.findByBoardLabelAndOriginalPostPostNumber(boardLabel, threadNumber)
                .orElseThrow(ThreadNotFoundException::new);
    }

    /**
     * Creates thread (first post) from {@link ThreadForm}.
     *
     * @param form with thread details
     * @return created thread
     */
    public Thread createThread(ThreadForm form, Board board) {
        Thread thread = Thread.builder()
                .subject(form.getSubject())
                .locked(form.isLocked())
                .stickied(form.isStickied())
                .board(board)
                .build();

        Post firstPost = postService.createPost(form.getPost(), board);
        thread.setOriginalPost(firstPost);
        firstPost.setThread(thread);

        return thread;
    }

    /**
     * Saves thread and first post to database
     *
     * @param thread to save
     * @return saved thread
     */
    public Thread saveThread(Thread thread) {
        postService.savePost(thread.getOriginalPost(), thread.getBoard());
        return threadRepository.save(thread);
    }

    /**
     * Saves updated thread into database
     *
     * @param thread to update
     * @return updated thread
     */
    public Thread updateThread(Thread thread) {
        return threadRepository.save(thread);
    }

    public Thread updateLastReplyTimestamp(Thread thread) {
        thread.setLastReplyAt(LocalDateTime.now());
        return threadRepository.save(thread);
    }

    /**
     * Deletes thread from database
     *
     * @param thread to delete
     */
    public void deleteThread(Thread thread) {
        threadRepository.delete(thread);
        postService.deletePosts(thread.getPosts());
    }


}
