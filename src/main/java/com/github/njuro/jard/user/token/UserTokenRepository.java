package com.github.njuro.jard.user.token;

import com.github.njuro.jard.user.User;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, String> {

  Optional<UserToken> findByUserAndType(User user, UserTokenType type);

  Optional<UserToken> findByUserAndValueAndType(User user, String value, UserTokenType type);

  void deleteByExpirationAtBefore(OffsetDateTime timestamp);
}
