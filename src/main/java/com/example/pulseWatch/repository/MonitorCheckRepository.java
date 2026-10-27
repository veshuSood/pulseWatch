package com.example.pulseWatch.repository;
import com.example.pulseWatch.entity.MonitorCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface MonitorCheckRepository extends JpaRepository<MonitorCheck, UUID> {
    Optional<MonitorCheck>
    findTopByMonitor_IdOrderByCheckedAtDesc(UUID monitorId);
    List<MonitorCheck> findTop20ByMonitor_IdOrderByCheckedAtDesc(
            UUID monitorId
    );
    List<MonitorCheck> findByMonitor_IdAndCheckedAtGreaterThanEqual(
            UUID monitorId,
            Instant startTime
    );
}
