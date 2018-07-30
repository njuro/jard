package com.github.njuro.jboard.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PostRepositoryTest extends RepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    public void findByThreadBoardLabelAndPostNumber() {
        postRepository.findAll().forEach(post -> System.out.println(post.getBody()));
    }
}
