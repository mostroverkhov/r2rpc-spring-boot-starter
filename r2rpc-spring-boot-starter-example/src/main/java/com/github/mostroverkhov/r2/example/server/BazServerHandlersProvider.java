package com.github.mostroverkhov.r2.example.server;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.autoconfigure.server.ServerHandlersProvider;
import com.github.mostroverkhov.r2.core.ConnectionContext;
import com.github.mostroverkhov.r2.example.server.handlers.BazApiImpl;

public class BazServerHandlersProvider implements ServerHandlersProvider<BazApiImpl> {

  @Override
  public BazApiImpl apply(ConnectionContext connectionContext,
                          ApiRequesterFactory apiRequesterFactory) {
    return new BazApiImpl(connectionContext);
  }
}
