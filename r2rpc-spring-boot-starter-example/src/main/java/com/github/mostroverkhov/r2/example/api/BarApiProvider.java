package com.github.mostroverkhov.r2.example.api;

import com.github.mostroverkhov.r2.autoconfigure.ServerApiProvider;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;

public class BarApiProvider implements ServerApiProvider<BarApiImpl> {

  @Override
  public BarApiImpl apply(ConnectionContext connectionContext) {
    return new BarApiImpl();
  }
}
