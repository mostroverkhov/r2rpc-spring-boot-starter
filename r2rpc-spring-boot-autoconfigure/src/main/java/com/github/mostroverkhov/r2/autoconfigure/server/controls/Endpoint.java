package com.github.mostroverkhov.r2.autoconfigure.server.controls;

import java.util.Objects;

public class Endpoint {

  private final String name;

  public Endpoint(String name) {
    Objects.requireNonNull(name);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "Endpoint{" +
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
