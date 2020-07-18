package com.github.njuro.jard.attachment;

import com.github.njuro.jard.attachment.embedded.EmbedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmbedServiceTest {

  @Autowired private EmbedService embedService;

  @Test
  void basicTest() throws Exception {
    var attachment = Attachment.builder().build();
    //    embedService.processEmbedded("https://www.youtube.com/watch?v=gBW0sMYpyGo", attachment);
    embedService.processEmbedded("https://soundcloud.com/pslwave/drowning", attachment);
    System.out.println("fdsfs");
  }
}
