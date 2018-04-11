package com.github.mostroverkhov.r2.autoconfigure.internal.server;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toSet;

import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;
import com.github.mostroverkhov.r2.autoconfigure.internal.CodecResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.Resolver;
import com.github.mostroverkhov.r2.autoconfigure.server.ResponderApiProvider;
import com.github.mostroverkhov.r2.autoconfigure.server.R2ServerTransport;
import com.github.mostroverkhov.r2.core.DataCodec;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import io.rsocket.Closeable;
import io.rsocket.RSocketFactory.ServerRSocketFactory;
import io.rsocket.transport.ServerTransport;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

class ServerConfigResolver implements Resolver<Set<R2ServerProperties>, Set<ServerConfig>> {

  private final ServerTransportResolver serverTransportResolver;
  private final CodecResolver codecResolver;
  private final HandlersResolver handlersResolver;
  private final ServerRSocketFactory serverRSocketFactory;

  public ServerConfigResolver(ServerRSocketFactory serverRSocketFactory,
                              List<ResponderApiProvider> apiProviders,
                              List<R2DataCodec> dataCodecs,
                              List<R2ServerTransport> transports) {
    this.serverRSocketFactory = serverRSocketFactory;
    this.serverTransportResolver = new ServerTransportResolver(transports);
    this.codecResolver = new CodecResolver(dataCodecs);
    this.handlersResolver = new HandlersResolver(apiProviders);
  }

  @Override
  public Set<ServerConfig> resolve(Set<R2ServerProperties> r2ServerProperties) {
    return r2ServerProperties
        .stream()
        .map(this::resolveServerConfig)
        .collect(toSet());
  }

  private ServerConfig resolveServerConfig(R2ServerProperties props) {
    String name = props.getName();
    @SuppressWarnings("unchecked")
    R2ServerTransport<Closeable> transportFactory =
        serverTransportResolver
            .resolve(props.getTransport());

    ServerTransport<Closeable> transport = transportFactory
        .apply(props.getPort());

    List<R2DataCodec> codecFactories = codecResolver
        .resolve(props.getCodecs());

    List<DataCodec> codecs = codecFactories
        .stream()
        .map(Supplier::get)
        .collect(toList());

    Function<ConnectionContext, Collection<Object>> handlers =
        handlersResolver.resolve(props.getApi());

    return new ServerConfig(name, serverRSocketFactory, transport, codecs, handlers);
  }
}
