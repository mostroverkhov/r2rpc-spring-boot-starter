package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.github.mostroverkhov.r2.autoconfigure.internal.R2DefaultProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "r2rpc.client")
public class R2ClientRootProperties {
  private List<R2ClientProperties> endpoints;
  private R2DefaultProperties defaults;

  public List<R2ClientProperties> getEndpoints() {
    return endpoints;
  }

  public void setEndpoints(List<R2ClientProperties> endpoints) {
    this.endpoints = endpoints;
  }

  public R2DefaultProperties getDefaults() {
    return defaults;
  }

  public void setDefaults(R2DefaultProperties defaults) {
    this.defaults = defaults;
  }
}
