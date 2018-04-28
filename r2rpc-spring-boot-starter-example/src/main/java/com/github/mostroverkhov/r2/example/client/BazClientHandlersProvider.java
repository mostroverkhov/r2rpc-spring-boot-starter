package com.github.mostroverkhov.r2.example.client;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.autoconfigure.client.ClientHandlersProvider;
import com.github.mostroverkhov.r2.example.server.handlers.BazApiImpl;

public class BazClientHandlersProvider implements ClientHandlersProvider<BazApiImpl> {
  @Override
  public BazApiImpl apply(ApiRequesterFactory requesterFactory) {
    return new BazApiImpl(requesterFactory);
  }
}
