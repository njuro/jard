package com.github.njuro.jard.common;

import com.github.njuro.jard.ban.BanFacade;
import com.github.njuro.jard.board.BoardFacade;
import com.github.njuro.jard.config.security.jwt.JwtAuthenticationFilter;
import com.github.njuro.jard.post.PostFacade;
import com.github.njuro.jard.thread.ThreadFacade;
import com.github.njuro.jard.user.UserFacade;
import com.github.njuro.jard.utils.validation.RequestValidator;
import com.jfilter.filter.DynamicFilterComponent;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(
    includeFilters = {
      @Filter(type = FilterType.ASSIGNABLE_TYPE, value = RequestValidator.class),
      @Filter(DynamicFilterComponent.class)
    },
    excludeFilters = {
      @Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurerAdapter.class),
      @Filter(type = FilterType.ASSIGNABLE_TYPE, value = JwtAuthenticationFilter.class)
    })
@WithMockUser
@MockBeans({
  @MockBean(BoardFacade.class),
  @MockBean(UserFacade.class),
  @MockBean(ThreadFacade.class),
  @MockBean(PostFacade.class),
  @MockBean(BanFacade.class)
}) // needed for importing of custom argument resolvers
public abstract class ControllerTest extends MockRequestTest {}
