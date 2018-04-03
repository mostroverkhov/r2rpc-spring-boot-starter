package com.github.mostroverkhov.r2.example.api;

import com.github.mostroverkhov.r2.example.api.bar.BarContract;

public class BarApiImpl implements BarApi {

  @Override
  public BarContract barContract() {
    return new BarHandler();
  }
}
