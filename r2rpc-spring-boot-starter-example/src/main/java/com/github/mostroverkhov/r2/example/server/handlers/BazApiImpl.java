package com.github.mostroverkhov.r2.example.server.handlers;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.core.ConnectionContext;
import com.github.mostroverkhov.r2.example.api.BazApi;
import com.github.mostroverkhov.r2.example.svc.contract.BazContract;
import com.github.mostroverkhov.r2.example.svc.handlers.BazHandler;

public class BazApiImpl implements BazApi {

  private ConnectionContext connectionContext;
  private ApiRequesterFactory apiRequesterFactory;

  public BazApiImpl(
      ConnectionContext connectionContext) {
    this.connectionContext = connectionContext;
  }

  public BazApiImpl(ApiRequesterFactory apiRequesterFactory) {
    this.apiRequesterFactory = apiRequesterFactory;
  }

  @Override
  public BazContract bazContract() {
    return new BazHandler();
  }
}
