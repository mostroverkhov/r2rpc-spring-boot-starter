package com.github.mostroverkhov.r2.autoconfigure.internal.server;

import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;
import com.github.mostroverkhov.r2.autoconfigure.RequestersProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.ApiHandlersArgs;
import com.github.mostroverkhov.r2.autoconfigure.internal.CodecResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.Resolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.client.RequesterApiResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.ServerEndpointProperties;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.apihandlers.ServerApiHandlersFactory;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.apihandlers.ServerHandlersResolver;
import com.github.mostroverkhov.r2.autoconfigure.server.R2ServerTransport;
import com.github.mostroverkhov.r2.autoconfigure.server.ServerHandlersProvider;
import com.github.mostroverkhov.r2.core.DataCodec;
import io.rsocket.Closeable;
import io.rsocket.RSocketFactory.ServerRSocketFactory;
import io.rsocket.transport.ServerTransport;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

class ServerConfigResolver implements Resolver<Set<ServerEndpointProperties>, Set<ServerConfig>> {

  private final ServerTransportResolver serverTransportResolver;
  private final CodecResolver codecResolver;
  private final ServerHandlersResolver handlersResolver;
  private final ServerRSocketFactory serverRSocketFactory;
  private final RequesterApiResolver requesterApiResolver;

  public ServerConfigResolver(ServerRSocketFactory serverRSocketFactory,
                              List<ServerHandlersProvider<?>> handlerProviders,
                              List<R2DataCodec> dataCodecs,
                              List<R2ServerTransport> transports,
                              List<RequestersProvider> requesterProviders) {
    this.serverRSocketFactory = serverRSocketFactory;
    this.serverTransportResolver = new ServerTransportResolver(transports);
    this.codecResolver = new CodecResolver(dataCodecs);
    this.handlersResolver = new ServerHandlersResolver(handlerProviders);
    this.requesterApiResolver = new RequesterApiResolver(requesterProviders);
  }

  @Override
  public Set<ServerConfig> resolve(Set<ServerEndpointProperties> serverEndpointProperties) {
    return serverEndpointProperties
        .stream()
        .map(this::resolveServerConfig)
        .collect(toSet());
  }

  private ServerConfig resolveServerConfig(ServerEndpointProperties props) {
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

    List<Class<?>> apiRequesters = requesterApiResolver.resolve(props.getRequesters());
    List<String> responderNames = props.getResponders();
    ApiHandlersArgs apiHandlersArgs = new ApiHandlersArgs(responderNames, apiRequesters);
    ServerApiHandlersFactory handlers = handlersResolver.resolve(apiHandlersArgs);

    return new ServerConfig(name, serverRSocketFactory, transport, codecs, handlers);
  }
}
