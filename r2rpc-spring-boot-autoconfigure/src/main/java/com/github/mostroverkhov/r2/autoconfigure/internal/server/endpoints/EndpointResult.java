package com.github.mostroverkhov.r2.autoconfigure.internal.server.endpoints;

import java.util.Objects;

public class EndpointResult {

  private final String name;
  private final Throwable error;

  private EndpointResult(String name, Throwable error) {
    Objects.requireNonNull(name);
    this.name = name;
    this.error = error;
  }

  public static EndpointResult success(String name) {
    return new EndpointResult(name, null);
  }

  public static EndpointResult error(String name, Throwable error) {
    Objects.requireNonNull(error);
    return new EndpointResult(name, error);
  }

  public boolean isError() {
    return error != null;
  }

  public String getName() {
    return name;
  }

  public Throwable getError() {
    return error;
  }

  @Override
  public String toString() {
    return "EndpointResult{" +
        "name='" + name + '\'' +
        ", error=" + error +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EndpointResult that = (EndpointResult) o;
    return Objects.equals(name, that.name) &&
        Objects.equals(error, that.error);
  }

  @Override
  public int hashCode() {

    return Objects.hash(name, error);
  }
}
