package com.example.pulseWatch.controller;

import com.example.pulseWatch.dto.MonitorStatusResponse;
import com.example.pulseWatch.service.MonitorStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/monitors/{id}/status")
public class MonitorStatusController {

    private final MonitorStatusService monitorStatusService;

    public MonitorStatusController(
            MonitorStatusService monitorStatusService
    ) {
        this.monitorStatusService = monitorStatusService;
    }

    @GetMapping
    public MonitorStatusResponse getMonitorStatus(
            @PathVariable UUID id
    ) {
        return monitorStatusService.getMonitorStatus(id);
    }
}