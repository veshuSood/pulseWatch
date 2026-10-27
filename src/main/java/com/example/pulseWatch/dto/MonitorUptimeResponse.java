package com.example.pulseWatch.dto;

import java.util.UUID;

public class MonitorUptimeResponse {

    private UUID monitorId;
    private String name;
    private int periodHours;
    private long totalChecks;
    private long successfulChecks;
    private long failedChecks;
    private double uptimePercentage;

    public MonitorUptimeResponse(
            UUID monitorId,
            String name,
            int periodHours,
            long totalChecks,
            long successfulChecks,
            long failedChecks,
            double uptimePercentage
    ) {
        this.monitorId = monitorId;
        this.name = name;
        this.periodHours = periodHours;
        this.totalChecks = totalChecks;
        this.successfulChecks = successfulChecks;
        this.failedChecks = failedChecks;
        this.uptimePercentage = uptimePercentage;
    }

    public UUID getMonitorId() {
        return monitorId;
    }

    public String getName() {
        return name;
    }

    public int getPeriodHours() {
        return periodHours;
    }

    public long getTotalChecks() {
        return totalChecks;
    }

    public long getSuccessfulChecks() {
        return successfulChecks;
    }

    public long getFailedChecks() {
        return failedChecks;
    }

    public double getUptimePercentage() {
        return uptimePercentage;
    }
}