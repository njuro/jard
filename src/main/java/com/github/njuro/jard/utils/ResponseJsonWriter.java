package com.github.njuro.jard.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ResponseJsonWriter {

  private final ObjectMapper objectMapper;

  @Autowired
  public ResponseJsonWriter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Serializes given object and writes is as response body.
   *
   * @param response response to HTTP request
   * @param object object to serialize
   * @throws IOException if serializing or writing to response fails
   */
  public void writeJsonToResponse(HttpServletResponse response, Object object) throws IOException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(objectMapper.writeValueAsString(object));
    response.getWriter().flush();
  }
}
