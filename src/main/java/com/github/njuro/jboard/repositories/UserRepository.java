package com.github.njuro.jboard.repositories;

import com.github.njuro.jboard.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameIgnoreCase(String username);

    User findByEmailIgnoreCase(String email);
}
