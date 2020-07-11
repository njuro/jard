package com.github.njuro.jard.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller mapped to root endpoint, just for quick check if the API is running. */
@RestController
@RequestMapping("/")
public class HeartbeatRestController {

  @GetMapping
  public ResponseEntity<String> heartbeat() {
    return ResponseEntity.ok("jard API is running");
  }
}
