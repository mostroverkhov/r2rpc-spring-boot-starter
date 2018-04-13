package com.github.mostroverkhov.r2.autoconfigure.server.endpoints;

import com.github.mostroverkhov.r2.autoconfigure.internal.server.endpoints.EndpointResult;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class EndpointLifecycle {

  private final String name;
  private final Flux<EndpointResult> startSignals;
  private final Flux<EndpointResult> stopSignals;

  public EndpointLifecycle(
      String name,
      Flux<EndpointResult> startSignals,
      Flux<EndpointResult> stopSignals) {
    this.name = name;
    this.startSignals = startSignals;
    this.stopSignals = stopSignals;
  }

  public Mono<Endpoint> started() {
    return endpointFrom(startSignals);
  }

  public Mono<Endpoint> stopped() {
    return endpointFrom(stopSignals);
  }

  @NotNull
  private Mono<Endpoint> endpointFrom(Flux<EndpointResult> startSignals) {
    return startSignals
        .filter(result ->
            name.equals(
                result.getName()))
        .next()
        .switchIfEmpty(Mono
            .error(new MissingEndpointException(name)))
        .flatMap(signal -> {
          if (signal.isError()) {
            return Mono.error(signal.getError());
          } else {
            return Mono.just(new Endpoint(signal.getName()));
          }
        });
  }
}
