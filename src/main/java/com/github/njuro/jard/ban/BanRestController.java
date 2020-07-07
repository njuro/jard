package com.github.njuro.jard.ban;

import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.config.security.methods.HasAuthorities;
import com.github.njuro.jard.user.UserAuthority;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Mappings.API_ROOT_BANS)
public class BanRestController {

  private final BanFacade banFacade;

  @Autowired
  public BanRestController(BanFacade banFacade) {
    this.banFacade = banFacade;
  }

  @PutMapping
  @HasAuthorities({UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP})
  public Ban createBan(@RequestBody @Valid BanForm banForm) {
    return banFacade.createBan(banForm);
  }

  @GetMapping("/me")
  public Ban getOwnBan(HttpServletRequest request) {
    return banFacade.getActiveBan(request.getRemoteAddr());
  }

  @GetMapping
  @HasAuthorities({UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP})
  public List<Ban> getAllBans() {
    return banFacade.getAllBans();
  }

  @PostMapping(Mappings.PATH_VARIABLE_BAN + "/edit")
  @HasAuthorities({UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP})
  public Ban editBan(Ban oldBan, @RequestBody @Valid BanForm banForm) {
    return banFacade.editBan(oldBan, banForm);
  }

  @PostMapping(Mappings.PATH_VARIABLE_BAN + "/unban")
  @HasAuthorities({UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP})
  public Ban unban(Ban ban, @RequestBody @Valid UnbanForm unbanForm) {
    return banFacade.unban(ban, unbanForm);
  }
}
