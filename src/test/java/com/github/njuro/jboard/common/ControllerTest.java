package com.github.njuro.jboard.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.njuro.jboard.board.BoardFacade;
import com.github.njuro.jboard.config.security.jwt.JwtAuthenticationFilter;
import com.github.njuro.jboard.post.PostFacade;
import com.github.njuro.jboard.thread.ThreadFacade;
import com.github.njuro.jboard.user.UserFacade;
import com.github.njuro.jboard.utils.validation.RequestValidator;
import com.github.njuro.jboard.utils.validation.ValidationErrors;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(
    includeFilters = {@Filter(type = FilterType.ASSIGNABLE_TYPE, value = RequestValidator.class)},
    excludeFilters = {
      @Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurerAdapter.class),
      @Filter(type = FilterType.ASSIGNABLE_TYPE, value = JwtAuthenticationFilter.class)
    })
@WithMockUser
@MockBeans({
  @MockBean(BoardFacade.class),
  @MockBean(UserFacade.class),
  @MockBean(ThreadFacade.class),
  @MockBean(PostFacade.class)
}) // needed for importing of custom argument resolvers
public abstract class ControllerTest {

  @Autowired protected ObjectMapper objectMapper;

  @Autowired protected MockMvc mockMvc;

  @Autowired protected MessageSource messageSource;

  protected ResultActions performMockRequest(HttpMethod method, String url) throws Exception {
    return performMockRequest(method, url, null);
  }

  protected ResultActions performMockRequest(HttpMethod method, String url, Object body)
      throws Exception {
    return mockMvc.perform(buildRequest(method, url, body));
  }

  private MockHttpServletRequestBuilder buildRequest(HttpMethod method, String url, Object body)
      throws Exception {
    MockHttpServletRequestBuilder request =
        request(method, url).accept(MediaType.APPLICATION_JSON).with(csrf());
    if (body == null) {
      return request;
    }

    return request
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(body));
  }

  protected ResultMatcher validationError(String message) {
    return result -> {
      List<String> errors =
          objectMapper
              .readValue(result.getResponse().getContentAsString(), ValidationErrors.class)
              .getErrors();
      assertThat(errors).contains(message);
      assertThat(result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value());
    };
  }
}
