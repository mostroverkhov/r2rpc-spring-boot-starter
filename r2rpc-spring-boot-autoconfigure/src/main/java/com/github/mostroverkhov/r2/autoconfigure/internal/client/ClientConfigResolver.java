package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;
import com.github.mostroverkhov.r2.autoconfigure.RequestersProvider;
import com.github.mostroverkhov.r2.autoconfigure.client.ClientHandlersProvider;
import com.github.mostroverkhov.r2.autoconfigure.client.R2ClientTransport;
import com.github.mostroverkhov.r2.autoconfigure.internal.ApiHandlersArgs;
import com.github.mostroverkhov.r2.autoconfigure.internal.CodecResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.Resolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.client.apihandlers.ClientApiHandlersFactory;
import com.github.mostroverkhov.r2.autoconfigure.internal.client.apihandlers.ClientHandlersResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.ClientEndpointProperties;

import java.util.List;
import java.util.Set;

import static io.rsocket.RSocketFactory.ClientRSocketFactory;
import static java.util.stream.Collectors.toSet;

public class ClientConfigResolver implements
    Resolver<Set<ClientEndpointProperties>, Set<ClientConfig>> {
  private ClientRSocketFactory clientRSocketFactory;
  private final CodecResolver codecResolver;
  private final ClientTransportResolver clientTransportResolver;
  private final RequesterApiResolver requesterApiResolver;
  private final ClientHandlersResolver handlersResolver;

  ClientConfigResolver(ClientRSocketFactory clientRSocketFactory,
                       List<RequestersProvider> requesterProviders,
                       List<R2DataCodec> dataCodecs,
                       List<R2ClientTransport> transports,
                       List<ClientHandlersProvider<?>> handlerProviders) {
    this.clientRSocketFactory = clientRSocketFactory;
    this.clientTransportResolver = new ClientTransportResolver(transports);
    this.codecResolver = new CodecResolver(dataCodecs);
    this.requesterApiResolver = new RequesterApiResolver(requesterProviders);
    this.handlersResolver = new ClientHandlersResolver(handlerProviders);
  }

  @Override
  public Set<ClientConfig> resolve(Set<ClientEndpointProperties> props) {
    return props
        .stream()
        .map(this::resolve)
        .collect(toSet());
  }

  private ClientConfig resolve(ClientEndpointProperties clientProperties) {
    String name = clientProperties.getName();

    List<R2DataCodec> codecFactories = codecResolver
        .resolve(clientProperties.getCodecs());
    if (codecFactories.size() != 1) {
      throw new IllegalArgumentException("R2 Client currently " +
          "supports single data codec only");
    }
    R2DataCodec codecFactory = codecFactories.get(0);

    R2ClientTransport r2ClientTransport =
        clientTransportResolver
            .resolve(clientProperties.getTransport());

    List<Class<?>> apis = requesterApiResolver
        .resolve(clientProperties.getRequesters());

    List<String> responders = clientProperties.getResponders();
    ApiHandlersArgs apiHandlersArgs = new ApiHandlersArgs(responders, apis);
    ClientApiHandlersFactory handlers = handlersResolver.resolve(apiHandlersArgs);

    return new ClientConfig(
        name,
        codecFactory.get(),
        r2ClientTransport,
        clientRSocketFactory,
        apis,
        handlers);
  }
}
