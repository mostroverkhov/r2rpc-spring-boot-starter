package com.github.mostroverkhov.r2.autoconfigure.internal.server;

import com.github.mostroverkhov.r2.autoconfigure.internal.server.apihandlers.ServerApiHandlersFactory;
import com.github.mostroverkhov.r2.core.DataCodec;
import io.rsocket.Closeable;
import io.rsocket.RSocketFactory.ServerRSocketFactory;
import io.rsocket.transport.ServerTransport;

import java.util.List;

public class ServerConfig {
  private final String name;
  private final ServerRSocketFactory rSocketFactory;
  private final ServerTransport<Closeable> transport;
  private final List<DataCodec> codecs;
  private final ServerApiHandlersFactory handlers;

  public ServerConfig(String name,
      ServerRSocketFactory rSocketFactory,
      ServerTransport<Closeable> transport,
      List<DataCodec> codecs,
      ServerApiHandlersFactory handlers) {
    this.name = name;
    this.rSocketFactory = rSocketFactory;
    this.transport = transport;
    this.codecs = codecs;
    this.handlers = handlers;
  }

  public String getName() {
    return name;
  }

  public ServerRSocketFactory rSocketFactory() {
    return rSocketFactory;
  }

  public ServerTransport<Closeable> transport() {
    return transport;
  }

  public List<DataCodec> codecs() {
    return codecs;
  }

  public ServerApiHandlersFactory handlers() {
    return handlers;
  }
}
