package com.github.njuro.jard.user;

import com.github.njuro.jard.base.BaseRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User> {

  Optional<User> findByUsernameIgnoreCase(String username);

  User findByEmailIgnoreCase(String email);
}
