package com.github.mostroverkhov.r2.autoconfigure.internal.properties;

public class ClientEndpointProperties extends EndpointProperties {
  @Override
  public String toString() {
    return "ClientEndpointProperties{" +
        "name='" + name + '\'' +
        ", transport='" + transport + '\'' +
        ", codecs=" + codecs +
        ", requesters=" + requesters +
        ", responders=" + responders +
        ", enabled=" + enabled +
        "} " + super.toString();
  }
}
