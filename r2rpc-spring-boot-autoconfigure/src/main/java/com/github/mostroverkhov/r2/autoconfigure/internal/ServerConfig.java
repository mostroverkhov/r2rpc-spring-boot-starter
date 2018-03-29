package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.core.DataCodec;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import io.rsocket.Closeable;
import io.rsocket.RSocketFactory.ServerRSocketFactory;
import io.rsocket.transport.ServerTransport;
import java.util.List;
import java.util.function.Function;

class ServerConfig {

  private final ServerRSocketFactory rSocketFactory;
  private final ServerTransport<Closeable> transport;
  private final List<DataCodec> codecs;
  private final Function<ConnectionContext, List<Object>> handlers;

  public ServerConfig(ServerRSocketFactory rSocketFactory,
      ServerTransport<Closeable> transport,
      List<DataCodec> codecs,
      Function<ConnectionContext, List<Object>> handlers) {
    this.rSocketFactory = rSocketFactory;
    this.transport = transport;
    this.codecs = codecs;
    this.handlers = handlers;
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

  public Function<ConnectionContext, List<Object>> handlers() {
    return handlers;
  }
}
