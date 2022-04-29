package com.github.njuro.jard.rewrite.config.security.methods

import com.github.njuro.jard.user.UserAuthority
import org.springframework.security.access.ConfigAttribute

/** Configuration attribute based on [UserAuthority].  */
class AuthorityAttribute(val authority: UserAuthority) : ConfigAttribute {
    override fun getAttribute() = authority.name
}
