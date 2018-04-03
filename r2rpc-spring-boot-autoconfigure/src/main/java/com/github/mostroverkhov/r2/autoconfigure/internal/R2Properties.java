package com.github.mostroverkhov.r2.autoconfigure.internal;

import java.util.List;

public class R2Properties {

  private String name;
  private String transport;
  private int port;
  private List<String> codecs;
  private List<String> api;
  private boolean enabled = true;

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

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
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

  R2Properties copy() {
    R2Properties r2Properties = new R2Properties();
    r2Properties.setPort(port);
    r2Properties.setTransport(transport);
    r2Properties.setApi(api);
    r2Properties.setCodecs(codecs);
    r2Properties.setName(name);
    r2Properties.setEnabled(enabled);
    return r2Properties;
  }

  @Override
  public String toString() {
    return "R2Properties{" +
        "name='" + name + '\'' +
        ", transport='" + transport + '\'' +
        ", port=" + port +
        ", codecs=" + codecs +
        ", api='" + api + '\'' +
        ", enabled=" + enabled +
        '}';
  }
}
