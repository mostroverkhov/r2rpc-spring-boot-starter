package com.github.mostroverkhov.r2.autoconfigure.server.controls;

import java.util.Objects;
import reactor.core.publisher.Flux;

public class ServerControls {

  private final Flux<EndpointStartResult> endpointStarts;
  private final Flux<EndpointStartResult> endpointStops;

  public ServerControls(
      Flux<EndpointStartResult> endpointStarts,
      Flux<EndpointStartResult> endpointStops) {
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
