package com.github.njuro.jboard.ban;

import com.github.njuro.jboard.common.Constants;
import com.github.njuro.jboard.user.User;
import com.github.njuro.jboard.user.UserService;
import com.github.njuro.jboard.utils.validation.FormValidationException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BanFacade {

  private final BanService banService;
  private final UserService userService;

  @Autowired
  public BanFacade(BanService banService, UserService userService) {
    this.banService = banService;
    this.userService = userService;
  }

  public Ban createBan(BanForm banForm) {
    User loggedUser = userService.getCurrentUser();
    if (loggedUser == null) {
      throw new FormValidationException("No user is logged in");
    }

    if (banService.hasActiveBan(banForm.getIp())) {
      throw new FormValidationException("There is already active ban on this IP");
    }

    Ban ban = banForm.toBan();
    ban.setBannedBy(loggedUser);
    ban.setStart(LocalDateTime.now());

    if (ban.getStatus() == BanStatus.WARNING) {
      ban.setEnd(null);
    }

    return banService.saveBan(ban);
  }

  public Ban unban(UnbanForm unbanForm) {
    User loggedUser = userService.getCurrentUser();
    if (loggedUser == null) {
      throw new FormValidationException("No user is logged in!");
    }

    Ban ban = banService.getActiveBan(unbanForm.getIp());
    if (ban == null) {
      throw new FormValidationException("There is no active ban on this IP");
    }

    ban.setUnbannedBy(loggedUser);
    ban.setUnbanReason(unbanForm.getReason());
    ban.setStatus(BanStatus.UNBANNED);

    return banService.saveBan(ban);
  }

  @Scheduled(fixedRateString = Constants.EXPIRED_BANS_CHECK_PERIOD)
  public void unbanExpired() {
    List<Ban> expiredBans = banService.getExpiredBans();
    expiredBans.forEach(
        ban -> {
          ban.setStatus(BanStatus.EXPIRED);
          banService.saveBan(ban);
        });
  }
}
