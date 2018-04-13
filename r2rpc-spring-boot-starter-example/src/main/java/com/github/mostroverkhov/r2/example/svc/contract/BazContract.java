package com.github.mostroverkhov.r2.example.svc.contract;

import com.github.mostroverkhov.r2.core.contract.RequestStream;
import com.github.mostroverkhov.r2.core.contract.Service;
import com.github.mostroverkhov.r2.example.svc.Baz;
import reactor.core.publisher.Flux;

@Service("baz")
public interface BazContract {

  @RequestStream("bazStream")
  Flux<Baz> baz(Baz baz);
}
