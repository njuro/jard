package com.github.njuro.jard.ban;

import com.github.njuro.jard.common.Constants;
import com.github.njuro.jard.user.User;
import com.github.njuro.jard.user.UserFacade;
import com.github.njuro.jard.utils.validation.FormValidationException;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BanFacade {

  private final UserFacade userFacade;
  private final BanService banService;

  @Autowired
  public BanFacade(UserFacade userFacade, BanService banService) {
    this.userFacade = userFacade;
    this.banService = banService;
  }

  /**
   * Creates and saves new ban or warning. New ban is active immediately.
   *
   * @param banForm form with ban data
   * @return created ban
   * @throws FormValidationException if no user is logged in or there is already active ban on given
   *     IP
   */
  public Ban createBan(BanForm banForm) {
    User loggedUser = userFacade.getCurrentUser();
    if (loggedUser == null) {
      throw new FormValidationException("No user is logged in");
    }

    if (banService.hasActiveBan(banForm.getIp())) {
      throw new FormValidationException("There is already active ban on this IP");
    }

    Ban ban = banForm.toBan();
    ban.setBannedBy(loggedUser);
    ban.setValidFrom(OffsetDateTime.now());

    if (ban.getStatus() == BanStatus.WARNING) {
      ban.setValidTo(null);
    }

    return banService.saveBan(ban);
  }

  /** @see BanService#getActiveBan(String) */
  public Ban getActiveBan(String ip) {
    return banService.getActiveBan(ip);
  }

  /** @return all bans sorted by most to least recent */
  public List<Ban> getAllBans() {
    List<Ban> bans = banService.getAllBans();
    bans.sort(Comparator.comparing(Ban::getValidFrom).reversed());
    return bans;
  }

  /** @see BanService#resolveBan(UUID) */
  public Ban resolveBan(UUID id) {
    return banService.resolveBan(id);
  }

  /**
   * Edits a ban. Only reason and ban ending data can be edited.
   *
   * @param oldBan ban to be edited
   * @param banForm form with new values
   * @return updated ban
   */
  public Ban editBan(Ban oldBan, BanForm banForm) {
    oldBan.setReason(banForm.getReason());
    oldBan.setValidTo(banForm.getValidTo());

    return banService.saveBan(oldBan);
  }

  /**
   * Invalidates active ban.
   *
   * @param ban ban to invalidate
   * @param unbanForm form with details of invalidation
   * @return invalidated ban
   * @throws FormValidationException if no user is logged in or there is no active ban on given IP
   */
  public Ban unban(Ban ban, UnbanForm unbanForm) {
    User loggedUser = userFacade.getCurrentUser();
    if (loggedUser == null) {
      throw new FormValidationException("No user is logged in!");
    }

    if (ban == null || ban.getStatus() != BanStatus.ACTIVE) {
      throw new FormValidationException("There is no active ban on this IP");
    }

    ban.setUnbannedBy(loggedUser);
    ban.setUnbanReason(unbanForm.getReason());
    ban.setStatus(BanStatus.UNBANNED);

    return banService.saveBan(ban);
  }

  /**
   * Periodically checks for expired active bans and invalidates them. Period is defined by {@link
   * Constants#EXPIRED_BANS_CHECK_PERIOD}.
   */
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
