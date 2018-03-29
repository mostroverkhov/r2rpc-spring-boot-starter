package com.github.mostroverkhov.r2.autoconfigure.internal;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "r2rpc.server.defaults")
public class R2DefaultProperties {

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
    return "R2DefaultProperties{" +
        "codecs=" + codecs +
        ", transport='" + transport + '\'' +
        '}';
  }
}
