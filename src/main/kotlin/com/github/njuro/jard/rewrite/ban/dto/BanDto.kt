package com.github.njuro.jard.rewrite.ban.dto

import com.github.njuro.jard.rewrite.ban.Ban
import com.github.njuro.jard.rewrite.ban.BanStatus
import com.github.njuro.jard.rewrite.base.BaseDto
import com.github.njuro.jard.rewrite.user.dto.UserDto
import java.time.OffsetDateTime

/** DTO for [Ban].  */
data class BanDto(
    /** [Ban.ip]  */
    val ip: String,

    /** [Ban.status]  */
    val status: BanStatus,

    /** [Ban.bannedBy]  */
    val bannedBy: UserDto? = null,

    /** [Ban.reason]  */
    val reason: String? = null,

    /** [Ban.unbannedBy]  */
    val unbannedBy: UserDto? = null,

    /** [Ban.unbanReason]  */
    val unbanReason: String? = null,

    /** [Ban.validFrom]  */
    val validFrom: OffsetDateTime? = null,

    /** [Ban.validTo]  */
    val validTo: OffsetDateTime? = null
) : BaseDto() {
    override fun toString(): String = "BanDto(ip='$ip', status=$status, validFrom=$validFrom, validTo=$validTo)"
}
