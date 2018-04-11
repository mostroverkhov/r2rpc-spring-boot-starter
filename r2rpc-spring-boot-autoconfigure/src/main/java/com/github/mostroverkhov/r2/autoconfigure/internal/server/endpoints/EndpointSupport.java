package com.github.mostroverkhov.r2.autoconfigure.internal.server.endpoints;

import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.ReplayProcessor;

public class EndpointSupport {

  private final FluxProcessor<
      EndpointResult,
      EndpointResult> starts = newProcessor();
  private final FluxProcessor<
      EndpointResult,
      EndpointResult> stops = newProcessor();

  public void startFailed(String name, Throwable err) {
    starts.onNext(EndpointResult.error(name, err));
  }

  public void startSucceeded(String name) {
    starts.onNext(EndpointResult.success(name));
  }

  public void startCompleted() {
    starts.onComplete();
  }

  public void stopSucceeded(String name) {
    stops.onNext(EndpointResult.success(name));
  }

  public void stopCompleted() {
    stops.onComplete();
  }

  public Flux<EndpointResult> starts() {
    return starts;
  }

  public Flux<EndpointResult> stops() {
    return stops;
  }

  @NotNull
  private FluxProcessor<EndpointResult, EndpointResult> newProcessor() {
    return ReplayProcessor
        .<EndpointResult>create()
        .serialize();
  }
}
