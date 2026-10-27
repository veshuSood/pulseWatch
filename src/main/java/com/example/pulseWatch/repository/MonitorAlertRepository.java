package com.example.pulseWatch.repository;

import com.example.pulseWatch.entity.MonitorAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MonitorAlertRepository extends JpaRepository<MonitorAlert, UUID> {
    List<MonitorAlert> findTop20ByMonitor_IdOrderByCreatedAtDesc(
            UUID monitorId
    );
    Optional<MonitorAlert> findTopByMonitor_IdOrderByCreatedAtDesc(UUID monitorId);
}
