package com.example.pulseWatch.dto;

import java.time.Instant;
import java.util.UUID;

public class MonitorAlertResponse {

    private UUID id;
    private String alertType;
    private String message;
    private Instant createdAt;

    public MonitorAlertResponse(
            UUID id,
            String alertType,
            String message,
            Instant createdAt
    ) {
        this.id = id;
        this.alertType = alertType;
        this.message = message;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getAlertType() {
        return alertType;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}