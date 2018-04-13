package com.github.mostroverkhov.r2.autoconfigure.internal.properties;

public class RequesterEndpointProperties extends EndpointProperties {

  @Override
  public String toString() {
    return "RequesterEndpointProperties{" +
        "name='" + name + '\'' +
        ", transport='" + transport + '\'' +
        ", codecs=" + codecs +
        ", api=" + api +
        ", enabled=" + enabled +
        '}';
  }
}
