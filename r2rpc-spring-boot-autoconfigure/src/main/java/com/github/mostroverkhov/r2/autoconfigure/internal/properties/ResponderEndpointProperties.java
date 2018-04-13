package com.github.mostroverkhov.r2.autoconfigure.internal.properties;

public class ResponderEndpointProperties extends EndpointProperties {

  private int port;

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  @Override
  public String toString() {
    return "ResponderEndpointProperties{" +
        "name='" + name + '\'' +
        ", transport='" + transport + '\'' +
        ", port=" + port +
        ", codecs=" + codecs +
        ", api='" + api + '\'' +
        ", enabled=" + enabled +
        '}';
  }
}
