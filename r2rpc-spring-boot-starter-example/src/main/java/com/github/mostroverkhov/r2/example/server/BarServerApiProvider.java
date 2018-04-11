package com.github.mostroverkhov.r2.example.server;

import com.github.mostroverkhov.r2.autoconfigure.server.ResponderApiProvider;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import com.github.mostroverkhov.r2.example.server.handlers.BarApiImpl;

public class BarServerApiProvider implements ResponderApiProvider<BarApiImpl> {

  @Override
  public BarApiImpl apply(ConnectionContext connectionContext) {
    return new BarApiImpl();
  }
}
