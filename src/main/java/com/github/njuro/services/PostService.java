package com.github.njuro.services;

import com.github.njuro.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for CRUD operations with posts.
 *
 * @author njuro
 */
@Repository
interface PostRepository extends JpaRepository<Post, Long> {
}

@Service
@Transactional
public class PostService {

    private final BoardService boardService;

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository, BoardService boardService) {
        this.postRepository = postRepository;
        this.boardService = boardService;
    }

    public Post createPost(String board, Post post) {
        post.setDateTime(LocalDateTime.now());
        post.setPostNumber(boardService.getPostNumber(board));
        boardService.increasePostNumber(board);

        return postRepository.save(post);
    }
}
