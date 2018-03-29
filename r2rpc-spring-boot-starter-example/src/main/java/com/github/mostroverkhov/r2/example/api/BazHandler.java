package com.github.mostroverkhov.r2.example.api;

import java.time.Duration;
import reactor.core.publisher.Flux;

public class BazHandler implements BazContract {

  @Override
  public Flux<Baz> baz(Baz baz) {
    return Flux.interval(Duration.ofSeconds(1)).map(v -> new Baz(String.valueOf(v)));
  }
}