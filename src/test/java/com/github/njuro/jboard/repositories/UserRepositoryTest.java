package com.github.njuro.jboard.repositories;

import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DataSet("/datasets/users.yml")
class UserRepositoryTest extends RepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail() {
        assertThat(userRepository.findByEmailIgnoreCase("admin@JBOARD")).hasFieldOrPropertyWithValue("username", "admin");
    }

    @Test
    void testFindByUsername() {
        assertThat(userRepository.findByUsernameIgnoreCase("moderator")).hasValueSatisfying(user -> user.getUsername().equals("moderator"));
    }
}
