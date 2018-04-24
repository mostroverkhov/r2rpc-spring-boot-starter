package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;
import com.github.mostroverkhov.r2.autoconfigure.client.R2ClientConnector;
import com.github.mostroverkhov.r2.autoconfigure.client.R2ClientTransport;
import com.github.mostroverkhov.r2.autoconfigure.client.RequesterApiProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.DefaultProperties;
import com.github.mostroverkhov.r2.autoconfigure.internal.PropertiesResolver.Resolved;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.RequesterEndpointProperties;
import io.rsocket.RSocketFactory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toMap;

class R2ConnectorsBuilder {
  private Optional<List<R2ClientTransport>> transports;
  private Optional<List<R2DataCodec>> dataCodecs;
  private Optional<List<RequesterApiProvider>> apiProviders;
  private RSocketFactory.ClientRSocketFactory rSocketFactory;

  R2ConnectorsBuilder(RSocketFactory.ClientRSocketFactory clientRSocketFactory) {
    rSocketFactory = clientRSocketFactory;
  }

  public R2ConnectorsBuilder apiProviders(Optional<List<RequesterApiProvider>> apiProviders) {
    this.apiProviders = apiProviders;
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
                                List<RequesterEndpointProperties> props) {
    ClientPropertiesResolver propertiesResolver =
        new ClientPropertiesResolver(
            clientFallbackProperties());
    ClientConfigResolver configResolver = new ClientConfigResolver(
        rSocketFactory,
        orEmpty(apiProviders),
        orEmpty(dataCodecs),
        orEmpty(transports));

    Resolved<Set<RequesterEndpointProperties>> resolved = propertiesResolver
        .resolve(props, defProps);
    if (resolved.isErr()) {
      throw new IllegalArgumentException(
          "R2Client config is not complete: " + resolved.err());
    }
    Set<RequesterEndpointProperties> clientProperties = resolved.succ();
    Set<ClientConfig> clientConfigs = configResolver.resolve(clientProperties);

    Map<String, Supplier<R2ClientConnector>> endpoints = clientConfigs
        .stream()
        .collect(
            toMap(
                ClientConfig::name,
                this::connectorSupplier));

    return new ClientConnectors(endpoints);
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
