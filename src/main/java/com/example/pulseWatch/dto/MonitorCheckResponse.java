package com.example.pulseWatch.dto;

import java.time.Instant;
import java.util.UUID;

public class MonitorCheckResponse {
    private UUID id;
    private Instant checkedAt;
    private Integer statusCode;
    private Long responseTimeMs;
    private boolean successful;
    public MonitorCheckResponse(
            UUID id,
            Instant checkedAt,
            Integer statusCode,
            Long responseTimeMs,
            boolean successful
    ) {
        this.id = id;
        this.checkedAt = checkedAt;
        this.statusCode = statusCode;
        this.responseTimeMs = responseTimeMs;
        this.successful = successful;
    }

    public Instant getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(Instant checkedAt) {
        this.checkedAt = checkedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
