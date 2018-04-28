package com.github.mostroverkhov.r2.autoconfigure.internal.server.apihandlers;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.autoconfigure.internal.ApiHandlersInfo;
import com.github.mostroverkhov.r2.autoconfigure.server.ServerHandlersProvider;
import com.github.mostroverkhov.r2.core.ConnectionContext;

import java.util.Collection;

public final class ServerHandlersInfo extends
    ApiHandlersInfo<ServerHandlersProvider<?>> {

  public ServerHandlersInfo(
      String name,
      Class<?> apiType,
      ServerHandlersProvider<?> serverHandlersProvider) {
    super(name, apiType, serverHandlersProvider);
  }

  public ServerHandlersInfo copy(String name) {
    return new ServerHandlersInfo(name, apiType, handlersProvider);
  }

  public Collection<Object> createHandlers(ConnectionContext ctx,
                                           ApiRequesterFactory apiRequesterFactory) {
    Object apiImpl = handlersProvider.apply(ctx, apiRequesterFactory);
    return resolveSvcHandlers(apiImpl);
  }
}
