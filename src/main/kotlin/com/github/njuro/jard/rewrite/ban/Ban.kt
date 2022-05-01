package com.github.njuro.jard.rewrite.ban

import com.github.njuro.jard.rewrite.base.BaseEntity
import com.github.njuro.jard.rewrite.user.User
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.OffsetDateTime
import javax.persistence.Basic
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Entity representing a ban on IP, which prevents it from posting. Special case of this is warning,
 * which should only warn user next time he attempts to post.
 */
@Entity
@Table(name = "bans")
class Ban(
    /** Banned IP (e.g. `127.0.0.1`)  */
    @Basic
    @Column(nullable = false)
    var ip: String,

    /**
     * Current status of this ban.
     *
     * @see BanStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: BanStatus,

    /** User who created this ban. Can be `null` if the user was deleted meanwhile.  */
    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    var bannedBy: User?,

    /** Reason for this ban (e.g. `spam`).  */
    @Basic
    var reason: String?,

    /** (Optional) user who ended this ban before its natural expiration.  */
    @ManyToOne(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    var unbannedBy: User?,

    /** (Optional) reason for ending this ban before its natural expiration  */
    @Basic
    var unbanReason: String?,

    /** Date and time this ban started to be valid.  */
    @Column(nullable = false)
    var validFrom: OffsetDateTime,

    /**
     * (Optional) date and time this ban expired / will expire. Value of `null` means the ban is
     * permanent (unless the ban is just a warning).
     */
    var validTo: OffsetDateTime?
): BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Ban) return false

        if (ip != other.ip) return false

        return true
    }

    override fun hashCode() = ip.hashCode()
    override fun toString(): String = "Ban(ip='$ip', status=$status, validFrom=$validFrom, validTo=$validTo)"
}
