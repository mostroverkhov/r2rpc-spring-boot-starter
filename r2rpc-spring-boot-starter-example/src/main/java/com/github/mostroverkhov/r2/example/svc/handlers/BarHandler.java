package com.github.mostroverkhov.r2.example.svc.handlers;

import com.github.mostroverkhov.r2.example.api.BazApi;
import com.github.mostroverkhov.r2.example.svc.Bar;
import com.github.mostroverkhov.r2.example.svc.Baz;
import com.github.mostroverkhov.r2.example.svc.contract.BarContract;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

public class BarHandler implements BarContract {
  private static final Logger logger = LoggerFactory.getLogger(BarHandler.class);

  public BarHandler(BazApi bazApi) {
    bazApi
        .bazContract()
        .baz(new Baz("77"))
        .subscribe(
            baz ->
                logger.debug("Server Got Baz response: " + baz),
            err ->
                logger.debug("Server Got Baz error: " + err));
  }

  @Override
  public Flux<Bar> bar(Bar bar) {
    return Flux.interval(Duration.ofSeconds(3))
        .map(v -> new Bar(String.valueOf(v)));
  }
}
