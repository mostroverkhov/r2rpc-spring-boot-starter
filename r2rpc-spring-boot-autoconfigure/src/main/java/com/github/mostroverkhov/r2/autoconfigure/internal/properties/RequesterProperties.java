package com.github.mostroverkhov.r2.autoconfigure.internal.properties;

import java.util.List;

public class RequesterProperties {
  private List<RequesterEndpointProperties> endpoints;
  private DefaultProperties defaults;

  public List<RequesterEndpointProperties> getEndpoints() {
    return endpoints;
  }

  public void setEndpoints(List<RequesterEndpointProperties> endpoints) {
    this.endpoints = endpoints;
  }

  public DefaultProperties getDefaults() {
    return defaults;
  }

  public void setDefaults(DefaultProperties defaults) {
    this.defaults = defaults;
  }
}
