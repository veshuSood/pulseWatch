
package com.example.pulseWatch.service;

import org.apache.hc.client5.http.DnsResolver;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class SafeDnsResolver implements DnsResolver {

    @Override
    public InetAddress[] resolve(String host)
            throws UnknownHostException {

        InetAddress[] addresses = InetAddress.getAllByName(host);

        if (addresses.length == 0) {
            throw new UnknownHostException(
                    "No IP addresses found for host"
            );
        }

        for (InetAddress address : addresses) {
            if (IpAddressSafety.isUnsafe(address)) {
                throw new UnknownHostException(
                        "Host resolves to a restricted IP address"
                );
            }
        }

        return addresses;
    }


    @Override
    public String resolveCanonicalHostname(String host)
            throws UnknownHostException {
        return InetAddress.getByName(host).getCanonicalHostName();
    }






}
