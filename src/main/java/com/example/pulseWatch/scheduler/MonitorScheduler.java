package com.example.pulseWatch.scheduler;

import com.example.pulseWatch.entity.Monitor;
import com.example.pulseWatch.repository.MonitorCheckRepository;
import com.example.pulseWatch.repository.MonitorRepository;
import com.example.pulseWatch.service.UptimeCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MonitorScheduler {

    private final MonitorRepository monitorRepository;
    private final UptimeCheckService uptimeCheckService;
    private final MonitorCheckRepository monitorCheckRepository;
    private static final Logger log =
            LoggerFactory.getLogger(MonitorScheduler.class);

    public MonitorScheduler(
            MonitorRepository monitorRepository,
            UptimeCheckService uptimeCheckService,
            MonitorCheckRepository monitorCheckRepository
            ) {
        this.monitorRepository = monitorRepository;
        this.uptimeCheckService = uptimeCheckService;
        this.monitorCheckRepository=monitorCheckRepository;
    }

    @Scheduled(fixedDelay = 5000)
    public void checkMonitors() {
        var monitors = monitorRepository.findAll();

        log.info("Scheduled monitoring started. Found {} monitors.",
                monitors.size());

        for (Monitor monitor : monitors) {
            if (!monitor.isEnabled()) {
                continue;
            }

            try {
                var latestCheck =
                        monitorCheckRepository
                                .findTopByMonitor_IdOrderByCheckedAtDesc(
                                        monitor.getId()
                                );

                if (latestCheck.isEmpty()) {
                    uptimeCheckService.checkWebsite(monitor);
                    continue;
                }

                var lastCheckedAt =
                        latestCheck.get().getCheckedAt();

                var secondsSinceLastCheck =
                        java.time.Duration.between(
                                lastCheckedAt,
                                java.time.Instant.now()
                        ).getSeconds();

                if (secondsSinceLastCheck
                        >= monitor.getCheck_interval_seconds()) {
                    uptimeCheckService.checkWebsite(monitor);
                }

            } catch (Exception e) {
                log.error("Failed to check monitor: {}",
                        monitor.getId(), e);
            }
        }
    }
}