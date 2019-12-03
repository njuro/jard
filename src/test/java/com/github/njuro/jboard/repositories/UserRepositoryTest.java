package com.github.njuro.jboard.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DataSet("/datasets/users.yml")
class UserRepositoryTest extends RepositoryTest {

  @Autowired private UserRepository userRepository;

  @Test
  void testFindByEmail() {
    assertThat(this.userRepository.findByEmailIgnoreCase("admin@JBOARD"))
        .hasFieldOrPropertyWithValue("username", "admin");
  }

  @Test
  void testFindByUsername() {
    assertThat(this.userRepository.findByUsernameIgnoreCase("moderator"))
        .hasValueSatisfying(user -> user.getUsername().equals("moderator"));
  }
}
