package com.github.njuro.jard.rewrite.ban.dto

import com.github.njuro.jard.rewrite.ban.Ban
import com.github.njuro.jard.rewrite.common.IP_PATTERN
import com.github.njuro.jard.rewrite.common.InputConstraints.MAX_BAN_REASON_LENGTH
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/** Form for invalidating ban before its natural expiration.  */
data class UnbanForm(
    /** [Ban.ip]  */
    @NotNull(message = "{validation.ban.ip.null}") @Pattern(
        regexp = IP_PATTERN,
        message = "{validation.ban.ip.pattern}"
    )
    val ip:  String?,

    /** [Ban.unbanReason]  */
    @Size(
        max = MAX_BAN_REASON_LENGTH,
        message = "{validation.ban.reason.max}"
    )
    val reason: String?
)
