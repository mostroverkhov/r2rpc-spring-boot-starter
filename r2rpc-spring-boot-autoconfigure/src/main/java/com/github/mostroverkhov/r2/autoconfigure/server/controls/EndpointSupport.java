package com.github.mostroverkhov.r2.autoconfigure.server.controls;

import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.ReplayProcessor;

public class EndpointSupport {

  private final FluxProcessor<
      EndpointStartResult,
      EndpointStartResult> starts = newProcessor();
  private final FluxProcessor<
      EndpointStartResult,
      EndpointStartResult> stops = newProcessor();

  public void startFailed(String name, Throwable err) {
    starts.onNext(EndpointStartResult.error(name, err));
  }

  public void startSucceeded(String name) {
    starts.onNext(EndpointStartResult.success(name));
  }

  public void startCompleted() {
    starts.onComplete();
  }

  public void stopSuceeded(String name) {
    stops.onNext(EndpointStartResult.success(name));
  }

  public void stopCompleted() {
    stops.onComplete();
  }

  public Flux<EndpointStartResult> starts() {
    return starts;
  }

  public Flux<EndpointStartResult> stops() {
    return stops;
  }

  @NotNull
  private FluxProcessor<EndpointStartResult, EndpointStartResult> newProcessor() {
    return ReplayProcessor
        .<EndpointStartResult>create()
        .serialize();
  }
}
