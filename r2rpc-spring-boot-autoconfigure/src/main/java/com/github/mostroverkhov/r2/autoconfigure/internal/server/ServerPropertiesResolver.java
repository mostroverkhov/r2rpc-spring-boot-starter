package com.github.mostroverkhov.r2.autoconfigure.internal.server;

import com.github.mostroverkhov.r2.autoconfigure.internal.PropertiesResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.DefaultProperties;
import com.github.mostroverkhov.r2.autoconfigure.internal.Verifications;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.ServerEndpointProperties;

import java.util.Optional;
import java.util.function.Function;

class ServerPropertiesResolver extends PropertiesResolver<ServerEndpointProperties> {

  public ServerPropertiesResolver(DefaultProperties fallbackServerProps) {
    super(fallbackServerProps);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void verifications(Verifications<ServerEndpointProperties> verifications) {
    verifications.addVerifications(name(), port());
  }

  private Function<ServerEndpointProperties, Optional<String>> port() {
    return props -> {
      int port = props.getPort();
      if (port <= 0) {
        return Optional.of(String.format(
            "%s: port must be positive: %d", props.getName(), port));
      } else {
        return Optional.empty();
      }
    };
  }
}
