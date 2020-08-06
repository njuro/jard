package com.github.njuro.jard.utils;

import com.github.njuro.jard.common.InputConstraints;
import com.github.njuro.jard.common.Mappings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller for utilities endpoints. */
@RestController
public class UtilitiesRestController {

  @GetMapping("/")
  public ResponseEntity<String> heartbeat() {
    return ResponseEntity.ok("jard API is running");
  }

  @GetMapping(Mappings.API_ROOT + "/input-constraints")
  public InputConstraints.Values getInputConstraints() {
    return InputConstraints.Values.INSTANCE;
  }
}
