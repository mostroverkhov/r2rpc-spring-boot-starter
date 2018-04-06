package com.github.mostroverkhov.r2.example;

import com.github.mostroverkhov.r2.autoconfigure.server.ServerApiProvider;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import com.github.mostroverkhov.r2.example.api.BazApiImpl;

public class BazApiProvider implements ServerApiProvider<BazApiImpl> {

  @Override
  public BazApiImpl apply(ConnectionContext connectionContext) {
    return new BazApiImpl(connectionContext);
  }
}
