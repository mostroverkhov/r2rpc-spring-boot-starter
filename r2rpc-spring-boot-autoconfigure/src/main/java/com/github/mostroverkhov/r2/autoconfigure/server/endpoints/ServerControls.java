package com.github.mostroverkhov.r2.autoconfigure.server.endpoints;

import com.github.mostroverkhov.r2.autoconfigure.internal.server.endpoints.EndpointResult;
import java.util.Objects;
import reactor.core.publisher.Flux;

public class ServerControls {

  private final Flux<EndpointResult> endpointStarts;
  private final Flux<EndpointResult> endpointStops;

  public ServerControls(
      Flux<EndpointResult> endpointStarts,
      Flux<EndpointResult> endpointStops) {
    Objects.requireNonNull(endpointStarts);
    Objects.requireNonNull(endpointStops);
    this.endpointStarts = endpointStarts;
    this.endpointStops = endpointStops;
  }

  public EndpointLifecycle endpoint(String name) {
    Objects.requireNonNull(name);
    return new EndpointLifecycle(name, endpointStarts, endpointStops);
  }
}
