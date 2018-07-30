package com.github.njuro.jboard;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;

@DBRider
@DataSet(value = "/datasets/test.yml", useSequenceFiltering = false, disableConstraints = true, transactional = true)
public abstract class MockDatabaseTest {

}
