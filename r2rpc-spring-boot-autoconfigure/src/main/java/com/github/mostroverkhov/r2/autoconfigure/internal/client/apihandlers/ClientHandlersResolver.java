package com.github.mostroverkhov.r2.autoconfigure.internal.client.apihandlers;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.autoconfigure.client.ClientHandlersProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.BaseHandlersResolver;
import com.github.mostroverkhov.r2.core.RequesterFactory;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClientHandlersResolver extends BaseHandlersResolver<
    ClientApiHandlersFactory,
    ClientHandlersProvider<?>,
    ClientHandlersInfo> {
  public ClientHandlersResolver(List<ClientHandlersProvider<?>> clientHandlersProviders) {
    super(clientHandlersProviders, new ClientHandlersOperations());
  }

  @Override
  protected ClientApiHandlersFactory handlersFactory(
      Collection<ClientHandlersInfo> apiInfos,
      Function<RequesterFactory, ApiRequesterFactory> requesterFactoryConverter) {

    return (requesterFactory) -> {

      ApiRequesterFactory apiRequesterFactory =
          requesterFactoryConverter
              .apply(requesterFactory);

      return apiInfos
          .stream()
          .map(apiInfo -> apiInfo.createHandlers(apiRequesterFactory))
          .flatMap(Collection::stream)
          .collect(Collectors.toList());
    };

  }
}
