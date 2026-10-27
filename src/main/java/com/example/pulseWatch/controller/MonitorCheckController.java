package com.example.pulseWatch.controller;

import com.example.pulseWatch.dto.MonitorCheckResponse;
import com.example.pulseWatch.entity.MonitorCheck;
import com.example.pulseWatch.repository.MonitorCheckRepository;
import com.example.pulseWatch.service.MonitorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/monitors/{id}/checks")
public class MonitorCheckController {

    private final MonitorService monitorService;
    private final MonitorCheckRepository monitorCheckRepository;

    public MonitorCheckController(
            MonitorService monitorService,
            MonitorCheckRepository monitorCheckRepository
    ) {
        this.monitorService = monitorService;
        this.monitorCheckRepository = monitorCheckRepository;
    }

    @GetMapping
    public List<MonitorCheckResponse> getMonitorChecks(
            @PathVariable UUID id
    ) {
        // 1. Verify that the authenticated user owns this monitor.
        monitorService.getMonitorById(id);

        // 2. Retrieve the latest 20 checks.
        List<MonitorCheck> checks =
                monitorCheckRepository
                        .findTop20ByMonitor_IdOrderByCheckedAtDesc(id);

        // 3. Convert entities into response DTOs.
        return checks.stream()
                .map(check -> new MonitorCheckResponse(
                        check.getId(),
                        check.getCheckedAt(),
                        check.getStatusCode(),
                        check.getResponseTimeMs(),
                        check.isSuccessful()
                ))
                .toList();
    }
}
