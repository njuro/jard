package com.github.njuro.jboard.common;

import com.github.njuro.jboard.database.UseMockDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@UseMockDatabase
public abstract class RepositoryTest {}
