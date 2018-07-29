package com.github.njuro.jboard;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.github.njuro.jboard.config.TestConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ContextConfiguration(classes = TestConfiguration.class)
@DBRider
@DataSet(value = "/datasets/test.yml", useSequenceFiltering = false, disableConstraints = true, transactional = true)
public abstract class MockDatabaseTest {

}
