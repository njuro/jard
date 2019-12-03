package com.github.njuro.jboard.repositories;

import com.github.njuro.jboard.models.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsernameIgnoreCase(String username);

  User findByEmailIgnoreCase(String email);
}
