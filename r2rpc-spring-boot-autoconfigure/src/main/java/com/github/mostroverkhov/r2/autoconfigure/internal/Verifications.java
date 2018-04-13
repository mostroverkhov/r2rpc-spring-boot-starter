package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.internal.properties.EndpointProperties;

import java.util.Optional;
import java.util.function.Function;

public interface Verifications<T extends EndpointProperties> {

  void addVerifications(Function<T, Optional<String>>... functions);

  default Function<T, Optional<String>> name() {
    return props -> {
      String name = props.getName();
      if (name == null || name.isEmpty()) {
        return Optional.of("Configuration name must be present");
      } else {
        return Optional.empty();
      }
    };
  }
}
