package com.example.pulseWatch.controller;

import com.example.pulseWatch.dto.MonitorAlertResponse;
import com.example.pulseWatch.service.MonitorAlertService;
import com.example.pulseWatch.service.MonitorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/monitors/{monitorId}/alerts")
public class MonitorAlertController {

    private final MonitorAlertService monitorAlertService;
    private final MonitorService monitorService;

    public MonitorAlertController(
            MonitorAlertService monitorAlertService,
            MonitorService monitorService
    ) {
        this.monitorAlertService = monitorAlertService;
        this.monitorService=monitorService;
    }

    @GetMapping
    public List<MonitorAlertResponse> getAlerts(
            @PathVariable UUID monitorId
    ) {
        monitorService.getMonitorById(monitorId);
        return monitorAlertService.getAlerts(monitorId);
    }
}