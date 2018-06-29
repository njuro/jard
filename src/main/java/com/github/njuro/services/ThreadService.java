package com.github.njuro.services;

import com.github.njuro.models.Board;
import com.github.njuro.models.Post;
import com.github.njuro.models.Thread;
import com.github.njuro.models.dto.ThreadForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for CRUD operations with threads
 *
 * @author njuro
 */

@Repository
interface ThreadRepository extends JpaRepository<Thread, Long> {
    List<Thread> findByBoardLabel(String label);

    Thread findByBoardLabelAndOriginalPostPostNumber(String label, Long postNumber);
}

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

    public Thread getThread(Board board, Long threadNumber) {
        return threadRepository.findByBoardLabelAndOriginalPostPostNumber(board.getLabel(), threadNumber);
    }


    public List<Thread> getThreadsFromBoard(Board board) {
        return threadRepository.findByBoardLabel(board.getLabel());
    }


    public Thread createThread(ThreadForm form, Board board) {
        Thread thread = new Thread(form.getSubject(), form.isLocked(), form.isStickied(), board);

        Post firstPost = postService.createPost(form.getPost(), board);
        thread.setOriginalPost(firstPost);
        firstPost.setThread(thread);

        return thread;
    }

    public Thread saveThread(Thread thread) {
        thread.setCreatedAt(LocalDateTime.now());

        postService.savePost(thread.getOriginalPost(), thread.getBoard());
        return threadRepository.save(thread);
    }


}
