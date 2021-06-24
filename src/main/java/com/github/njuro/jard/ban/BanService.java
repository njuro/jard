package com.github.njuro.jard.ban;

import com.github.njuro.jard.user.User;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BanService {

  private final BanRepository banRepository;

  @Autowired
  public BanService(BanRepository banRepository) {
    this.banRepository = banRepository;
  }

  /**
   * Saves new ban to database.
   *
   * @param ban ban to save
   * @return saved ban
   */
  public Ban saveBan(Ban ban) {
    return banRepository.save(ban);
  }

  /**
   * Gets active ban for given IP
   *
   * @param ip IP address to check
   * @return active ban for given IP, or {@code null} if such ban was not found
   */
  public Ban getActiveBan(String ip) {
    return banRepository.findByIpAndStatus(ip, BanStatus.ACTIVE).orElse(null);
  }

  /**
   * @param ip IP address to check
   * @return true if there is active ban on given IP, false otherwise
   */
  public boolean hasActiveBan(String ip) {
    return getActiveBan(ip) != null;
  }

  /** @return all bans in database */
  public List<Ban> getAllBans() {
    return banRepository.findAll();
  }

  /** @return list of active bans past their expiration date */
  public List<Ban> getExpiredBans() {
    return banRepository.findByStatusAndValidToBefore(BanStatus.ACTIVE, OffsetDateTime.now());
  }

  /**
   * @param user to get bans of
   * @return list of bans created by given user
   */
  public List<Ban> getBansBannedByUser(User user) {
    return banRepository.findByBannedBy(user);
  }

  /**
   * Resolves ban by given identifier.
   *
   * @param id UUID of ban
   * @return resolved ban or {@code null} if such ban was not found
   */
  public Ban resolveBan(UUID id) {
    return banRepository.findById(id).orElseThrow(BanNotFoundException::new);
  }
}
