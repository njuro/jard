package com.github.njuro.jboard.services;

import com.github.njuro.jboard.exceptions.ThreadNotFoundException;
import com.github.njuro.jboard.models.Board;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.Thread;
import com.github.njuro.jboard.models.dto.ThreadForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
interface ThreadRepository extends JpaRepository<Thread, Long> {
    List<Thread> findByBoard(Board board);

    Optional<Thread> findByBoardLabelAndOriginalPostPostNumber(String label, Long postNumber);

}

/**
 * Service methods for manipulating {@link Thread threads}
 *
 * @author njuro
 */
@Service
@Transactional
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
     * Resolves all threads from board, sorted primarily by sticky status (stickied first), secondarily by creation
     * date (newest first)
     *
     * @param board to fetch threads from
     * @return list of all threads from board
     */
    public List<Thread> getSortedThreadsFromBoard(Board board) {
        List<Thread> threads = threadRepository.findByBoard(board);

        threads.sort(Comparator.comparing(Thread::isStickied)
                .thenComparing(thread -> thread.getPosts().get(thread.getPosts().size() - 1).getCreatedAt()).reversed());

        return threads;
    }


    /**
     * Creates thread (first post) from {@link ThreadForm}.
     *
     * @param form with thread details
     * @return created thread
     */
    public Thread createThread(ThreadForm form, Board board) {
        Thread thread = new Thread(form.getSubject(), form.isLocked(), form.isStickied(), board);

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

    /**
     * Deletes thread from database
     *
     * @param thread to delete
     */
    public void deleteThread(Thread thread) {
        threadRepository.delete(thread);
    }

}
