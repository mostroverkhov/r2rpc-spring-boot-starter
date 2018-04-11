package com.github.mostroverkhov.r2.autoconfigure.client;

import com.github.mostroverkhov.r2.autoconfigure.internal.client.ClientConfig;
import com.github.mostroverkhov.r2.core.Metadata;
import com.github.mostroverkhov.r2.core.internal.requester.ClientFluentBuilder;
import com.github.mostroverkhov.r2.core.requester.RequesterFactory;
import com.github.mostroverkhov.r2.java.R2Client;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory.ClientRSocketFactory;
import io.rsocket.transport.ClientTransport;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

public class R2ClientConnector {
  private final ClientFluentBuilder<ClientRSocketFactory,
      ClientTransport, RSocket, Mono<RequesterFactory>> clientBuilder;
  private final R2ClientTransport transportFactory;
  private volatile Metadata metadata;
  private final List<Class<?>> apis;

  public R2ClientConnector(ClientConfig clientConfig) {
    this.apis = clientConfig.apis();
    this.transportFactory = clientConfig.transportFactory();
    this.clientBuilder = new R2Client()
        .connectWith(clientConfig.rSocketFactory())
        .configureRequester(config ->
            config.codec(clientConfig.dataCodec()));
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
}
