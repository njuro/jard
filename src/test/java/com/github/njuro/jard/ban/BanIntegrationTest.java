package com.github.njuro.jard.ban;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.common.MockRequestTest;
import com.github.njuro.jard.common.WithMockUserAuthorities;
import com.github.njuro.jard.user.UserAuthority;
import com.github.njuro.jard.user.UserFacade;
import com.github.njuro.jard.user.UserForm;
import com.github.njuro.jard.user.UserRole;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BanIntegrationTest extends MockRequestTest {

  private static final String API_ROOT = Mappings.API_ROOT_BANS;

  @Autowired private BanFacade banFacade;
  @SpyBean private UserFacade userFacade;

  private BanForm banForm;

  @BeforeEach
  void setUp() {
    banForm =
        BanForm.builder()
            .ip("127.0.0.1")
            .reason("Test")
            .validTo(OffsetDateTime.now().plusDays(1))
            .build();

    var user =
        userFacade.createUser(
            UserForm.builder()
                .username("user")
                .password("password")
                .passwordRepeated("password")
                .email("user@email.com")
                .role(UserRole.ADMIN)
                .build());
    when(userFacade.getCurrentUser()).thenReturn(user);
  }

  @Test
  @WithMockUserAuthorities(value = {UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP})
  void testCreateBan() throws Exception {
    assertThat(banFacade.getActiveBan(banForm.getIp())).isNull();

    performMockRequest(HttpMethod.PUT, API_ROOT, banForm)
        .andExpect(status().isOk())
        .andExpect(nonEmptyBody());

    assertThat(banFacade.getActiveBan(banForm.getIp())).isNotNull();
  }

  @Test
  void testGetOwnBan() throws Exception {
    banFacade.createBan(banForm);

    var result =
        performMockRequest(HttpMethod.GET, API_ROOT + "/me")
            .andExpect(status().isOk())
            .andExpect(nonEmptyBody())
            .andReturn();

    assertThat(getResponse(result, Ban.class))
        .extracting(Ban::getReason)
        .isEqualTo(banForm.getReason());
  }

  @Test
  @WithMockUserAuthorities(value = {UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP})
  void testGetAllBans() throws Exception {
    String originalIp = banForm.getIp();
    banFacade.createBan(banForm);
    String newIp = "127.0.1.1";
    banForm.setIp(newIp);
    banFacade.createBan(banForm);

    var result =
        performMockRequest(HttpMethod.GET, API_ROOT)
            .andExpect(status().isOk())
            .andExpect(nonEmptyBody())
            .andReturn();

    assertThat(getResponseCollection(result, List.class, Ban.class))
        .extracting(Ban::getIp)
        .containsExactlyInAnyOrder(originalIp, newIp);
  }

  @Test
  @WithMockUserAuthorities(value = {UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP})
  void testEditBan() throws Exception {
    var ban = banFacade.createBan(banForm);

    assertThat(banFacade.resolveBan(ban.getId()))
        .isNotNull()
        .extracting(Ban::getReason)
        .isEqualTo(banForm.getReason());

    String newReason = "Spam";
    banForm.setReason(newReason);
    performMockRequest(
            HttpMethod.POST,
            buildUri(API_ROOT + Mappings.PATH_VARIABLE_BAN + "/edit", ban.getId()),
            banForm)
        .andExpect(status().isOk())
        .andExpect(nonEmptyBody());

    assertThat(banFacade.resolveBan(ban.getId()))
        .isNotNull()
        .extracting(Ban::getReason)
        .isEqualTo(newReason);
  }

  @Test
  @WithMockUserAuthorities(value = {UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP})
  void testUnban() throws Exception {
    var ban = banFacade.createBan(banForm);

    assertThat(banFacade.resolveBan(ban.getId()))
        .isNotNull()
        .extracting(Ban::getStatus)
        .isEqualTo(BanStatus.ACTIVE);

    var unbanForm = UnbanForm.builder().ip(ban.getIp()).reason("OK").build();
    performMockRequest(
            HttpMethod.POST,
            buildUri(API_ROOT + Mappings.PATH_VARIABLE_BAN + "/unban", ban.getId()),
            unbanForm)
        .andExpect(status().isOk())
        .andExpect(nonEmptyBody());

    assertThat(banFacade.resolveBan(ban.getId()))
        .isNotNull()
        .extracting(Ban::getStatus)
        .isEqualTo(BanStatus.UNBANNED);
  }
}
