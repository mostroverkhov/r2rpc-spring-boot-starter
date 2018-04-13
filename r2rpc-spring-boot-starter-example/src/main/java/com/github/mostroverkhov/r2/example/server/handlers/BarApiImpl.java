package com.github.mostroverkhov.r2.example.server.handlers;

import com.github.mostroverkhov.r2.example.api.BarApi;
import com.github.mostroverkhov.r2.example.svc.contract.BarContract;
import com.github.mostroverkhov.r2.example.svc.handlers.BarHandler;

public class BarApiImpl implements BarApi {

  @Override
  public BarContract barContract() {
    return new BarHandler();
  }
}
