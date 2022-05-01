package com.github.njuro.jard.rewrite.ban

import com.github.njuro.jard.rewrite.base.BaseRepository
import com.github.njuro.jard.rewrite.user.User
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface BanRepository : BaseRepository<Ban> {
    fun findByIp(ip: String): List<Ban>
    fun findByStatusAndValidToBefore(status: BanStatus, dateTime: OffsetDateTime): List<Ban>
    fun findByIpAndStatus(ip: String, status: BanStatus): Ban?
    fun findByBannedBy(bannedBy: User): List<Ban>
}
