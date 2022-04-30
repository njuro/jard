package com.github.njuro.jard.rewrite.user.token

import com.github.njuro.jard.rewrite.user.User
import lombok.EqualsAndHashCode
import lombok.ToString
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Entity represents validation token issued to [User] for purposes like resetting of
 * password, or confirmation of e-mail address.
 */
@Entity
@Table(name = "user_tokens")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
class UserToken(
    /** (Unique) value of the token. Should be random long string.  */
    @Id
    @EqualsAndHashCode.Include
    var value: String,

    /** User the token was issued for.  */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Fetch(FetchMode.JOIN)
    var user: User,

    /** Type of the token.  */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ToString.Include
    var type: UserTokenType,

    /** When was the token created.  */
    @Column(nullable = false)
    @ToString.Include
    var issuedAt: OffsetDateTime,

    /** When will the token expire.  */
    @Column(nullable = false)
    @ToString.Include
    var expirationAt: OffsetDateTime
)
