package com.github.njuro.jard.database;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.njuro.jard.board.BoardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@UseMockDatabase
class MockDatabaseTest {

  @Autowired private BoardRepository boardRepository;

  @Test
  void testMockDatabase() {
    assertThat(boardRepository.findAll()).isNotEmpty();
  }
}
