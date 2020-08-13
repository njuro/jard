package com.github.njuro.jard.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.common.MockRequestTest;
import com.github.njuro.jard.common.WithMockUserAuthorities;
import com.github.njuro.jard.user.dto.UserDto;
import com.github.njuro.jard.user.dto.UserForm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserIntegrationTest extends MockRequestTest {

  private static final String API_ROOT = Mappings.API_ROOT_USERS;

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
  @WithMockUserAuthorities(UserAuthority.MANAGE_USERS)
  void testCreateUser() throws Exception {
    performMockRequest(HttpMethod.PUT, API_ROOT, userForm)
        .andExpect(status().isOk())
        .andExpect(nonEmptyBody());

    assertThat(userFacade.resolveUser(userForm.getUsername())).isNotNull();
  }

  @Test
  @WithMockUserAuthorities(UserAuthority.MANAGE_USERS)
  void testGetAllUsers() throws Exception {
    var user1 = userFacade.createUser(userForm);
    userForm.setUsername("user2");
    userForm.setEmail("user2@email.com");
    var user2 = userFacade.createUser(userForm);

    var result =
        performMockRequest(HttpMethod.GET, API_ROOT)
            .andExpect(status().isOk())
            .andExpect(nonEmptyBody())
            .andReturn();

    assertThat(getResponseCollection(result, List.class, UserDto.class))
        .extracting(UserDto::getUsername)
        .containsExactlyInAnyOrder(user1.getUsername(), user2.getUsername());
  }

  @Test
  void testGetCurrentUser() throws Exception {
    var result =
        performMockRequest(
                buildRequest(HttpMethod.GET, buildUri(API_ROOT + "/current"), null)
                    .with(user(userForm.toUser())))
            .andExpect(status().isOk())
            .andExpect(nonEmptyBody())
            .andReturn();

    assertThat(getResponse(result, UserDto.class))
        .extracting(UserDto::getUsername)
        .isEqualTo(userForm.getUsername());
  }

  @Test
  @WithMockUserAuthorities(UserAuthority.MANAGE_USERS)
  void testEditUser() throws Exception {
    var user = userFacade.createUser(userForm);
    assertThat(userFacade.resolveUser(user.getUsername()).getEmail()).isEqualTo(user.getEmail());

    String newEmail = "newemail@user.com";
    userForm.setEmail(newEmail);
    performMockRequest(
            HttpMethod.POST,
            buildUri(API_ROOT + Mappings.PATH_VARIABLE_USER + "/edit", user.getUsername()),
            userForm)
        .andExpect(status().isOk())
        .andExpect(nonEmptyBody());

    assertThat(userFacade.resolveUser(user.getUsername()).getEmail()).isEqualTo(newEmail);
  }

  @Test
  @WithMockUserAuthorities(UserAuthority.MANAGE_USERS)
  void testDeleteUser() throws Exception {
    var user = userFacade.createUser(userForm);
    assertThat(userFacade.resolveUser(user.getUsername())).isNotNull();

    performMockRequest(
            HttpMethod.DELETE, buildUri(API_ROOT + Mappings.PATH_VARIABLE_USER, user.getUsername()))
        .andExpect(status().isOk());

    assertThatThrownBy(() -> userFacade.resolveUser(user.getUsername()))
        .isInstanceOf(UserNotFoundException.class);
  }
}
