package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.internal.ServersLifecycle.ServersStarter;
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

  @Before
  public void setUp() {
    factory = new CloseableFactory(200, 200);
  }

  @Test
  public void startsSuccessfully() {
    ServersStarter starter = new ServersStarter(starts());
    StepVerifier.create(starter.start()
        .flatMapIterable(l -> l))
        .expectNextCount(5)
        .expectComplete()
        .verify(Duration.ofSeconds(10));
  }

  @Test
  public void startsPartialSuccessfully() {
    StepVerifier.create(new ServersStarter(startsWithError())
        .start()
        .flatMapIterable(l -> l))
        .expectNextCount(4)
        .expectComplete()
        .verify(Duration.ofSeconds(10));
  }

  @Test
  public void closesSucessfully() {
    ServersStarter starter = new ServersStarter(starts());
    StepVerifier.create(starter
        .start()
        .then(starter.stop())
        .then(starter.onStop()))
        .expectComplete()
        .verify(Duration.ofSeconds(10));
  }

  @Test
  public void closesPartialSucessfully() {
    ServersStarter starter = new ServersStarter(startsWithError());
    StepVerifier.create(starter.start()
        .then(starter.stop()).then(starter.onStop()))
        .expectComplete()
        .verify(Duration.ofSeconds(10));
  }

  private List<Mono<Closeable>> startsWithError() {
    return Flux.range(0, 5)
        .map(v -> {
          if (v == 4) {
            return factory.error();
          } else {
            return factory.create();
          }
        })
        .collectList()
        .block();
  }

  private List<Mono<Closeable>> starts() {
    return Flux.range(0, 5)
          .map(v -> factory.create())
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

    public Mono<Closeable> create() {
      return Mono.delay(Duration.ofMillis(monoDelayMillis))
          .map(l -> new MockCloseable(closeDelayMillis));
    }

    public Mono<Closeable> error() {
      return Mono.delay(Duration.ofMillis(monoDelayMillis))
          .flatMap(l -> Mono.error(new Throwable("err")));
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
