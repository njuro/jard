package com.github.njuro.jard.rewrite.base

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.util.UUID

/** Base repository for manipulating with JPA entities inheriting from [BaseEntity].  */
@NoRepositoryBean
interface BaseRepository<E : BaseEntity> : JpaRepository<E, UUID>
