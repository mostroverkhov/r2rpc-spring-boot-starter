package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.internal.ServersLifecycle.NamedStart;
import com.github.mostroverkhov.r2.autoconfigure.internal.ServersLifecycle.ServersStarter;
import com.github.mostroverkhov.r2.autoconfigure.server.endpoints.EndpointSupport;
import io.rsocket.Closeable;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.test.StepVerifier;

public class ServerStarterTest {

  private CloseableFactory factory;
  private ServersStarter starter;

  @Before
  public void setUp() {
    factory = new CloseableFactory(200, 200);
    starter = new ServersStarter(new EndpointSupport(), starts());
  }

  @Test
  public void startsSuccessfully() {
    StepVerifier.create(starter.start()
        .flatMapIterable(l -> l))
        .expectNextCount(5)
        .expectComplete()
        .verify(Duration.ofSeconds(10));
  }

  @Test
  public void startsPartialSuccessfully() {
    StepVerifier.create(starter
        .start()
        .flatMapIterable(l -> l))
        .expectNextCount(4)
        .expectComplete()
        .verify(Duration.ofSeconds(10));
  }

  @Test
  public void closesSucessfully() {
    StepVerifier.create(starter
        .start()
        .then(starter.stop())
        .then(starter.onStop()))
        .expectComplete()
        .verify(Duration.ofSeconds(10));
  }

  @Test
  public void closesPartialSucessfully() {
    StepVerifier.create(starter.start()
        .then(starter.stop()).then(starter.onStop()))
        .expectComplete()
        .verify(Duration.ofSeconds(10));
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
