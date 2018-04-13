package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;
import com.github.mostroverkhov.r2.autoconfigure.client.R2ClientTransport;
import com.github.mostroverkhov.r2.autoconfigure.client.RequesterApiProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.CodecResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.Resolver;

import java.util.List;
import java.util.Set;

import static io.rsocket.RSocketFactory.ClientRSocketFactory;
import static java.util.stream.Collectors.toSet;

public class ClientConfigResolver implements
    Resolver<Set<R2ClientProperties>, Set<ClientConfig>> {
  private ClientRSocketFactory clientRSocketFactory;
  private final CodecResolver codecResolver;
  private final ClientTransportResolver clientTransportResolver;
  private final ApiResolver apiResolver;

  ClientConfigResolver(ClientRSocketFactory clientRSocketFactory,
                       List<RequesterApiProvider> apiProviders,
                       List<R2DataCodec> dataCodecs,
                       List<R2ClientTransport> transports) {
    this.clientRSocketFactory = clientRSocketFactory;
    this.clientTransportResolver = new ClientTransportResolver(transports);
    this.codecResolver = new CodecResolver(dataCodecs);
    this.apiResolver = new ApiResolver(apiProviders);
  }

  @Override
  public Set<ClientConfig> resolve(Set<R2ClientProperties> props) {
    return props
        .stream()
        .map(this::resolve)
        .collect(toSet());
  }

  private ClientConfig resolve(R2ClientProperties clientProperties) {
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
    List<Class<?>> apis = apiResolver
        .resolve(clientProperties.getApi());

    return new ClientConfig(
        name,
        codecFactory.get(),
        r2ClientTransport,
        clientRSocketFactory,
        apis);
  }
}
