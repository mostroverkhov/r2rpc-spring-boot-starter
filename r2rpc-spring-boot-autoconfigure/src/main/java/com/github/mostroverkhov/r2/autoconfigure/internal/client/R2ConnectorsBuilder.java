package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;
import com.github.mostroverkhov.r2.autoconfigure.RequestersProvider;
import com.github.mostroverkhov.r2.autoconfigure.client.ClientConnectors;
import com.github.mostroverkhov.r2.autoconfigure.client.ClientHandlersProvider;
import com.github.mostroverkhov.r2.autoconfigure.client.R2ClientConnector;
import com.github.mostroverkhov.r2.autoconfigure.client.R2ClientTransport;
import com.github.mostroverkhov.r2.autoconfigure.internal.PropertiesResolver.Resolved;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.ClientEndpointProperties;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.DefaultProperties;
import io.rsocket.RSocketFactory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toMap;

class R2ConnectorsBuilder {
  private Optional<List<R2ClientTransport>> transports = Optional.empty();
  private Optional<List<R2DataCodec>> dataCodecs = Optional.empty();
  private Optional<List<RequestersProvider>> apiProviders = Optional.empty();
  private Optional<List<ClientHandlersProvider<?>>> handlersProvider = Optional.empty();
  private RSocketFactory.ClientRSocketFactory rSocketFactory;

  R2ConnectorsBuilder(RSocketFactory.ClientRSocketFactory clientRSocketFactory) {
    rSocketFactory = clientRSocketFactory;
  }

  public R2ConnectorsBuilder apiProviders(Optional<List<RequestersProvider>> apiProviders) {
    this.apiProviders = apiProviders;
    return this;
  }

  public R2ConnectorsBuilder handlerProviders(Optional<List<ClientHandlersProvider<?>>> handlersProvider) {
    this.handlersProvider = handlersProvider;
    return this;
  }

  public R2ConnectorsBuilder dataCodecs(Optional<List<R2DataCodec>> dataCodecs) {
    this.dataCodecs = dataCodecs;
    return this;
  }

  public R2ConnectorsBuilder transports(Optional<List<R2ClientTransport>> transports) {
    this.transports = transports;
    return this;
  }

  public ClientConnectors build(DefaultProperties defProps,
                                List<ClientEndpointProperties> props) {
    ClientPropertiesResolver propertiesResolver =
        new ClientPropertiesResolver(
            clientFallbackProperties());
    ClientConfigResolver configResolver = new ClientConfigResolver(
        rSocketFactory,
        orEmpty(apiProviders),
        orEmpty(dataCodecs),
        orEmpty(transports),
        orEmpty(handlersProvider));

    Resolved<Set<ClientEndpointProperties>> resolved = propertiesResolver
        .resolve(props, defProps);
    if (resolved.isErr()) {
      throw new IllegalArgumentException(
          "R2Client config is not complete: " + resolved.err());
    }
    Set<ClientEndpointProperties> clientProperties = resolved.succ();
    Set<ClientConfig> clientConfigs = configResolver.resolve(clientProperties);

    Map<String, Supplier<R2ClientConnector>> endpoints = clientConfigs
        .stream()
        .collect(
            toMap(
                ClientConfig::name,
                this::connectorSupplier));

    return new InternalClientConnectors(endpoints);
  }

  private static <T> List<T> orEmpty(Optional<List<T>> list) {
    return list.orElse(Collections.emptyList());
  }

  @NotNull
  private Supplier<R2ClientConnector> connectorSupplier(ClientConfig config) {
    return () -> new R2ClientConnector(config);
  }

  static DefaultProperties clientFallbackProperties() {
    DefaultProperties defaultProperties = new DefaultProperties();
    defaultProperties.setTransport("tcp");
    defaultProperties.setCodecs(Collections.singletonList("json"));
    return defaultProperties;
  }
}
