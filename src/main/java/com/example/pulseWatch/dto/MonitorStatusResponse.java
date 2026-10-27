package com.example.pulseWatch.dto;

import java.time.Instant;
import java.util.UUID;

public class MonitorStatusResponse {

    private UUID monitorId;
    private String name;
    private String currentStatus;
    private Instant lastCheckedAt;
    private Long responseTimeMs;
    private Integer statusCode;

    public MonitorStatusResponse(
            UUID monitorId,
            String name,
            String currentStatus,
            Instant lastCheckedAt,
            Long responseTimeMs,
            Integer statusCode
    ) {
        this.monitorId = monitorId;
        this.name = name;
        this.currentStatus = currentStatus;
        this.lastCheckedAt = lastCheckedAt;
        this.responseTimeMs = responseTimeMs;
        this.statusCode = statusCode;
    }

    public UUID getMonitorId() {
        return monitorId;
    }

    public String getName() {
        return name;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public Instant getLastCheckedAt() {
        return lastCheckedAt;
    }

    public Long getResponseTimeMs() {
        return responseTimeMs;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}