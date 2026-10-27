
package com.example.pulseWatch.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UrlValidationServiceTest {

    private final UrlValidationService urlValidationService =
            new UrlValidationService();

    @Test
    void shouldRejectLocalhost() {
        assertThrows(
                ResponseStatusException.class,
                () -> urlValidationService.validate(
                        "http://localhost:8080"
                )
        );
    }

    @Test
    void shouldRejectPrivateIpAddress() {
        assertThrows(
                ResponseStatusException.class,
                () -> urlValidationService.validate(
                        "http://192.168.1.1"
                )
        );
    }
}
