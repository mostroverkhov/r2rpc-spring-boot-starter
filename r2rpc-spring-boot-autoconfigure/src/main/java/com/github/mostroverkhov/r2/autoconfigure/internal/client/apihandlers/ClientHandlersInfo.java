package com.github.mostroverkhov.r2.autoconfigure.internal.client.apihandlers;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.autoconfigure.client.ClientHandlersProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.ApiHandlersInfo;

import java.util.Collection;

public class ClientHandlersInfo extends ApiHandlersInfo<ClientHandlersProvider<?>> {
  public ClientHandlersInfo(String name,
                            Class<?> apiType,
                            ClientHandlersProvider<?> clientHandlersProvider) {
    super(name, apiType, clientHandlersProvider);
  }

  public ClientHandlersInfo copy(String name) {
    return new ClientHandlersInfo(name, apiType, handlersProvider);
  }

  public Collection<Object> createHandlers(ApiRequesterFactory apiRequesterFactory) {
    Object apiImpl = handlersProvider.apply(apiRequesterFactory);
    return resolveSvcHandlers(apiImpl);
  }
}
