package com.github.njuro.jboard.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsernameIgnoreCase(String username);

  User findByEmailIgnoreCase(String email);
}
