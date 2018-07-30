package com.github.njuro.jboard.repositories;

import com.github.njuro.jboard.MockDatabaseTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public abstract class RepositoryTest extends MockDatabaseTest {
}
