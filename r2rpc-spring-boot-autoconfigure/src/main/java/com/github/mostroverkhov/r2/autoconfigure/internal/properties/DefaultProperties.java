package com.github.mostroverkhov.r2.autoconfigure.internal.properties;

import java.util.List;

public class DefaultProperties {

  private List<String> codecs;
  private String transport;

  public List<String> getCodecs() {
    return codecs;
  }

  public void setCodecs(List<String> codecs) {
    this.codecs = codecs;
  }

  public String getTransport() {
    return transport;
  }

  public void setTransport(String transport) {
    this.transport = transport;
  }

  @Override
  public String toString() {
    return "DefaultProperties{" +
        "codecs=" + codecs +
        ", transport='" + transport + '\'' +
        '}';
  }
}
