package com.github.mostroverkhov.r2.example.svc.contract;

import com.github.mostroverkhov.r2.core.contract.RequestStream;
import com.github.mostroverkhov.r2.core.contract.Service;
import com.github.mostroverkhov.r2.example.svc.Bar;
import reactor.core.publisher.Flux;

@Service("bar")
public interface BarContract {

  @RequestStream("barStream")
  Flux<Bar> bar(Bar bar);
}
