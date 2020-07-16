package com.github.njuro.jard.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.springframework.stereotype.Component;

/** Component for checking if IP is local. Used for bypassing API rate limiting. */
@Component
public class IPChecker {

  /**
   * Checks if given IP address belongs to local server.
   *
   * @param ip IP address to check
   * @return true if given IP is local, false otherwise
   */
  public boolean isLocalAddress(String ip) {
    try {
      InetAddress address = InetAddress.getByName(ip);
      if (address.isAnyLocalAddress() || address.isLoopbackAddress()) {
        return true;
      }

      return NetworkInterface.getByInetAddress(address) != null;
    } catch (SocketException | UnknownHostException ex) {
      return false;
    }
  }
}
