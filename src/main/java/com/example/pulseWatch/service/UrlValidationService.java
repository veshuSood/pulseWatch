
package com.example.pulseWatch.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Locale;

@Service
public class UrlValidationService {

    public URI validate(String url) {
        final URI uri;

        try {
            uri = URI.create(url.trim()).parseServerAuthority();
        } catch (IllegalArgumentException | java.net.URISyntaxException e) {
            throw badRequest("Invalid URL");
        }

        String scheme = uri.getScheme();
        String host = uri.getHost();

        if (scheme == null ||
                !(scheme.equalsIgnoreCase("http")
                        || scheme.equalsIgnoreCase("https"))) {
            throw badRequest("Only HTTP and HTTPS URLs are allowed");
        }

        if (host == null || host.isBlank()) {
            throw badRequest("URL must contain a valid hostname");
        }

        if (uri.getUserInfo() != null) {
            throw badRequest("URLs containing credentials are not allowed");
        }

        if (uri.getPort() < -1 || uri.getPort() > 65535) {
            throw badRequest("Invalid port");
        }

        host = host.toLowerCase(Locale.ROOT);

        if (host.equals("localhost") || host.endsWith(".localhost")
                || host.endsWith(".local")) {
            throw badRequest("Local hostnames are not allowed");
        }

        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);

            if (addresses.length == 0) {
                throw badRequest("Hostname could not be resolved");
            }

            for (InetAddress address : addresses) {
                if (IpAddressSafety.isUnsafe(address)) {
                    throw new UnknownHostException(
                            "Host resolves to a restricted IP address"
                    );
                }
            }
        } catch (UnknownHostException e) {
            throw badRequest("Hostname could not be resolved");
        }
        return uri;
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                message
        );
    }
}
