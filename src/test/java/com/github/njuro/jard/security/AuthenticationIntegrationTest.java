package com.github.njuro.jard.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.common.MockRequestTest;
import com.github.njuro.jard.config.security.JsonUsernamePasswordAuthenticationFilter;
import com.github.njuro.jard.user.UserFacade;
import com.github.njuro.jard.user.UserRole;
import com.github.njuro.jard.user.dto.UserDto;
import com.github.njuro.jard.user.dto.UserForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AuthenticationIntegrationTest extends MockRequestTest {

  @Autowired private UserFacade userFacade;

  private UserForm userForm;

  @BeforeEach
  void setUp() {
    userForm =
        UserForm.builder()
            .username("user")
            .password("password")
            .passwordRepeated("password")
            .email("user@email.com")
            .role(UserRole.ADMIN)
            .build();
  }

  @Test
  void testLogin() throws Exception {
    userFacade.createUser(userForm);

    var loginRequest =
        JsonUsernamePasswordAuthenticationFilter.LoginRequest.builder()
            .username(userForm.getUsername())
            .password(userForm.getPassword())
            .rememberMe(true)
            .build();

    var result =
        performMockRequest(HttpMethod.POST, Mappings.API_ROOT + "/login", loginRequest)
            .andExpect(status().isOk())
            .andExpect(nonEmptyBody())
            .andReturn();

    assertThat(getResponse(result, UserDto.class))
        .extracting(UserDto::getUsername)
        .isEqualTo(userForm.getUsername());
  }

  @Test
  void testLogout() throws Exception {
    performMockRequest(
            buildRequest(HttpMethod.POST, buildUri(Mappings.API_ROOT + "/logout"), null)
                .with(user(userForm.toUser())))
        .andExpect(status().isOk());
  }
}
