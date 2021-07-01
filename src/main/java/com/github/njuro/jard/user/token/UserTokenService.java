package com.github.njuro.jard.user.token;

import com.github.njuro.jard.common.Constants;
import com.github.njuro.jard.user.User;
import java.time.OffsetDateTime;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserTokenService {

  private final UserTokenRepository userTokenRepository;

  public UserTokenService(UserTokenRepository userTokenRepository) {
    this.userTokenRepository = userTokenRepository;
  }

  public UserToken generateToken(User user, UserTokenType type) {
    var now = OffsetDateTime.now();
    var token =
        UserToken.builder()
            .value(RandomStringUtils.randomAlphanumeric(32))
            .user(user)
            .type(type)
            .issuedAt(now)
            .expirationAt(now.plusMinutes(30))
            .build();

    return userTokenRepository.save(token);
  }

  public UserToken resolveToken(String value) {
    return userTokenRepository.findById(value).orElse(null);
  }

  public boolean doesTokenForUserExists(User user, UserTokenType type) {
    return userTokenRepository.findByUserAndType(user, type).isPresent();
  }

  public void deleteToken(User user, UserTokenType type) {
    userTokenRepository.deleteByUserAndType(user, type);
  }

  @Scheduled(fixedRateString = Constants.EXPIRED_USER_TOKENS_CHECK_PERIOD)
  public void deleteExpiredTokens() {
    userTokenRepository.deleteByExpirationAtBefore(OffsetDateTime.now());
  }
}
