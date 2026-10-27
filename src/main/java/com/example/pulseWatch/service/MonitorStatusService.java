package com.example.pulseWatch.service;

import com.example.pulseWatch.dto.MonitorStatusResponse;
import com.example.pulseWatch.entity.Monitor;
import com.example.pulseWatch.entity.MonitorCheck;
import com.example.pulseWatch.repository.MonitorCheckRepository;
import com.example.pulseWatch.repository.MonitorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class MonitorStatusService {

    private final MonitorRepository monitorRepository;
    private final MonitorCheckRepository monitorCheckRepository;

    public MonitorStatusService(
            MonitorCheckRepository monitorCheckRepository,
            MonitorRepository monitorRepository
    ) {
        this.monitorCheckRepository = monitorCheckRepository;
        this.monitorRepository = monitorRepository;
    }

    public MonitorStatusResponse getMonitorStatus(UUID monitorId) {

        // 1. Find the monitor
        Monitor monitor = monitorRepository.findById(monitorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Monitor not found"
                ));

        // 2. Find the latest check
        var latestCheck = monitorCheckRepository
                .findTopByMonitor_IdOrderByCheckedAtDesc(monitorId);

        // 3. If no check exists, return PENDING
        if (latestCheck.isEmpty()) {
            return new MonitorStatusResponse(
                    monitor.getId(),
                    monitor.getName(),
                    "PENDING",
                    null,
                    null,
                    null
            );
        }

        // 4. Determine the current status
        MonitorCheck check = latestCheck.get();

        String currentStatus = check.isSuccessful() ? "UP" : "DOWN";

        // 5. Return the response DTO
        return new MonitorStatusResponse(
                monitor.getId(),
                monitor.getName(),
                currentStatus,
                check.getCheckedAt(),
                check.getResponseTimeMs(),
                check.getStatusCode()
        );
    }
}