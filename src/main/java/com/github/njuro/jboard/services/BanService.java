package com.github.njuro.jboard.services;

import com.github.njuro.jboard.helpers.Constants;
import com.github.njuro.jboard.models.Ban;
import com.github.njuro.jboard.models.Post;
import com.github.njuro.jboard.models.User;
import com.github.njuro.jboard.models.enums.BanStatus;
import com.github.njuro.jboard.repositories.BanRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BanService {

  private final BanRepository banRepository;
  private final UserService userService;

  @Autowired
  public BanService(final BanRepository banRepository, final UserService userService) {
    this.banRepository = banRepository;
    this.userService = userService;
  }

  public boolean hasActiveBan(final String ip) {
    return !banRepository.findByIpAndStatus(ip, BanStatus.ACTIVE).isEmpty();
  }

  public Ban warn(final String ip, final String reason, final Post post) {
    return createBan(ip, reason, null, post, BanStatus.WARNING);
  }

  public Ban ban(final String ip, final String reason, final LocalDateTime end, final Post post) {
    return createBan(ip, reason, end, post, BanStatus.ACTIVE);
  }

  private Ban createBan(
      final String ip,
      final String reason,
      final LocalDateTime end,
      final Post post,
      final BanStatus banStatus) {
    final User loggedUser = UserService.getCurrentUser();
    if (loggedUser == null) {
      throw new IllegalArgumentException("No user is logged in!");
    }

    final LocalDateTime NOW = LocalDateTime.now();

    if (end != null && end.isBefore(NOW)) {
      throw new IllegalArgumentException("End datetime must be in the future");
    }

    final Ban ban =
        Ban.builder()
            .ip(ip)
            .status(banStatus)
            .post(post)
            .bannedBy(loggedUser)
            .reason(reason)
            .start(NOW)
            .end(end)
            .build();

    return banRepository.save(ban);
  }

  public Ban unban(final Ban ban, final String reason) {
    final User loggedUser = UserService.getCurrentUser();
    if (loggedUser == null) {
      throw new IllegalArgumentException("No user is logged in!");
    }

    ban.setUnbannedBy(loggedUser);
    ban.setUnbanReason(reason);

    return unban(ban, false);
  }

  private Ban unban(final Ban ban, final boolean expired) {
    if (!ban.getStatus().equals(BanStatus.ACTIVE)) {
      throw new IllegalArgumentException("Attempting to unban non-active createBan!");
    }

    ban.setStatus(expired ? BanStatus.EXPIRED : BanStatus.UNBANNED);
    return banRepository.save(ban);
  }

  @Scheduled(fixedRateString = Constants.EXPIRED_BANS_CHECK_PERIOD)
  public void checkExpiredBans() {
    final List<Ban> expiredBans =
        banRepository.findByStatusAndEndBefore(BanStatus.ACTIVE, LocalDateTime.now());
    expiredBans.forEach(ban -> unban(ban, true));
  }
}
