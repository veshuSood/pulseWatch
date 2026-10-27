package com.example.pulseWatch.service;

import com.example.pulseWatch.dto.MonitorAlertResponse;
import com.example.pulseWatch.entity.MonitorAlert;
import com.example.pulseWatch.repository.MonitorAlertRepository;
import com.example.pulseWatch.repository.MonitorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class MonitorAlertService {

    private final MonitorAlertRepository monitorAlertRepository;
    private final MonitorRepository monitorRepository;

    public MonitorAlertService(
            MonitorAlertRepository monitorAlertRepository,
            MonitorRepository monitorRepository
    ) {
        this.monitorAlertRepository = monitorAlertRepository;
        this.monitorRepository = monitorRepository;
    }

    public List<MonitorAlertResponse> getAlerts(UUID monitorId) {

        if (!monitorRepository.existsById(monitorId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Monitor not found"
            );
        }

        List<MonitorAlert> alerts =
                monitorAlertRepository
                        .findTop20ByMonitor_IdOrderByCreatedAtDesc(monitorId);

        return alerts.stream()
                .map(alert -> new MonitorAlertResponse(
                        alert.getId(),
                        alert.getAlertType(),
                        alert.getMessage(),
                        alert.getCreatedAt()
                ))
                .toList();
    }
}