package com.github.njuro.jard;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.njuro.jard.common.MockRequestTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class HeartbeatIntegrationTest extends MockRequestTest {

  @Test
  void testHeartbeat() throws Exception {
    performMockRequest(HttpMethod.GET, "/")
        .andExpect(status().isOk())
        .andExpect(content().string("\"jard API is running\""));
  }
}
