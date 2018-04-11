package com.github.mostroverkhov.r2.autoconfigure.internal;

import java.util.List;

public abstract class R2Properties {
  protected String name;
  protected String transport;
  protected List<String> codecs;
  protected List<String> api;
  protected boolean enabled = true;

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

  public List<String> getApi() {
    return api;
  }

  public void setApi(List<String> api) {
    this.api = api;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
