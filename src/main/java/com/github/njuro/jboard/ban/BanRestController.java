package com.github.njuro.jboard.ban;

import com.github.njuro.jboard.common.Mappings;
import com.github.njuro.jboard.config.security.methods.HasAuthorities;
import com.github.njuro.jboard.user.UserAuthority;
import java.util.List;
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

  @PostMapping("/unban")
  @HasAuthorities({UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP})
  public Ban unban(@RequestBody @Valid UnbanForm unbanForm) {
    return banFacade.unban(unbanForm);
  }

  @GetMapping
  @HasAuthorities({UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP})
  public List<Ban> getAllBans() {
    return banFacade.getAllBans();
  }
}
