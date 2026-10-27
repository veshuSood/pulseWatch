
package com.example.pulseWatch.controller;

import com.example.pulseWatch.dto.MonitorUptimeResponse;
import com.example.pulseWatch.service.MonitorService;
import com.example.pulseWatch.service.MonitorUptimeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/monitors/{id}/uptime")
public class MonitorUptimeController {

    private final MonitorUptimeService monitorUptimeService;
    private final MonitorService monitorService;

    public MonitorUptimeController(
            MonitorUptimeService monitorUptimeService,
            MonitorService monitorService
    ) {
        this.monitorUptimeService = monitorUptimeService;
        this.monitorService = monitorService;
    }

    @GetMapping
    public MonitorUptimeResponse getUptime(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "24") int hours
    ) {
        // Verify that the authenticated user owns this monitor.
        monitorService.getMonitorById(id);

        return monitorUptimeService.getUptime(id, hours);
    }
}
