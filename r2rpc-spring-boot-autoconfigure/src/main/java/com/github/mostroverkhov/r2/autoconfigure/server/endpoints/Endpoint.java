package com.github.mostroverkhov.r2.autoconfigure.server.endpoints;

import java.util.Objects;

public class Endpoint {

  private final String name;

  Endpoint(String name) {
    Objects.requireNonNull(name);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "EndpointProperties{" +
        "name='" + name + '\'' +
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
    Endpoint endpoint = (Endpoint) o;
    return Objects.equals(name, endpoint.name);
  }

  @Override
  public int hashCode() {

    return Objects.hash(name);
  }
}
