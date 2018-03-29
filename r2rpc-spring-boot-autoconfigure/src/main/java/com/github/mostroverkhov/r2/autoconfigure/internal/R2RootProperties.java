package com.github.mostroverkhov.r2.autoconfigure.internal;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "r2rpc.server")
public class R2RootProperties {

  private List<R2Properties> endpoints;

  public List<R2Properties> getEndpoints() {
    return endpoints;
  }

  public void setEndpoints(
      List<R2Properties> endpoints) {
    this.endpoints = endpoints;
  }
}
