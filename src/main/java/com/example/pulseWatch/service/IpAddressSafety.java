
package com.example.pulseWatch.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class IpAddressSafety {

    private IpAddressSafety() {
    }

    public static boolean isUnsafe(InetAddress address) {
        if (address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress()) {
            return true;
        }

        byte[] bytes = address.getAddress();

        if (bytes.length == 4) {
            return isRestrictedIpv4(bytes);
        }

        if (bytes.length != 16) {
            return true;
        }

        // IPv6 unique-local: fc00::/7
        if ((bytes[0] & 0xFE) == 0xFC) {
            return true;
        }

        // IPv4-compatible IPv6: ::/96
        if (isAllZeroPrefix(bytes, 12)) {
            return true;
        }

        // NAT64 well-known prefix: 64:ff9b::/96
        if ((bytes[0] & 0xFF) == 0x00
                && (bytes[1] & 0xFF) == 0x64
                && (bytes[2] & 0xFF) == 0xFF
                && (bytes[3] & 0xFF) == 0x9B
                && isRestrictedEmbeddedIpv4(bytes, 12)) {
            return true;
        }

        // 6to4: 2002::/16 — embedded IPv4 is in bytes 2–5.
        if ((bytes[0] & 0xFF) == 0x20
                && (bytes[1] & 0xFF) == 0x02
                && isRestrictedEmbeddedIpv4(bytes, 2)) {
            return true;
        }

        // IPv6 documentation prefix: 2001:db8::/32
        if ((bytes[0] & 0xFF) == 0x20
                && (bytes[1] & 0xFF) == 0x01
                && (bytes[2] & 0xFF) == 0x0D
                && (bytes[3] & 0xFF) == 0xB8) {
            return true;
        }

        // Teredo: 2001::/32
        if ((bytes[0] & 0xFF) == 0x20
                && (bytes[1] & 0xFF) == 0x01
                && bytes[2] == 0
                && bytes[3] == 0) {
            return true;
        }

        return false;
    }

    private static boolean isRestrictedEmbeddedIpv4(
            byte[] ipv6Bytes,
            int offset
    ) {
        byte[] ipv4Bytes = {
                ipv6Bytes[offset],
                ipv6Bytes[offset + 1],
                ipv6Bytes[offset + 2],
                ipv6Bytes[offset + 3]
        };

        if (isRestrictedIpv4(ipv4Bytes)) {
            return true;
        }

        try {
            InetAddress embedded = InetAddress.getByAddress(ipv4Bytes);

            return embedded.isAnyLocalAddress()
                    || embedded.isLoopbackAddress()
                    || embedded.isLinkLocalAddress()
                    || embedded.isSiteLocalAddress()
                    || embedded.isMulticastAddress();
        } catch (UnknownHostException e) {
            return true;
        }
    }

    private static boolean isRestrictedIpv4(byte[] bytes) {
        int first = bytes[0] & 0xFF;
        int second = bytes[1] & 0xFF;
        int third = bytes[2] & 0xFF;

        // 0.0.0.0/8
        if (first == 0) return true;

        // 100.64.0.0/10
        if (first == 100 && second >= 64 && second <= 127) return true;

        // 192.0.0.0/24 and 192.0.2.0/24
        if (first == 192 && second == 0 && (third == 0 || third == 2)) {
            return true;
        }

        // 198.18.0.0/15
        if (first == 198 && (second == 18 || second == 19)) return true;

        // 198.51.100.0/24
        if (first == 198 && second == 51 && third == 100) return true;

        // 203.0.113.0/24
        if (first == 203 && second == 0 && third == 113) return true;

        // 192.88.99.0/24
        if (first == 192 && second == 88 && third == 99) return true;

        // 240.0.0.0/4
        return first >= 240;
    }

    private static boolean isAllZeroPrefix(byte[] bytes, int length) {
        for (int i = 0; i < length; i++) {
            if (bytes[i] != 0) return false;
        }
        return true;
    }
}
