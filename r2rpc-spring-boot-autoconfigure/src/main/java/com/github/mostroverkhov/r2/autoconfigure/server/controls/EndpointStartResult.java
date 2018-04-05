package com.github.mostroverkhov.r2.autoconfigure.server.controls;

import java.util.Objects;

final class EndpointStartResult {

  private final String name;
  private final Throwable error;

  private EndpointStartResult(String name, Throwable error) {
    Objects.requireNonNull(name);
    this.name = name;
    this.error = error;
  }

  static EndpointStartResult success(String name) {
    return new EndpointStartResult(name, null);
  }

  static EndpointStartResult error(String name, Throwable error) {
    return new EndpointStartResult(name, error);
  }

  boolean isError() {
    return error != null;
  }

  public String getName() {
    return name;
  }

  public Throwable getError() {
    return error;
  }
}
