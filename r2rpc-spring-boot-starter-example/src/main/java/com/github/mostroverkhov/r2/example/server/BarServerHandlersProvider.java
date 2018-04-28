package com.github.mostroverkhov.r2.example.server;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.autoconfigure.server.ServerHandlersProvider;
import com.github.mostroverkhov.r2.core.ConnectionContext;
import com.github.mostroverkhov.r2.example.server.handlers.BarApiImpl;

public class BarServerHandlersProvider implements ServerHandlersProvider<BarApiImpl> {

  @Override
  public BarApiImpl apply(ConnectionContext connectionContext,
                          ApiRequesterFactory requesterFactory) {
    return new BarApiImpl(connectionContext, requesterFactory);
  }
}
