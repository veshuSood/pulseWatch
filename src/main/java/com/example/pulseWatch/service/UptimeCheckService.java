package com.example.pulseWatch.service;

import com.example.pulseWatch.entity.Monitor;
import com.example.pulseWatch.entity.MonitorAlert;
import com.example.pulseWatch.entity.MonitorCheck;
import com.example.pulseWatch.repository.MonitorAlertRepository;
import com.example.pulseWatch.repository.MonitorCheckRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import java.time.Instant;
import java.util.Optional;

@Service
public class UptimeCheckService {

    private final MonitorCheckRepository monitorCheckRepository;
    private final MonitorAlertRepository monitorAlertRepository;
    private final RestClient restClient;//for fecthing url
    private static final Logger log =
            LoggerFactory.getLogger(UptimeCheckService.class);//for exact time


    @Autowired
    public UptimeCheckService(
            MonitorCheckRepository monitorCheckRepository,
            MonitorAlertRepository monitorAlertRepository,
            RestClient uptimeRestClient
    ) {
        this.monitorCheckRepository = monitorCheckRepository;
        this.monitorAlertRepository = monitorAlertRepository;
        this.restClient = uptimeRestClient;
    }


    public ResponseEntity<String> testWebsite(String url) {

        return restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            // Preserve HTTP error responses.
                        }
                )
                .toEntity(String.class);
    }


    UptimeCheckService(
            MonitorCheckRepository monitorCheckRepository,
            MonitorAlertRepository monitorAlertRepository,
            org.apache.hc.client5.http.DnsResolver dnsResolver
    ) {
        this.monitorCheckRepository = monitorCheckRepository;
        this.monitorAlertRepository = monitorAlertRepository;

        var connectionManager =
                org.apache.hc.client5.http.impl.io
                        .PoolingHttpClientConnectionManagerBuilder.create()
                        .setDnsResolver(dnsResolver)
                        .build();

        var httpClient = org.apache.hc.client5.http.impl.classic
                .HttpClients.custom()
                .setConnectionManager(connectionManager)
                .disableRedirectHandling()
                .disableAutomaticRetries()
                .build();

        var requestFactory =
                new org.springframework.http.client
                        .HttpComponentsClientHttpRequestFactory(httpClient);

        requestFactory.setConnectionRequestTimeout(
                java.time.Duration.ofSeconds(5)
        );
        requestFactory.setReadTimeout(
                java.time.Duration.ofSeconds(5)
        );

        this.restClient = org.springframework.web.client.RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }


    @Transactional
    public void checkWebsite(Monitor monitor) {

        long startTime = System.currentTimeMillis();

        // Capture the previous check before saving the new one.
        Optional<MonitorCheck> previousCheck =
                monitorCheckRepository
                        .findTopByMonitor_IdOrderByCheckedAtDesc(monitor.getId());

        MonitorCheck check = new MonitorCheck();
        check.setMonitor(monitor);

        try {
            ResponseEntity<String> response = testWebsite(monitor.getUrl());

            check.setStatusCode(response.getStatusCode().value());
            check.setSuccessful(
                    response.getStatusCode().is2xxSuccessful()
            );

        } catch (ResourceAccessException e) {
            // Network failure or connection timeout.
            check.setStatusCode(null);
            check.setSuccessful(false);
        }

        check.setResponseTimeMs(
                System.currentTimeMillis() - startTime
        );

        check.setCheckedAt(Instant.now());
        log.info(
                "Website check completed: name={}, status={}, responseTimeMs={}, checkedAt={}",
                monitor.getName(),
                check.isSuccessful() ? "UP" : "DOWN",
                check.getResponseTimeMs(),
                check.getCheckedAt()
        );

        // Persist the new check first.
        monitorCheckRepository.save(check);
        log.info(
                "Monitor check saved: monitorId={}, checkedAt={}",
                monitor.getId(),
                check.getCheckedAt()
        );

        // Generate alerts only when the state changes.

        boolean isSuccessful = check.isSuccessful();

        Optional<MonitorAlert> latestAlert =
                monitorAlertRepository
                        .findTopByMonitor_IdOrderByCreatedAtDesc(monitor.getId());

        if (!isSuccessful) {
            // The website is DOWN.
            // Create an alert only if there is no active DOWN alert.
            if (latestAlert.isEmpty()
                    || "RECOVERY".equals(latestAlert.get().getAlertType())) {

                createAlert(
                        monitor,
                        "DOWN",
                        "Monitor " + monitor.getName() + " is DOWN."
                );

                System.out.println("ALERT: DOWN alert created for " + monitor.getName());
            }
        } else {
            // The website is UP.
            // Create a recovery alert only if its latest alert was DOWN.
            if (latestAlert.isPresent()
                    && "DOWN".equals(latestAlert.get().getAlertType())) {

                createAlert(
                        monitor,
                        "RECOVERY",
                        "Monitor " + monitor.getName() + " has recovered."
                );

                System.out.println("ALERT: RECOVERY alert created for " + monitor.getName());
            }
        }

    }

    private void createAlert(
            Monitor monitor,
            String alertType,
            String message
    ) {
        MonitorAlert alert = new MonitorAlert();
        alert.setMonitor(monitor);
        alert.setAlertType(alertType);
        alert.setMessage(message);
        alert.setCreatedAt(Instant.now());

        System.out.println("DEBUG saving alert: " + alertType);

       MonitorAlert savedAlert= monitorAlertRepository.save(alert);

        System.out.println("DEBUG saved alert ID: " + savedAlert.getId());
    }
}
