package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.github.mostroverkhov.r2.autoconfigure.client.R2ClientTransport;
import com.github.mostroverkhov.r2.autoconfigure.internal.client.apihandlers.ClientApiHandlersFactory;
import com.github.mostroverkhov.r2.core.DataCodec;
import io.rsocket.RSocketFactory;

import java.util.List;

public class ClientConfig {
  private final String name;
  private final DataCodec dataCodec;
  private final R2ClientTransport r2ClientTransport;
  private final RSocketFactory.ClientRSocketFactory clientRSocketFactory;
  private final List<Class<?>> apis;
  private final ClientApiHandlersFactory clientApiHandlersFactory;

  public ClientConfig(String name,
                      DataCodec dataCodec,
                      R2ClientTransport r2ClientTransport,
                      RSocketFactory.ClientRSocketFactory clientRSocketFactory,
                      List<Class<?>> apis,
                      ClientApiHandlersFactory clientApiHandlersFactory) {
    this.name = name;
    this.dataCodec = dataCodec;
    this.r2ClientTransport = r2ClientTransport;
    this.clientRSocketFactory = clientRSocketFactory;
    this.apis = apis;
    this.clientApiHandlersFactory = clientApiHandlersFactory;
  }

  public String name() {
    return name;
  }

  public DataCodec dataCodec() {
    return dataCodec;
  }

  public R2ClientTransport transportFactory() {
    return r2ClientTransport;
  }

  public RSocketFactory.ClientRSocketFactory rSocketFactory() {
    return clientRSocketFactory;
  }

  public List<Class<?>> apis() {
    return apis;
  }

  public ClientApiHandlersFactory clientApiHandlersFactory() {
    return clientApiHandlersFactory;
  }
}
