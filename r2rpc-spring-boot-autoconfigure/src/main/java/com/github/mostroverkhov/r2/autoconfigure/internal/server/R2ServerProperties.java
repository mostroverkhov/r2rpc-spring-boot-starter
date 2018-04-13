package com.github.mostroverkhov.r2.autoconfigure.internal.server;

import com.github.mostroverkhov.r2.autoconfigure.internal.R2Properties;

public class R2ServerProperties extends R2Properties {

  private int port;

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  @Override
  public String toString() {
    return "R2ServerProperties{" +
        "name='" + name + '\'' +
        ", transport='" + transport + '\'' +
        ", port=" + port +
        ", codecs=" + codecs +
        ", api='" + api + '\'' +
        ", enabled=" + enabled +
        '}';
  }
}
