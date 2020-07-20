package com.github.njuro.jard.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.njuro.jard.utils.validation.ValidationErrors;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@AutoConfigureMockMvc
public abstract class MockRequestTest {

  @Autowired protected ObjectMapper objectMapper;

  @Autowired protected MockMvc mockMvc;

  protected ResultActions performMockRequest(HttpMethod method, String url) throws Exception {
    return performMockRequest(method, buildUri(url));
  }

  protected ResultActions performMockRequest(HttpMethod method, URI url) throws Exception {
    return performMockRequest(method, url, null);
  }

  protected ResultActions performMockRequest(HttpMethod method, String url, Object body)
      throws Exception {
    return performMockRequest(method, buildUri(url), body);
  }

  protected ResultActions performMockRequest(HttpMethod method, URI url, Object body)
      throws Exception {
    return mockMvc.perform(buildRequest(method, url, body));
  }

  protected URI buildUri(String url, Object... pathVariables) {
    return UriComponentsBuilder.fromUriString(url).buildAndExpand(pathVariables).encode().toUri();
  }

  private MockHttpServletRequestBuilder buildRequest(HttpMethod method, URI url, Object body) {
    MockHttpServletRequestBuilder request =
        request(method, url).accept(MediaType.APPLICATION_JSON).with(csrf());
    if (body == null) {
      return request;
    }

    return request.contentType(MediaType.APPLICATION_JSON).content(toJson(body));
  }

  @SneakyThrows(UnsupportedEncodingException.class)
  protected <T> T getResponse(MvcResult result, Class<T> resultClass) {
    return fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8), resultClass);
  }

  protected String toJson(Object body) {
    try {
      return objectMapper.writeValueAsString(body);
    } catch (JsonProcessingException e) {
      return null;
    }
  }

  protected <T> T fromJson(String json, Class<T> resultClass) {
    try {
      return objectMapper.readValue(json, resultClass);
    } catch (JsonProcessingException e) {
      return null;
    }
  }

  protected ResultMatcher validationError(String... fields) {
    return result -> {
      Set<String> errorFields = getResponse(result, ValidationErrors.class).getErrors().keySet();
      assertThat(errorFields).containsExactlyInAnyOrder(fields);
      assertThat(result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value());
    };
  }

  protected ResultMatcher nonEmptyBody() {
    return result -> assertThat(result.getResponse().getContentAsString()).isNotBlank();
  }
}
