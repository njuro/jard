package com.github.njuro.jboard.ban;

import com.github.njuro.jboard.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BanRepository extends JpaRepository<Ban, UUID> {

  List<Ban> findByIp(String ip);

  List<Ban> findByStatusAndValidToBefore(BanStatus status, LocalDateTime dateTime);

  Optional<Ban> findByIpAndStatus(String ip, BanStatus status);

  List<Ban> findByBannedBy(User bannedBy);
}
