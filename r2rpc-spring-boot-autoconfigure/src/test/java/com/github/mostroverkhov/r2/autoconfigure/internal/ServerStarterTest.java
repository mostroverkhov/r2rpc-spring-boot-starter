package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.internal.server.R2ServersLifecycle.NamedStart;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.R2ServersLifecycle.ServersStarter;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.endpoints.EndpointResult;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.endpoints.EndpointSupport;
import io.rsocket.Closeable;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;

public class ServerStarterTest {

  private CloseableFactory factory;

  @Before
  public void setUp() {
    factory = new CloseableFactory(200, 200);
  }

  @Test
  public void startsSuccessfully() {
    EndpointSupport endpointSupport = new EndpointSupport();
    ServersStarter starter = new ServersStarter(endpointSupport, starts());
    StepVerifier.create(starter.start()
        .flatMapIterable(l -> l))
        .expectNextCount(5)
        .expectComplete()
        .verify(Duration.ofSeconds(10));

    StepVerifier.create(
        endpointSupport.starts().collectList())
        .expectNextMatches(list ->
            contains(
                list,
                asList("0", "1", "2", "3", "4"),
                emptyList()))
        .expectComplete()
        .verify(Duration.ofSeconds(1));
  }

  @Test
  public void startsPartialSuccessfully() {
    EndpointSupport endpointSupport = new EndpointSupport();
    ServersStarter starter = new ServersStarter(endpointSupport, startsWithError());
    StepVerifier.create(starter
        .start()
        .flatMapIterable(l -> l))
        .expectNextCount(4)
        .expectComplete()
        .verify(Duration.ofSeconds(10));

    StepVerifier.create(
        endpointSupport.starts().collectList())
        .expectNextMatches(list ->
            contains(
                list,
                asList("0", "1", "2", "3"),
                singletonList("4")))
        .expectComplete()
        .verify(Duration.ofSeconds(1));
  }

  @Test
  public void closesSuccessfully() {
    EndpointSupport endpointSupport = new EndpointSupport();
    ServersStarter starter = new ServersStarter(endpointSupport, starts());
    StepVerifier.create(starter
        .start()
        .then(starter.stop())
        .then(starter.onStop()))
        .expectComplete()
        .verify(Duration.ofSeconds(10));

    StepVerifier.create(
        endpointSupport.stops().collectList())
        .expectNextMatches(list ->
            contains(
                list,
                asList("0", "1", "2", "3", "4"),
                emptyList()))
        .expectComplete()
        .verify(Duration.ofSeconds(1));

  }

  @Test
  public void closesPartialSucessfully() {
    EndpointSupport endpointSupport = new EndpointSupport();
    ServersStarter starter = new ServersStarter(endpointSupport, startsWithError());
    StepVerifier.create(starter.start()
        .then(starter.stop()).then(starter.onStop()))
        .expectComplete()
        .verify(Duration.ofSeconds(10));

    StepVerifier.create(
        endpointSupport.stops().collectList())
        .expectNextMatches(list ->
            contains(
                list,
                asList("0", "1", "2", "3"),
                emptyList()))
        .expectComplete()
        .verify(Duration.ofSeconds(1));

  }

  private static boolean contains(
      List<EndpointResult> endpointResults,
      List<String> succNames,
      List<String> errNames) {

    return
        contains(
            endpointResults,
            succNames,
            endpointResult -> !endpointResult.isError())
            &&
            contains(
                endpointResults,
                errNames,
                EndpointResult::isError);
  }

  private static boolean contains(List<EndpointResult> endpointResults,
      List<String> names,
      Predicate<EndpointResult> pred) {

    Set<String> resultNames = endpointResults
        .stream()
        .filter(pred)
        .map(EndpointResult::getName)
        .collect(toSet());

    return resultNames.size() == names.size()
        && resultNames.containsAll(names);
  }

  private List<NamedStart> startsWithError() {
    return Flux.range(0, 5)
        .map(v -> {
          String val = String.valueOf(v);
          if (v == 4) {
            return factory.error(val);
          } else {
            return factory.create(val);
          }
        })
        .collectList()
        .block();
  }

  private List<NamedStart> starts() {
    return Flux.range(0, 5)
        .map(v -> factory.create(String.valueOf(v)))
        .collectList()
        .block();
  }

  static class CloseableFactory {

    private final int monoDelayMillis;
    private final int closeDelayMillis;

    public CloseableFactory(
        int monoDelayMillis,
        int closeDelayMillis) {

      this.monoDelayMillis = monoDelayMillis;
      this.closeDelayMillis = closeDelayMillis;
    }

    public NamedStart create(String name) {
      Mono<Closeable> closeable = Mono.delay(Duration.ofMillis(monoDelayMillis))
          .map(l -> new MockCloseable(closeDelayMillis));
      return new NamedStart(name, closeable);
    }

    public NamedStart error(String name) {
      Mono<Closeable> err = Mono.delay(Duration.ofMillis(monoDelayMillis))
          .flatMap(l -> Mono.error(new Throwable("err")));
      return new NamedStart(name, err);
    }

  }

  static class MockCloseable implements Closeable {

    private final int delayMillis;
    private final MonoProcessor<Void> close = MonoProcessor.create();
    private AtomicBoolean once = new AtomicBoolean();

    public MockCloseable(int delayMillis) {
      this.delayMillis = delayMillis;
    }

    @Override
    public Mono<Void> close() {
      if (once.compareAndSet(false, true)) {
        Mono.delay(Duration.ofMillis(delayMillis))
            .then().subscribe(close);
      }
      return Mono.empty();
    }

    @Override
    public Mono<Void> onClose() {
      return close;
    }
  }
}
