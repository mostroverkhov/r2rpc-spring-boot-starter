package com.github.mostroverkhov.r2.example.server.handlers;

import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import com.github.mostroverkhov.r2.example.api.BazApi;
import com.github.mostroverkhov.r2.example.svc.contract.BazContract;
import com.github.mostroverkhov.r2.example.svc.handlers.BazHandler;

public class BazApiImpl implements BazApi {

  private final ConnectionContext connectionContext;

  public BazApiImpl(
      ConnectionContext connectionContext) {
    this.connectionContext = connectionContext;
  }

  @Override
  public BazContract bazContract() {
    return new BazHandler();
  }
}
