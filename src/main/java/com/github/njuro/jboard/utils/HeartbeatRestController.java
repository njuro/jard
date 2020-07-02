package com.github.njuro.jboard.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HeartbeatRestController {

  @GetMapping
  public ResponseEntity<String> heartbeat() {
    return ResponseEntity.ok("JBoard API is running");
  }
}
