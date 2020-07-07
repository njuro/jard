package com.github.njuro.jard.ban;

import com.github.njuro.jard.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BanService {

  private final BanRepository banRepository;

  @Autowired
  public BanService(BanRepository banRepository) {
    this.banRepository = banRepository;
  }

  public Ban saveBan(Ban ban) {
    return banRepository.save(ban);
  }

  public Ban getActiveBan(String ip) {
    return banRepository.findByIpAndStatus(ip, BanStatus.ACTIVE).orElse(null);
  }

  public boolean hasActiveBan(String ip) {
    return getActiveBan(ip) != null;
  }

  public List<Ban> getAllBans() {
    return banRepository.findAll();
  }

  public List<Ban> getExpiredBans() {
    return banRepository.findByStatusAndValidToBefore(BanStatus.ACTIVE, LocalDateTime.now());
  }

  public List<Ban> getBansBannedByUser(User user) {
    return banRepository.findByBannedBy(user);
  }

  public Ban resolveBan(UUID id) {
    return banRepository.findById(id).orElse(null);
  }
}
