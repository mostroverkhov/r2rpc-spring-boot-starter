package com.github.mostroverkhov.r2.example.api;

import com.github.mostroverkhov.r2.example.api.bar.Bar;
import com.github.mostroverkhov.r2.example.api.bar.BarContract;
import java.time.Duration;
import reactor.core.publisher.Flux;

public class BarHandler implements BarContract {

  @Override
  public Flux<Bar> bar(Bar bar) {
    return Flux.interval(Duration.ofSeconds(3)).map(v -> new Bar(String.valueOf(v)));
  }
}
