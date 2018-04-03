package com.github.mostroverkhov.r2.example.api;

import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import com.github.mostroverkhov.r2.example.api.baz.BazContract;

public class BazApiImpl implements BazApi {

  private final ConnectionContext connectionContext;

  public BazApiImpl(
      ConnectionContext connectionContext) {
    this.connectionContext = connectionContext;
  }

  @Override
  public BazContract baz() {
    return new BazHandler();
  }
}
