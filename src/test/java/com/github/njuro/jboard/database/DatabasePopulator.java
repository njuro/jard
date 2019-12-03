package com.github.njuro.jboard.database;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.spring.api.DBRider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.DisabledIf;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DisabledIf(
    expression = "#{ systemProperties['populate'] == null}",
    reason = "Database populator must be enabled with -Dpopulate flag")
@DataJpaTest
@DBRider
@DBUnit(caseSensitiveTableNames = true)
@ActiveProfiles(profiles = "dev", inheritProfiles = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public class DatabasePopulator {

  @Test
  @DataSet(
      value = {"/datasets/test.yml", "/datasets/users.yml"},
      disableConstraints = true,
      executeStatementsBefore = "SET FOREIGN_KEY_CHECKS=0",
      executeStatementsAfter = "SET FOREIGN_KEY_CHECKS=1",
      transactional = true,
      useSequenceFiltering = false,
      strategy = SeedStrategy.CLEAN_INSERT)
  @Commit
  public void populateDatabase() {
    log.info("Database populated");
  }
}
