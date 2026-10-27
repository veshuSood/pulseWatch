
package com.example.pulseWatch.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class SafeDnsResolverTest {

    private final SafeDnsResolver resolver = new SafeDnsResolver();

    @Test
    void shouldRejectLoopbackAddress() {
        assertThrows(
                UnknownHostException.class,
                () -> resolver.resolve("127.0.0.1")
        );
    }

    @Test
    void shouldRejectPrivateAddress() {
        assertThrows(
                UnknownHostException.class,
                () -> resolver.resolve("192.168.1.1")
        );
    }

    @Test
    void shouldRejectIpv6LoopbackAddress() {
        assertThrows(
                UnknownHostException.class,
                () -> resolver.resolve("::1")
        );
    }
    @Test
    void shouldRejectBenchmarkingAddressRange() {
        assertThrows(
                UnknownHostException.class,
                () -> resolver.resolve("198.18.0.1")
        );
    }

    @Test
    void shouldRejectSharedAddressSpace() {
        assertThrows(
                UnknownHostException.class,
                () -> resolver.resolve("100.64.0.1")
        );
    }

    @Test
    void shouldRejectDocumentationAddressRange() {
        assertThrows(
                UnknownHostException.class,
                () -> resolver.resolve("203.0.113.1")
        );
    }
    @Test
    void shouldRejectDeprecated6to4RelayAddress()
            throws Exception {
        InetAddress address =
                InetAddress.getByName("192.88.99.1");

        assertThrows(
                UnknownHostException.class,
                () -> resolver.resolve("192.88.99.1")
        );
    }
    @Test
    void shouldRejectLastAddressInDeprecated6to4RelayRange()
            throws Exception {
        assertThrows(
                UnknownHostException.class,
                () -> resolver.resolve("192.88.99.255")
        );
    }
    @Test
    void shouldRejectIpv4LimitedBroadcastAddress() {
        assertThrows(
                UnknownHostException.class,
                () -> resolver.resolve("255.255.255.255")
        );
    }
    @Test
    void shouldRejectIpv6MulticastAddress() throws Exception {
        assertThrows(
                UnknownHostException.class,
                () -> resolver.resolve("ff02::1")
        );
    }
    @Test
    void shouldRejectIpv6LinkLocalAddress() {
        assertThrows(
                UnknownHostException.class,
                () -> resolver.resolve("fe80::1")
        );
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "::127.0.0.1",          // IPv4-compatible, embeds 127.0.0.1
            "64:ff9b::7f00:1",      // NAT64, embeds 127.0.0.1
            "64:ff9b::a00:1",       // NAT64, embeds 10.0.0.1
            "2002:7f00:1::",        // 6to4, embeds 127.0.0.1
            "2002:c0a8:101::",      // 6to4, embeds 192.168.1.1
            "::ffff:127.0.0.1",     // IPv4-mapped
            "0:0:0:0:0:ffff:7f00:1",// same address, long form
            "2001:db8::1",          // IPv6 documentation range
            "2001::1"               // Teredo prefix
    })
    void shouldRejectRestrictedIpv6Forms(String host) {
        assertThrows(UnknownHostException.class, () -> resolver.resolve(host));
    }
    @ParameterizedTest
    @ValueSource(strings = { "8.8.8.8", "2606:4700:4700::1111" })
    void shouldAllowPublicAddresses(String host) {
        assertDoesNotThrow(() -> resolver.resolve(host));
    }


}
