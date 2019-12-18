package com.github.njuro.jboard.common;

import com.github.njuro.jboard.board.BoardFacade;
import com.github.njuro.jboard.config.security.jwt.JwtAuthenticationFilter;
import com.github.njuro.jboard.post.PostFacade;
import com.github.njuro.jboard.thread.ThreadFacade;
import com.github.njuro.jboard.user.UserFacade;
import com.github.njuro.jboard.utils.validation.RequestValidator;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;

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
})
public abstract class WebControllerTest {}
