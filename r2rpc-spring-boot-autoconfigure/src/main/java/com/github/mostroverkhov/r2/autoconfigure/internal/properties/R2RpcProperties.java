package com.github.mostroverkhov.r2.autoconfigure.internal.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "r2rpc")
public class R2RpcProperties {
  private ServerProperties server;
  private ClientProperties client;

  public ServerProperties getServer() {
    return server;
  }

  public void setServer(ServerProperties server) {
    this.server = server;
  }

  public ClientProperties getClient() {
    return client;
  }

  public void setClient(ClientProperties client) {
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
