package com.github.mostroverkhov.r2.example.api.bar;

import com.github.mostroverkhov.r2.core.contract.RequestStream;
import com.github.mostroverkhov.r2.core.contract.Service;
import reactor.core.publisher.Flux;

@Service("bar")
public interface BarContract {

  @RequestStream("barStream")
  Flux<Bar> bar(Bar bar);
}
