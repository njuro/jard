package com.github.njuro.jard.common;

import com.github.njuro.jard.database.UseMockDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@UseMockDatabase
public abstract class RepositoryTest {}
