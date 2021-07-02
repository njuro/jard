package com.github.njuro.jard.ban;

import com.github.njuro.jard.ban.dto.BanDto;
import com.github.njuro.jard.ban.dto.BanForm;
import com.github.njuro.jard.base.BaseFacade;
import com.github.njuro.jard.common.Constants;
import com.github.njuro.jard.user.UserFacade;
import com.github.njuro.jard.user.dto.UserDto;
import com.github.njuro.jard.utils.validation.PropertyValidationException;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BanFacade extends BaseFacade<Ban, BanDto> {

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
   * @throws PropertyValidationException if no user is logged in or there is already active ban on
   *     given IP
   */
  public BanDto createBan(BanForm banForm) {
    UserDto loggedUser = userFacade.getCurrentUser();
    if (loggedUser == null) {
      throw new PropertyValidationException("No user is logged in");
    }

    if (banService.hasActiveBan(banForm.getIp())) {
      throw new PropertyValidationException("There is already active ban on this IP");
    }

    BanDto ban = banForm.toDto();
    ban.setBannedBy(loggedUser);
    ban.setValidFrom(OffsetDateTime.now());

    if (ban.getStatus() == BanStatus.WARNING) {
      ban.setValidTo(null);
    }

    return toDto(banService.saveBan(toEntity(ban)));
  }

  /** {@link BanService#getActiveBan(String)} */
  public BanDto getActiveBan(String ip) {
    return toDto(banService.getActiveBan(ip));
  }

  /** {@link BanService#hasActiveBan(String)} */
  public boolean hasActiveBan(String ip) {
    return banService.hasActiveBan(ip);
  }

  /** @return all bans sorted by most to least recent */
  public List<BanDto> getAllBans() {
    List<Ban> bans = banService.getAllBans();
    bans.sort(Comparator.comparing(Ban::getValidFrom).reversed());
    return toDtoList(bans);
  }

  /** {@link BanService#resolveBan(UUID)} */
  public BanDto resolveBan(UUID id) {
    return toDto(banService.resolveBan(id));
  }

  /**
   * Edits a ban. Only reason and ban ending data can be edited.
   *
   * @param oldBan ban to be edited
   * @param banForm form with new values
   * @return updated ban
   */
  public BanDto editBan(BanDto oldBan, BanForm banForm) {
    oldBan.setReason(banForm.getReason());
    oldBan.setValidTo(banForm.getValidTo());

    return toDto(banService.saveBan(toEntity(oldBan)));
  }

  /**
   * Invalidates active ban.
   *
   * @param ban ban to invalidate
   * @param unbanForm form with details of invalidation
   * @return invalidated ban
   * @throws PropertyValidationException if no user is logged in or there is no active ban on given
   *     IP
   */
  public BanDto unban(BanDto ban, UnbanForm unbanForm) {
    UserDto loggedUser = userFacade.getCurrentUser();
    if (loggedUser == null) {
      throw new PropertyValidationException("No user is logged in!");
    }

    if (ban == null || ban.getStatus() != BanStatus.ACTIVE) {
      throw new PropertyValidationException("There is no active ban on this IP");
    }

    ban.setUnbannedBy(loggedUser);
    ban.setUnbanReason(unbanForm.getReason());
    ban.setStatus(BanStatus.UNBANNED);

    return toDto(banService.saveBan(toEntity(ban)));
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
