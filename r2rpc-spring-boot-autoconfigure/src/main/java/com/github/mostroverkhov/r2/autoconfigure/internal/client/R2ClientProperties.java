package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.github.mostroverkhov.r2.autoconfigure.internal.R2Properties;

public class R2ClientProperties extends R2Properties {

  @Override
  public String toString() {
    return "R2ClientProperties{" +
        "name='" + name + '\'' +
        ", transport='" + transport + '\'' +
        ", codecs=" + codecs +
        ", api=" + api +
        ", enabled=" + enabled +
        '}';
  }
}
