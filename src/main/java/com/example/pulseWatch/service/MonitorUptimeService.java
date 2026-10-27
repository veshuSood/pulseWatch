
package com.example.pulseWatch.service;

import com.example.pulseWatch.dto.MonitorUptimeResponse;
import com.example.pulseWatch.entity.Monitor;
import com.example.pulseWatch.entity.MonitorCheck;
import com.example.pulseWatch.repository.MonitorCheckRepository;
import com.example.pulseWatch.repository.MonitorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MonitorUptimeService {

    private final MonitorRepository monitorRepository;
    private final MonitorCheckRepository monitorCheckRepository;

    public MonitorUptimeService(
            MonitorRepository monitorRepository,
            MonitorCheckRepository monitorCheckRepository
    ) {
        this.monitorRepository = monitorRepository;
        this.monitorCheckRepository = monitorCheckRepository;
    }

    public MonitorUptimeResponse getUptime(UUID monitorId, int hours) {

        if (hours < 1 || hours > 720) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Hours must be between 1 and 720"
            );
        }

        Monitor monitor = monitorRepository.findById(monitorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Monitor not found"
                ));

        Instant startTime = Instant.now().minus(Duration.ofHours(hours));

        List<MonitorCheck> checks =
                monitorCheckRepository
                        .findByMonitor_IdAndCheckedAtGreaterThanEqual(
                                monitorId,
                                startTime
                        );

        long totalChecks = checks.size();

        long successfulChecks = checks.stream()
                .filter(MonitorCheck::isSuccessful)
                .count();

        long failedChecks = totalChecks - successfulChecks;

        double uptimePercentage = totalChecks == 0
                ? 0.0
                : (successfulChecks * 100.0) / totalChecks;

        uptimePercentage = Math.round(uptimePercentage * 100.0) / 100.0;

        return new MonitorUptimeResponse(
                monitor.getId(),
                monitor.getName(),
                hours,
                totalChecks,
                successfulChecks,
                failedChecks,
                uptimePercentage
        );
    }
}
