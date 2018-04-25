package com.github.mostroverkhov.r2.autoconfigure.internal.properties;

import java.util.List;

public abstract class PeerProperties<T extends EndpointProperties> {
  private DefaultProperties defaults;
  private List<T> endpoints;

  public DefaultProperties getDefaults() {
    return defaults;
  }

  public void setDefaults(DefaultProperties defaults) {
    this.defaults = defaults;
  }

  public List<T> getEndpoints() {
    return endpoints;
  }

  public void setEndpoints(List<T> endpoints) {
    this.endpoints = endpoints;
  }
}
