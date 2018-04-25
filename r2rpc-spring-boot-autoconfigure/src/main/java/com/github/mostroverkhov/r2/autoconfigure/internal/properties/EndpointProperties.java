package com.github.mostroverkhov.r2.autoconfigure.internal.properties;

import java.util.List;

public abstract class EndpointProperties {
  String name;
  String transport;
  List<String> codecs;
  List<String> requesters;
  List<String> responders;
  boolean enabled = true;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTransport() {
    return transport;
  }

  public void setTransport(String transport) {
    this.transport = transport;
  }

  public List<String> getCodecs() {
    return codecs;
  }

  public void setCodecs(List<String> codecs) {
    this.codecs = codecs;
  }

  public List<String> getResponders() {
    return responders;
  }

  public void setResponders(List<String> responders) {
    this.responders = responders;
  }

  public List<String> getRequesters() {
    return requesters;
  }

  public void setRequesters(List<String> requesters) {
    this.requesters = requesters;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
