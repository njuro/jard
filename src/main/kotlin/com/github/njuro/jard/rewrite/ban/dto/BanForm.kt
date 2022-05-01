package com.github.njuro.jard.rewrite.ban.dto

import com.github.njuro.jard.rewrite.ban.BanStatus
import com.github.njuro.jard.rewrite.common.IP_PATTERN
import com.github.njuro.jard.rewrite.common.InputConstraints.MAX_BAN_REASON_LENGTH
import java.time.OffsetDateTime
import javax.validation.constraints.Future
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/** Form for creating/updating a [Ban].  */
data class BanForm(
    /** [Ban.ip]  */
    @NotNull(message = "{validation.ban.ip.null}")
    @Pattern(
        regexp = IP_PATTERN,
        message = "{validation.ban.ip.pattern}"
    )
    val ip: String?,

    /** [Ban.reason]  */
    @Size(
        max = MAX_BAN_REASON_LENGTH,
        message = "{validation.ban.reason.max}"
    )
    val reason:  String?,

    /** [Ban.validTo]  */
    @Future(message = "{validation.ban.valid.to.future}")
    val validTo: OffsetDateTime?,

    /** Whether this ban is only a warning.  */
    val warning: Boolean
) {
    /** @return [BanDto] created from values of this form.
     */
    fun toDto(): BanDto {
        val status = if (warning) BanStatus.WARNING else BanStatus.ACTIVE
        return BanDto(ip = ip!!, status = status, reason = reason, validTo = validTo)
    }

}
