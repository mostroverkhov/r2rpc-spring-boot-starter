package com.github.mostroverkhov.r2.autoconfigure.internal.server.apihandlers;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.autoconfigure.internal.BaseHandlersResolver;
import com.github.mostroverkhov.r2.autoconfigure.server.ServerHandlersProvider;
import com.github.mostroverkhov.r2.core.RequesterFactory;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServerHandlersResolver extends
    BaseHandlersResolver<
        ServerApiHandlersFactory,
        ServerHandlersProvider<?>,
        ServerHandlersInfo> {

  public ServerHandlersResolver(List<ServerHandlersProvider<?>> providers) {
    super(providers, new ServerHandlersOperations());
  }

  @Override
  public ServerApiHandlersFactory handlersFactory(
      Collection<ServerHandlersInfo> apiInfos,
      Function<RequesterFactory, ApiRequesterFactory> requesterFactoryConverter) {
    assertTypesUnique(apiInfos);

    return (connCtx, requesterFactory) -> {

      ApiRequesterFactory apiRequesterFactory =
          requesterFactoryConverter
              .apply(requesterFactory);

      return apiInfos
          .stream()
          .map(apiInfo -> apiInfo.createHandlers(connCtx, apiRequesterFactory))
          .flatMap(Collection::stream)
          .collect(Collectors.toList());
    };
  }
}
