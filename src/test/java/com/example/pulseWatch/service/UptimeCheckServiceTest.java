
package com.example.pulseWatch.service;
import com.example.pulseWatch.repository.MonitorAlertRepository;
import org.apache.hc.client5.http.DnsResolver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import com.example.pulseWatch.entity.Monitor;
import com.example.pulseWatch.entity.MonitorCheck;
import com.example.pulseWatch.repository.MonitorCheckRepository;
import com.example.pulseWatch.repository.MonitorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UptimeCheckServiceTest {

    private static class LocalTestDnsResolver implements DnsResolver {

        @Override
        public InetAddress[] resolve(String host)
                throws UnknownHostException {

            if ("127.0.0.1".equals(host)) {
                return new InetAddress[]{
                        InetAddress.getByAddress(
                                new byte[]{127, 0, 0, 1}
                        )
                };
            }

            throw new UnknownHostException(
                    "Test resolver permits only 127.0.0.1"
            );
        }

        @Override
        public String resolveCanonicalHostname(String host)
                throws UnknownHostException {

            if ("127.0.0.1".equals(host)) {
                return "127.0.0.1";
            }

            throw new UnknownHostException(
                    "Test resolver permits only 127.0.0.1"
            );
        }
    }


    @Autowired
    private UptimeCheckService uptimeCheckService;

    @Autowired
    private MonitorRepository monitorRepository;

    @Autowired
    private MonitorCheckRepository monitorCheckRepository;
    private MonitorAlertRepository monitorAlertRepository;
    private MockWebServer mockWebServer;
    private final UrlValidationService service = new UrlValidationService();

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(
                java.net.InetAddress.getByName("127.0.0.1"),
                0
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
    @Test
    void shouldNotFollowRedirects() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(302)
                        .addHeader(
                                "Location",
                                "http://localhost:8080/internal"
                        )
        );

        String url = "http://127.0.0.1:"
                + mockWebServer.getPort()
                + "/redirect";

        UptimeCheckService localTestService =
                new UptimeCheckService(
                        monitorCheckRepository,
                        monitorAlertRepository,
                        new LocalTestDnsResolver()
                );

        var response = localTestService.testWebsite(url);

        assertEquals(302, response.getStatusCode().value());
        assertEquals(1, mockWebServer.getRequestCount());
    }

    @Test
    void shouldSaveWebsiteCheckToDatabase() {
        // Find an existing monitor to check.
        Monitor monitor = monitorRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No monitor found. Create a monitor first."
                ));

        // Perform the HTTP check and save its result.
        uptimeCheckService.checkWebsite(monitor);

        // Verify that a check was persisted for this monitor.
        MonitorCheck savedCheck = monitorCheckRepository.findAll()
                .stream()
                .filter(check ->
                        check.getMonitor().getId()
                                .equals(monitor.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "No website check was saved."
                ));

        assertNotNull(savedCheck.getId());
        assertNotNull(savedCheck.getCheckedAt());
        assertNotNull(savedCheck.getResponseTimeMs());
        assertTrue(savedCheck.getResponseTimeMs() >= 0);
    }
    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            "http://0.0.0.0",
            "http://127.0.0.1",
            "http://[::1]",
            "http://[fc00::1]",
            "http://[::ffff:127.0.0.1]",
            "http://localhost.",
            "http://LOCALHOST",
            "http://2130706433",
            "http://0x7f000001",
            "ftp://example.com",
            "http://user:pass@8.8.8.8",
            "http://169.254.169.254/latest/meta-data",
            // these expose the policy mismatch:
            "http://100.64.0.1",
            "http://198.18.0.1",
            "http://[::127.0.0.1]",
            "http://[64:ff9b::7f00:1]",
            "http://[2002:7f00:1::]"
    })
    void shouldRejectUnsafeUrls(String url) {
        assertThrows(ResponseStatusException.class, () -> service.validate(url));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = { "http://8.8.8.8", "https://8.8.8.8:8443/health" })
    void shouldAcceptPublicUrls(String url) {
        assertDoesNotThrow(() -> service.validate(url));
    }
}
