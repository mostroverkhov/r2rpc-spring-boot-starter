package com.github.mostroverkhov.r2.autoconfigure.internal.server;

import java.util.List;

import com.github.mostroverkhov.r2.autoconfigure.internal.R2DefaultProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "r2rpc.server")
public class R2ServerRootProperties {

  private List<R2ServerProperties> endpoints;
  private R2DefaultProperties defaults;
  public List<R2ServerProperties> getEndpoints() {
    return endpoints;
  }

  public void setEndpoints(List<R2ServerProperties> endpoints) {
    this.endpoints = endpoints;
  }

  public R2DefaultProperties getDefaults() {
    return defaults;
  }

  public void setDefaults(R2DefaultProperties defaults) {
    this.defaults = defaults;
  }
}
