package com.github.mostroverkhov.r2.autoconfigure.internal.properties;

public class ServerEndpointProperties extends EndpointProperties {

  private int port;

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  @Override
  public String toString() {
    return "ServerEndpointProperties{" +
        "port=" + port +
        ", name='" + name + '\'' +
        ", transport='" + transport + '\'' +
        ", codecs=" + codecs +
        ", requesters=" + requesters +
        ", responders=" + responders +
        ", enabled=" + enabled +
        "} " + super.toString();
  }
}
