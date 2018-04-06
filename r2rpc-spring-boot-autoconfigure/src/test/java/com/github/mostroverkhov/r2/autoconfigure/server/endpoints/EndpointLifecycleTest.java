package com.github.mostroverkhov.r2.autoconfigure.server.endpoints;

import static com.github.mostroverkhov.r2.autoconfigure.internal.endpoints.EndpointResult.error;
import static com.github.mostroverkhov.r2.autoconfigure.internal.endpoints.EndpointResult.success;

import com.github.mostroverkhov.r2.autoconfigure.internal.endpoints.EndpointResult;
import java.time.Duration;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class EndpointLifecycleTest {

  @Test
  public void lifecycleSuccess() {

    Flux<EndpointResult> results = Flux.just(
        success("bar"),
        success("test"));

    EndpointLifecycle lifecycle = new EndpointLifecycle(
        "test",
        results,
        results);

    verifySuccess(lifecycle.started());
    verifySuccess(lifecycle.stopped());
  }

  @Test
  public void lifecycleError() {
    Flux<EndpointResult> results = Flux.just(
        error("bar",
            new RuntimeException("err")),
        error("test",
            new RuntimeException("err")));

    EndpointLifecycle lifecycle = new EndpointLifecycle(
        "test",
        results,
        results);

    verifyError(lifecycle.started());
    verifyError(lifecycle.stopped());
  }

  @Test
  public void lifecycleMissingName() {
    Flux<EndpointResult> results = Flux.just(
        success("bar"),
        success("baz"));

    EndpointLifecycle lifecycle = new EndpointLifecycle(
        "test",
        results,
        results);

    verifyMissingName(lifecycle.started());
    verifyMissingName(lifecycle.stopped());
  }

  static void verifySuccess(Mono<Endpoint> endpoint) {
    StepVerifier.create(endpoint)
        .expectNextMatches(endpointVal ->
            endpointVal.getName().equals("test"))
        .expectComplete()
        .verify(Duration.ofSeconds(1));
  }

  static void verifyError(Mono<Endpoint> endpoint) {
    StepVerifier.create(endpoint)
        .expectErrorMatches(err ->
            err instanceof RuntimeException && err.getMessage().equals("err"))
        .verify(Duration.ofSeconds(1));
  }

  static void verifyMissingName(Mono<Endpoint> endpoint) {
    StepVerifier.create(endpoint)
        .expectErrorMatches(err ->
            err instanceof MissingEndpointException
                && ((MissingEndpointException) err).getEndpointName().equals("test"))
        .verify(Duration.ofSeconds(1));
  }
}
