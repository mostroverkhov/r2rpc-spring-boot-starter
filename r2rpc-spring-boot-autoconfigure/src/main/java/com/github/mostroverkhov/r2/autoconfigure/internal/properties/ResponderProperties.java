package com.github.mostroverkhov.r2.autoconfigure.internal.properties;

import java.util.List;

public class ResponderProperties {
  private List<ResponderEndpointProperties> endpoints;
  private DefaultProperties defaults;
  public List<ResponderEndpointProperties> getEndpoints() {
    return endpoints;
  }

  public void setEndpoints(List<ResponderEndpointProperties> endpoints) {
    this.endpoints = endpoints;
  }

  public DefaultProperties getDefaults() {
    return defaults;
  }

  public void setDefaults(DefaultProperties defaults) {
    this.defaults = defaults;
  }
}
