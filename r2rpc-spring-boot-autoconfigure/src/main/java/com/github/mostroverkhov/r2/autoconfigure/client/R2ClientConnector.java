package com.github.mostroverkhov.r2.autoconfigure.client;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.autoconfigure.internal.client.ClientConfig;
import com.github.mostroverkhov.r2.autoconfigure.internal.client.apihandlers.ClientApiHandlersFactory;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.apihandlers.ServerApiHandlersFactory;
import com.github.mostroverkhov.r2.codec.jackson.JacksonJsonDataCodec;
import com.github.mostroverkhov.r2.core.*;
import com.github.mostroverkhov.r2.java.ClientAcceptorBuilder;
import com.github.mostroverkhov.r2.java.R2Client;
import io.rsocket.RSocketFactory.ClientRSocketFactory;
import io.rsocket.transport.ClientTransport;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class R2ClientConnector {
  private final R2ClientFluentBuilder<
      ClientRSocketFactory,
      ClientAcceptorBuilder,
      ClientTransport,
      Mono<RequesterFactory>> clientBuilder;
  private final R2ClientTransport transportFactory;
  private volatile Metadata metadata;
  private final List<Class<?>> apis;

  public R2ClientConnector(ClientConfig clientConfig) {
    this.apis = clientConfig.apis();
    ClientApiHandlersFactory handlers = clientConfig.clientApiHandlersFactory();
    DataCodec dataCodec = clientConfig.dataCodec();

    this.transportFactory = clientConfig.transportFactory();
    this.clientBuilder = new R2Client()
        .connectWith(clientConfig.rSocketFactory())
        .configureAcceptor(b ->
            b.codecs(
                new Codecs()
                    .add(dataCodec))
                .services(requesterFactory ->
                    addHandlers(requesterFactory, handlers)));
  }

  public R2ClientConnector metadata(Metadata metadata) {
    this.metadata = Objects.requireNonNull(metadata);
    return this;
  }

  public R2ClientConnector clearMetadata() {
    this.metadata = null;
    return this;
  }

  public Mono<ApiRequesterFactory> connect(String address, int port) {
    Objects.requireNonNull(address);
    return Mono.defer(
        () ->
            clientBuilder
                .transport(transportFactory.apply(address, port))
                .metadata(metadata)
                .start()
                .map(rf -> new ApiRequesterFactory(rf, apis)));
  }

  private Services addHandlers(
      RequesterFactory requesterFactory,
      ClientApiHandlersFactory clientHandlers) {
    Services services = new Services();
    Collection<Object> handlers = clientHandlers.apply(requesterFactory);
    handlers.forEach(services::add);
    return services;
  }
}
