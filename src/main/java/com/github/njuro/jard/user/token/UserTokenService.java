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

  /**
   * Generates new 32 characters long token with expiration set to 30 minutes from generation.
   *
   * @param user to generate token for
   * @param type of the token
   * @return generated token
   */
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

  /**
   * Resolves token with given value and type.
   *
   * @return resolved token or {@code null} if none was found.
   */
  public UserToken resolveToken(String value, UserTokenType type) {
    return userTokenRepository.findByValueAndType(value, type).orElse(null);
  }

  /**
   * Checks if token of given type exists for given user.
   *
   * @return true if token exists, false otherwise.
   */
  public boolean doesTokenForUserExists(User user, UserTokenType type) {
    return userTokenRepository.findByUserAndType(user, type).isPresent();
  }

  /**
   * Deletes token(s) of given type issued for given user.
   *
   * @param user to delete tokens for
   * @param type of tokens to delete
   */
  public void deleteToken(User user, UserTokenType type) {
    userTokenRepository.deleteByUserAndType(user, type);
  }

  /**
   * Deletes token(s) issued for given user.
   *
   * @param user to delete tokens for
   */
  public void deleteTokensForUser(User user) {
    userTokenRepository.deleteByUser(user);
  }

  /**
   * Regularly deletes expired tokens. The check perios is determined by {@link
   * Constants#EXPIRED_USER_TOKENS_CHECK_PERIOD}.
   */
  @Scheduled(fixedRateString = Constants.EXPIRED_USER_TOKENS_CHECK_PERIOD)
  public void deleteExpiredTokens() {
    userTokenRepository.deleteByExpirationAtBefore(OffsetDateTime.now());
  }
}
