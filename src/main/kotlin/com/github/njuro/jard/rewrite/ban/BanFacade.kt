package com.github.njuro.jard.rewrite.ban

import com.github.njuro.jard.rewrite.ban.BanStatus.EXPIRED
import com.github.njuro.jard.rewrite.ban.BanStatus.UNBANNED
import com.github.njuro.jard.rewrite.ban.BanStatus.WARNING
import com.github.njuro.jard.rewrite.ban.dto.BanDto
import com.github.njuro.jard.rewrite.ban.dto.BanForm
import com.github.njuro.jard.rewrite.ban.dto.UnbanForm
import com.github.njuro.jard.rewrite.base.BaseFacade
import com.github.njuro.jard.rewrite.common.EXPIRED_BANS_CHECK_PERIOD
import com.github.njuro.jard.rewrite.user.UserFacade
import com.github.njuro.jard.rewrite.utils.validation.PropertyValidationException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.UUID

@Component
class BanFacade(
    private val userFacade: UserFacade,
    private val banService: BanService
    ) : BaseFacade<Ban, BanDto>() {
    /**
     * Creates and saves new ban or warning. New ban is active immediately.
     *
     * @param banForm form with ban data
     * @return created ban
     * @throws PropertyValidationException if no user is logged in or there is already active ban on
     * given IP
     */
    fun createBan(banForm: BanForm): BanDto {
        val loggedUser = userFacade.getCurrentUser() ?: throw PropertyValidationException("No user is logged in")
        if (banService.hasActiveBan(banForm.ip)) {
            throw PropertyValidationException("There is already active ban on this IP")
        }
        val ban = banForm.toDto().let {
            it.copy(
                bannedBy = loggedUser,
                validFrom = OffsetDateTime.now(),
                validTo = if (it.status == WARNING) null else it.validTo
            )
        }
        return toDto(banService.saveBan(toEntity(ban)))
    }

    /** [BanService.getActiveBan]  */
    fun getActiveBan(ip: String): BanDto =
        toDto(banService.getActiveBan(ip) ?: throw BanNotFoundException())


    /** [BanService.hasActiveBan]  */
    fun hasActiveBan(ip: String): Boolean = banService.hasActiveBan(ip)


    /** @return all bans sorted by most to least recent
     */
    fun getAllBans(): List<BanDto> {
        val bans = banService.getAllBans().sortedByDescending(Ban::validFrom)
        return toDtoList(bans)
    }

    /** [BanService.resolveBan]  */
    fun resolveBan(id: UUID): BanDto? = toDto(banService.resolveBan(id))


    /**
     * Edits a ban. Only reason and ban ending data can be edited.
     *
     * @param oldBan ban to be edited
     * @param banForm form with new values
     * @return updated ban
     */
    fun editBan(oldBan: BanDto, banForm: BanForm): BanDto {
        val updated = oldBan.copy(reason = banForm.reason, validTo = banForm.validTo)
        return toDto(banService.saveBan(toEntity(updated)))
    }

    /**
     * Invalidates active ban.
     *
     * @param ban ban to invalidate
     * @param unbanForm form with details of invalidation
     * @return invalidated ban
     * @throws PropertyValidationException if no user is logged in or there is no active ban on given
     * IP
     */
    fun unban(ban: BanDto, unbanForm: UnbanForm): BanDto {
        val loggedUser = userFacade.getCurrentUser() ?: throw PropertyValidationException("No user is logged in!")
        if (ban.status !== BanStatus.ACTIVE) {
            throw PropertyValidationException("There is no active ban on this IP")
        }
        val updated = ban.copy(
            unbannedBy = loggedUser,
            unbanReason = unbanForm.reason,
            status = UNBANNED
        )
        return toDto(banService.saveBan(toEntity(updated)))
    }

    /**
     * Periodically checks for expired active bans and invalidates them. Period is defined by [EXPIRED_BANS_CHECK_PERIOD].
     */
    @Scheduled(fixedRateString = EXPIRED_BANS_CHECK_PERIOD)
    fun unbanExpired() {
        banService.getExpiredBans().forEach {
            it.status = EXPIRED
            banService.saveBan(it)
        }
    }
}
