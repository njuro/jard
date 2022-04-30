package com.github.njuro.jard.rewrite.user.token

import com.github.njuro.jard.rewrite.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface UserTokenRepository : JpaRepository<UserToken, String> {
    fun findByUserAndType(user: User, type: UserTokenType): UserToken?

    fun findByValueAndType(value: String, type: UserTokenType): UserToken?

    fun deleteByUserAndType(user: User, type: UserTokenType)

    fun deleteByExpirationAtBefore(timestamp: OffsetDateTime)

    fun deleteByUser(user: User)
}
