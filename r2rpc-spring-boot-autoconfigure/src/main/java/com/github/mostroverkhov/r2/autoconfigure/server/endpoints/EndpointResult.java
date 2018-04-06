package com.github.mostroverkhov.r2.autoconfigure.server.endpoints;

import java.util.Objects;

final class EndpointResult {

  private final String name;
  private final Throwable error;

  private EndpointResult(String name, Throwable error) {
    Objects.requireNonNull(name);
    this.name = name;
    this.error = error;
  }

  static EndpointResult success(String name) {
    return new EndpointResult(name, null);
  }

  static EndpointResult error(String name, Throwable error) {
    Objects.requireNonNull(error);
    return new EndpointResult(name, error);
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
