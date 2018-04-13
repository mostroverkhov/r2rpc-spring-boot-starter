package com.github.mostroverkhov.r2.example.server;

import com.github.mostroverkhov.r2.autoconfigure.server.ResponderApiProvider;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import com.github.mostroverkhov.r2.example.server.handlers.BazApiImpl;

public class BazServerApiProvider implements ResponderApiProvider<BazApiImpl> {

  @Override
  public BazApiImpl apply(ConnectionContext connectionContext) {
    return new BazApiImpl(connectionContext);
  }
}
