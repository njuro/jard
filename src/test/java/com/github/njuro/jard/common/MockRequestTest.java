package com.github.njuro.jard.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.njuro.jard.attachment.AttachmentCategory;
import com.github.njuro.jard.attachment.AttachmentCategory.AttachmentCategoryDeserializer;
import com.github.njuro.jard.attachment.AttachmentCategory.AttachmentCategorySerializer;
import com.github.njuro.jard.utils.validation.ValidationErrors;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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

  @PostConstruct
  protected void initModules() {
    objectMapper.registerModule(
        new SimpleModule("AttachmentCategoryModule")
            .addSerializer(AttachmentCategory.class, new AttachmentCategorySerializer())
            .addDeserializer(AttachmentCategory.class, new AttachmentCategoryDeserializer()));
  }

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
    return performMockRequest(buildRequest(method, url, body));
  }

  protected ResultActions performMockRequest(MockHttpServletRequestBuilder request)
      throws Exception {
    return mockMvc.perform(request);
  }

  protected MockHttpServletRequestBuilder buildRequest(HttpMethod method, URI url, Object body) {
    MockHttpServletRequestBuilder request =
        request(method, url).accept(MediaType.APPLICATION_JSON).with(csrf());
    if (body == null) {
      return request;
    }

    return request.contentType(MediaType.APPLICATION_JSON).content(toJson(body));
  }

  protected ResultActions performMockMultipartRequest(
      HttpMethod method, String url, MockMultipartFile... files) throws Exception {
    return mockMvc.perform(buildMultipartRequest(method, buildUri(url), files));
  }

  protected ResultActions performMockMultipartRequest(
      HttpMethod method, URI url, MockMultipartFile... files) throws Exception {
    return mockMvc.perform(buildMultipartRequest(method, url, files));
  }

  protected MockHttpServletRequestBuilder buildMultipartRequest(
      HttpMethod method, URI url, MockMultipartFile... files) {
    var multipartRequest = multipart(url);
    for (MockMultipartFile file : files) {
      multipartRequest = multipartRequest.file(file);
    }

    return multipartRequest
        .accept(MediaType.APPLICATION_JSON)
        .with(csrf())
        .with(
            request -> {
              request.setMethod(method.name());
              return request;
            });
  }

  protected MockMultipartFile buildMultipartParam(String name, Object value) {
    return new MockMultipartFile(
        name,
        name,
        MediaType.APPLICATION_JSON_VALUE,
        toJson(value).getBytes(StandardCharsets.UTF_8));
  }

  protected MockMultipartFile buildMultipartFile(String name, String path) throws IOException {
    return buildMultipartFile(
        name, Paths.get("src", "test", "resources", "attachments").resolve(path).toFile());
  }

  protected MockMultipartFile buildMultipartFile(String name, File file) throws IOException {
    return new MockMultipartFile(
        name,
        file.getName(),
        Files.probeContentType(file.toPath()),
        Files.readAllBytes(file.toPath()));
  }

  protected URI buildUri(String url, Object... pathVariables) {
    return UriComponentsBuilder.fromUriString(url).buildAndExpand(pathVariables).encode().toUri();
  }

  @SneakyThrows(UnsupportedEncodingException.class)
  protected <T, COLLECTION extends Collection<?>> Collection<T> getResponseCollection(
      MvcResult result, Class<COLLECTION> collectionClass, Class<T> typeClass) {
    return fromJsonCollection(
        result.getResponse().getContentAsString(StandardCharsets.UTF_8),
        collectionClass,
        typeClass);
  }

  protected <T> T getResponse(MvcResult result, Class<T> resultClass) {
    return getResponse(result, resultClass, null);
  }

  @SneakyThrows(UnsupportedEncodingException.class)
  protected <T> T getResponse(MvcResult result, Class<T> resultClass, Class<?> typeClass) {
    return fromJson(
        result.getResponse().getContentAsString(StandardCharsets.UTF_8), resultClass, typeClass);
  }

  protected String toJson(Object body) {
    try {
      return objectMapper.writeValueAsString(body);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  protected <T> T fromJson(String json, Class<T> resultClass, Class<?> typeClass) {
    try {
      if (typeClass != null) {
        JavaType type =
            objectMapper.getTypeFactory().constructParametricType(resultClass, typeClass);
        return objectMapper.readValue(json, type);
      }
      return objectMapper.readValue(json, resultClass);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  protected <T, COLLECTION extends Collection<?>> Collection<T> fromJsonCollection(
      String json, Class<COLLECTION> collectionClass, Class<T> typeClass) {
    try {
      return objectMapper.readValue(
          json, TypeFactory.defaultInstance().constructCollectionType(collectionClass, typeClass));
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
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
    return result ->
        assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
            .isNotBlank()
            .isNotEqualTo("null");
  }
}
