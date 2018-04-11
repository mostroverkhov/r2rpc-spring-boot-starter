package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.github.mostroverkhov.r2.autoconfigure.client.R2ClientConnector;

import java.util.Map;
import java.util.function.Supplier;

public class ClientConnectors {
  private final Map<String, Supplier<R2ClientConnector>> endpoints;

  public ClientConnectors(Map<String, Supplier<R2ClientConnector>> endpoints) {
    this.endpoints = endpoints;
  }

  public R2ClientConnector name(String endpoint) {
    Supplier<R2ClientConnector> conn = endpoints.get(endpoint);
    if (conn == null) {
      String msg = "Unknown endpoint: %s. Supported: %s";
      throw new IllegalArgumentException(String.format(
          msg, endpoint,
          endpoints.keySet()));
    }
    return conn.get();
  }
}
