package com.github.njuro.jard.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.njuro.jard.common.RepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DataSet("/datasets/users.yml")
class UserRepositoryTest extends RepositoryTest {

  @Autowired private UserRepository userRepository;

  @Test
  void testFindByEmail() {
    assertThat(userRepository.findByEmailIgnoreCase("admin@jard"))
        .hasFieldOrPropertyWithValue("username", "admin");
  }

  @Test
  void testFindByUsername() {
    assertThat(userRepository.findByUsernameIgnoreCase("moderator"))
        .hasValueSatisfying(user -> user.getUsername().equals("moderator"));
  }
}
