package com.github.njuro.jboard.repositories;

import com.github.njuro.jboard.models.Ban;
import com.github.njuro.jboard.models.enums.BanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BanRepository extends JpaRepository<Ban, Long> {

    List<Ban> findByIp(String ip);

    List<Ban> findByStatusAndEndBefore(BanStatus status, LocalDateTime dateTime);

    List<Ban> findByIpAndStatus(String ip, BanStatus status);

}
