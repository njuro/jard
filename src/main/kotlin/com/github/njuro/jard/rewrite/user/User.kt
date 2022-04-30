package com.github.njuro.jard.rewrite.user

import com.github.njuro.jard.rewrite.base.BaseEntity
import lombok.EqualsAndHashCode
import lombok.ToString
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.springframework.security.core.userdetails.UserDetails
import java.time.OffsetDateTime
import javax.persistence.Basic
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.PrePersist
import javax.persistence.Table

/** Entity representing registered user.  */
@Entity
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
class User(
    /** Unique username of this user.  */
    @Basic
    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    @ToString.Include
    private var username: String,

    /** Password of this user.  */
    @Basic
    @Column(nullable = false)
    private var password: String,

    /**
     * Authorities of this user.
     *
     * @see UserAuthority
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @Column(name = "authority")
    @Enumerated(value = EnumType.STRING)
    private var authorities: Set<UserAuthority>,

    /** Unique e-mail address of this user.  */
    @Basic
    @Column(unique = true)
    @ToString.Include
    var email: String?,

    /** Whether is this user enabled  */
    @Basic
    var enabled: Boolean,

    /**
     * Active role of this user.
     *
     * @see UserRole
     */
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    @ToString.Include
    var role: UserRole,

    /** IP this user registered from.  */
    @Basic
    var registrationIp: String?
) : BaseEntity(), UserDetails {

    /** IP from which this user logged into system most recently.  */
    @Basic
    var lastLoginIp: String? = null

    /** Date and time of last login of this user.  */
    var lastLogin: OffsetDateTime? = null

    /** Date and time when this user was created.  */
    @Column(nullable = false)
    var createdAt: OffsetDateTime? = null

    /** Before inserting to database, set creation date to current date and time.  */
    @PrePersist
    fun setCreatedAt() {
        createdAt = OffsetDateTime.now()
    }
    override fun getUsername() = username
    fun setUsername(username: String) { this.username = username }

    override fun getPassword() = password
    fun setPassword(password: String) { this.password = password }

    override fun getAuthorities() = authorities
    fun setAuthorities(authorities: Set<UserAuthority>) { this.authorities = authorities }


    override fun isEnabled() = true
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
}
