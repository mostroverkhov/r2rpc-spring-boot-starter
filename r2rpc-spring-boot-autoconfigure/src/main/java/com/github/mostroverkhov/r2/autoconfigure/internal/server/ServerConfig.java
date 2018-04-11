package com.github.mostroverkhov.r2.autoconfigure.internal.server;

import com.github.mostroverkhov.r2.core.DataCodec;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import io.rsocket.Closeable;
import io.rsocket.RSocketFactory.ServerRSocketFactory;
import io.rsocket.transport.ServerTransport;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ServerConfig {

  private final String name;
  private final ServerRSocketFactory rSocketFactory;
  private final ServerTransport<Closeable> transport;
  private final List<DataCodec> codecs;
  private final Function<ConnectionContext, Collection<Object>> handlers;

  public ServerConfig(String name,
      ServerRSocketFactory rSocketFactory,
      ServerTransport<Closeable> transport,
      List<DataCodec> codecs,
      Function<ConnectionContext, Collection<Object>> handlers) {
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

  public Function<ConnectionContext, Collection<Object>> handlers() {
    return handlers;
  }
}
