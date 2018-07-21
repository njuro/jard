package com.github.njuro.jboard.services;

import com.github.njuro.jboard.models.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    private Board boardG;

    @BeforeEach
    public void setupBoard() {
        boardG = Board.builder().label("g").name("Technology").postCounter(25L).build();
        boardG = boardRepository.save(boardG);
    }

    @Test
    public void testFindByLabel() {
        assertThat(boardRepository.findByLabel("g").get()).isEqualToIgnoringNullFields(boardG);
        assertThat(boardRepository.findByLabel("b")).isNotPresent();
    }

    @Test
    public void testPostCounter() {
        long postCounterBefore = boardG.getPostCounter();
        boardRepository.increasePostNumber(boardG.getLabel());
        long postCounterAfter = boardRepository.getPostCounter(boardG.getLabel());

        assertThat(postCounterAfter).isEqualTo(postCounterBefore + 1);
    }
}
