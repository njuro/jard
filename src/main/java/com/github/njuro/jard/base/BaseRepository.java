package com.github.njuro.jard.base;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/** Base repository for manipulating with JPA entites inheriting from {@link BaseEntity}. */
@NoRepositoryBean
public interface BaseRepository<ENTITY extends BaseEntity> extends JpaRepository<ENTITY, UUID> {}
