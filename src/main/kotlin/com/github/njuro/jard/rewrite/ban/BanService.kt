package com.github.njuro.jard.rewrite.ban

import com.github.njuro.jard.rewrite.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
@Transactional
class BanService(private val banRepository: BanRepository) {
    /**
     * Saves new ban to database.
     *
     * @param ban ban to save
     * @return saved ban
     */
    fun saveBan(ban: Ban): Ban =  banRepository.save(ban)


    /**
     * Gets active ban for given IP
     *
     * @param ip IP address to check
     * @return active ban for given IP, or `null` if such ban was not found
     */
    fun getActiveBan(ip: String?): Ban? = banRepository.findByIpAndStatus(ip!!, BanStatus.ACTIVE)


    /**
     * @param ip IP address to check
     * @return true if there is active ban on given IP, false otherwise
     */
    fun hasActiveBan(ip: String?): Boolean = getActiveBan(ip) != null


    /** @return all bans in database
     */
    fun getAllBans(): List<Ban> = banRepository.findAll()

    /** @return list of active bans past their expiration date
     */
    fun getExpiredBans(): List<Ban> = banRepository.findByStatusAndValidToBefore(BanStatus.ACTIVE, OffsetDateTime.now())

    /**
     * @param user to get bans of
     * @return list of bans created by given user
     */
    fun getBansBannedByUser(user: User): List<Ban> = banRepository.findByBannedBy(user)

    /**
     * Resolves ban by given identifier.
     *
     * @param id UUID of ban
     * @return resolved ban or `null` if such ban was not found
     */
    fun resolveBan(id: UUID): Ban = banRepository.findById(id).orElseThrow(::BanNotFoundException)

}
