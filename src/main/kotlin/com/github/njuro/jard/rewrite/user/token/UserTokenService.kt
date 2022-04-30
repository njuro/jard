package com.github.njuro.jard.rewrite.user.token

import com.github.njuro.jard.rewrite.common.EXPIRED_USER_TOKENS_CHECK_PERIOD
import com.github.njuro.jard.rewrite.user.User
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
@Transactional
class UserTokenService(private val userTokenRepository: UserTokenRepository) {
    /**
     * Generates new 32 characters long token with expiration set to 30 minutes from generation.
     *
     * @param user to generate token for
     * @param type of the token
     * @return generated token
     */
    fun generateToken(user: User, type: UserTokenType): UserToken {
        val now = OffsetDateTime.now()
        val token = UserToken(
            value = randomAlphanumeric(32),
            user,
            type,
            issuedAt = now,
            expirationAt = now.plusMinutes(30)
        )
        return userTokenRepository.save(token)
    }

    /**
     * Resolves token with given value and type.
     *
     * @return resolved token or `null` if none was found.
     */
    fun resolveToken(value: String, type: UserTokenType): UserToken? {
        return userTokenRepository.findByValueAndType(value, type)
    }

    /**
     * Resolves token with given user and type.
     *
     * @return resolved token or `null` if none was found.
     */
    fun resolveToken(user: User, type: UserTokenType): UserToken? {
        return userTokenRepository.findByUserAndType(user, type)
    }

    /**
     * Checks if token of given type exists for given user.
     *
     * @return true if token exists, false otherwise.
     */
    fun doesTokenForUserExists(user: User, type: UserTokenType): Boolean =
        resolveToken(user, type) != null


    /**
     * Deletes token(s) of given type issued for given user.
     *
     * @param user to delete tokens for
     * @param type of tokens to delete
     */
    fun deleteToken(user: User, type: UserTokenType) {
        userTokenRepository.deleteByUserAndType(user, type)
    }

    /**
     * Deletes token(s) issued for given user.
     *
     * @param user to delete tokens for
     */
    fun deleteTokensForUser(user: User) {
        userTokenRepository.deleteByUser(user)
    }

    /**
     * Regularly deletes expired tokens. The check period is determined by [EXPIRED_USER_TOKENS_CHECK_PERIOD].
     */
    @Scheduled(fixedRateString = EXPIRED_USER_TOKENS_CHECK_PERIOD)
    fun deleteExpiredTokens() {
        userTokenRepository.deleteByExpirationAtBefore(OffsetDateTime.now())
    }
}
