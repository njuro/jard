package com.github.njuro.jard.ban;

import com.github.njuro.jard.base.BaseRepository;
import com.github.njuro.jard.user.User;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface BanRepository extends BaseRepository<Ban> {

  List<Ban> findByIp(String ip);

  List<Ban> findByStatusAndValidToBefore(BanStatus status, OffsetDateTime dateTime);

  Optional<Ban> findByIpAndStatus(String ip, BanStatus status);

  List<Ban> findByBannedBy(User bannedBy);
}
