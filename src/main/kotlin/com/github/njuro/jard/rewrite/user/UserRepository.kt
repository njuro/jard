package com.github.njuro.jard.rewrite.user

import com.github.njuro.jard.rewrite.base.BaseRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : BaseRepository<User> {

    fun findByUsernameIgnoreCase(username: String): Optional<User>

    fun findByEmailIgnoreCase(email: String): User
}
