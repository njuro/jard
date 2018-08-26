package com.github.njuro.jboard.database;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@UseMockDatabase
public class MockDatabaseTest {

    @Test
    public void testMockDatabase() {
        // empty
    }

}
