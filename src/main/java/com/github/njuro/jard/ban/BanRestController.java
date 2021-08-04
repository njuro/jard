package com.github.njuro.jard.ban;

import com.github.njuro.jard.ban.dto.BanDto;
import com.github.njuro.jard.ban.dto.BanForm;
import com.github.njuro.jard.common.Mappings;
import com.github.njuro.jard.config.security.methods.HasAuthorities;
import com.github.njuro.jard.user.UserAuthority;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Mappings.API_ROOT_BANS)
@SuppressWarnings("MVCPathVariableInspection")
public class BanRestController {

  private final BanFacade banFacade;

  @Autowired
  public BanRestController(BanFacade banFacade) {
    this.banFacade = banFacade;
  }

  @PostMapping
  @HasAuthorities({UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP})
  public ResponseEntity<BanDto> createBan(@RequestBody @Valid BanForm banForm) {
    return ResponseEntity.status(HttpStatus.CREATED).body(banFacade.createBan(banForm));
  }

  @GetMapping("/me")
  public BanDto getOwnBan(HttpServletRequest request) {
    return banFacade.getActiveBan(request.getRemoteAddr());
  }

  @GetMapping
  @HasAuthorities({UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP})
  public List<BanDto> getAllBans() {
    return banFacade.getAllBans();
  }

  @PutMapping(Mappings.PATH_VARIABLE_BAN)
  @HasAuthorities({UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP})
  public BanDto editBan(BanDto oldBan, @RequestBody @Valid BanForm banForm) {
    return banFacade.editBan(oldBan, banForm);
  }

  @PutMapping(Mappings.PATH_VARIABLE_BAN + "/unban")
  @HasAuthorities({UserAuthority.MANAGE_BANS, UserAuthority.VIEW_IP})
  public BanDto unban(BanDto ban, @RequestBody @Valid UnbanForm unbanForm) {
    return banFacade.unban(ban, unbanForm);
  }
}
