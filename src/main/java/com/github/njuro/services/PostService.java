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

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post createPost(Post post) {
        post.setDateTime(LocalDateTime.now());
        return postRepository.save(post);
    }
}
