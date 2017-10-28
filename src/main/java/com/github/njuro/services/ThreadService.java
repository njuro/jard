package com.github.njuro.services;

import com.github.njuro.models.Thread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    public ThreadService(ThreadRepository threadRepository) {
        this.threadRepository = threadRepository;
    }

    public List<Thread> getAllThreads() {
        List<Thread> threads = new ArrayList<>();
        threads.addAll(threadRepository.findAll());
        return threads;
    }

    public List<Thread> getThreadsFromBoard(String board) {
        return threadRepository.findByBoardLabel(board);
    }

    public Thread createThread(Thread thread) {
        thread.setCreatedAt(LocalDateTime.now());
        return threadRepository.save(thread);
    }

    public Thread getThread(String board, Long threadNumber) {
        return threadRepository.findByBoardLabelAndOriginalPostPostNumber(board, threadNumber);
    }

}
