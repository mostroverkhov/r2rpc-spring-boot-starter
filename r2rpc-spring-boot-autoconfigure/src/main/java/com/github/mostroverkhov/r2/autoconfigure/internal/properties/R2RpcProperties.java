package com.github.mostroverkhov.r2.autoconfigure.internal.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "r2rpc")
public class R2RpcProperties {
  private PeerProperties server;
  private PeerProperties client;

  public PeerProperties getServer() {
    return server;
  }

  public void setServer(PeerProperties server) {
    this.server = server;
  }

  public PeerProperties getClient() {
    return client;
  }

  public void setClient(PeerProperties client) {
    this.client = client;
  }

  @Override
  public String toString() {
    return "R2RpcProperties{" +
        "server=" + server +
        ", client=" + client +
        '}';
  }

}
