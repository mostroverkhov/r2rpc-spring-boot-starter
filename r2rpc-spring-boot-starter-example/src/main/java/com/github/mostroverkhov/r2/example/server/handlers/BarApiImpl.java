package com.github.mostroverkhov.r2.example.server.handlers;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.core.ConnectionContext;
import com.github.mostroverkhov.r2.example.api.BarApi;
import com.github.mostroverkhov.r2.example.api.BazApi;
import com.github.mostroverkhov.r2.example.svc.contract.BarContract;
import com.github.mostroverkhov.r2.example.svc.handlers.BarHandler;

public class BarApiImpl implements BarApi {
  private final ApiRequesterFactory apiRequesterFactory;
  private final ConnectionContext connectionContext;

  public BarApiImpl(ConnectionContext connectionContext,
                    ApiRequesterFactory apiRequesterFactory) {
    this.apiRequesterFactory = apiRequesterFactory;
    this.connectionContext = connectionContext;
  }

  @Override
  public BarContract barContract() {
    return new BarHandler(apiRequesterFactory.create(BazApi.class));
  }
}
