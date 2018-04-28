package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.autoconfigure.server.ServerHandlersProvider;
import com.github.mostroverkhov.r2.contract.R2Api;
import com.github.mostroverkhov.r2.core.ConnectionContext;
import com.github.mostroverkhov.r2.core.contract.RequestStream;
import com.github.mostroverkhov.r2.core.contract.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;

class ServerHandlersResolverFixtures {

  static class RenamedValidServer implements ServerHandlersProvider<RenamedValidApiImpl> {

    @Override
    public RenamedValidApiImpl apply(ConnectionContext ctx,
                                     ApiRequesterFactory requesterFactory) {
      return new RenamedValidApiImpl();
    }
  }

  static class TwoApisProviderServer implements ServerHandlersProvider<TwoApisImpl> {

    @Override
    public TwoApisImpl apply(ConnectionContext ctx,
                             ApiRequesterFactory requesterFactory) {
      return new TwoApisImpl();
    }
  }

  static class NoApisProviderServer implements ServerHandlersProvider<ArrayList<String>> {

    @Override
    public ArrayList<String> apply(ConnectionContext ctx,
                                   ApiRequesterFactory requesterFactory) {
      return new ArrayList<>();
    }
  }

  static class ValidServer implements ServerHandlersProvider<ValidApiImpl> {

    @Override
    public ValidApiImpl apply(ConnectionContext ctx,
                              ApiRequesterFactory requesterFactory) {
      return new ValidApiImpl();
    }
  }

  static class AnotherValidServer implements ServerHandlersProvider<AnotherValidApiImpl> {

    @Override
    public AnotherValidApiImpl apply(ConnectionContext ctx,
                                     ApiRequesterFactory requesterFactory) {
      return new AnotherValidApiImpl(ctx);
    }
  }

  static class ValidNamedServer implements ServerHandlersProvider<NamedValidApiImpl> {

    @Override
    public NamedValidApiImpl apply(ConnectionContext ctx,
                                   ApiRequesterFactory apiRequesterFactory) {
      return new NamedValidApiImpl(ctx);
    }
  }


  @R2Api("named-contract")
  static class NamedValidApiImpl implements AnotherValidApi {

    public NamedValidApiImpl(ConnectionContext ctx) {
    }

    @Override
    public AnotherContract anotherContract() {
      return new AnotherContractHandler();
    }
  }

  static class AnotherValidApiImpl implements AnotherValidApi {

    public AnotherValidApiImpl(ConnectionContext ctx) {
    }

    @Override
    public AnotherContract anotherContract() {
      return new AnotherContractHandler();
    }
  }

  static class ValidApiImpl implements ValidApi {

    @Override
    public Contract contract() {
      return new ContractHandler();
    }
  }

  @R2Api("contract")
  interface ValidApi {

    Contract contract();
  }

  @Service("contract")
  interface Contract {

    @RequestStream("stream")
    Flux<String> stream(String request);
  }

  @R2Api("another-contract")
  interface AnotherValidApi {

    AnotherContract anotherContract();
  }

  static class ContractHandler implements Contract {

    @Override
    public Flux<String> stream(String request) {
      return Flux.just(request);
    }
  }

  @Service("another-contract")
  interface AnotherContract {

    @RequestStream("stream")
    Flux<String> anotherStream(String request);
  }

  static class AnotherContractHandler implements AnotherContract {

    @Override
    public Flux<String> anotherStream(String request) {
      return Flux.just(request);
    }
  }

  @R2Api("no-name")
  interface NonApi {

    AnotherContract contract();
  }

  @R2Api("renamed-contract")
  interface RenamedValidApi extends ValidApi {

  }

  static class RenamedValidApiImpl implements RenamedValidApi {

    @Override
    public Contract contract() {
      return new ContractHandler();
    }
  }

  static class TwoApisImpl implements ValidApi, AnotherValidApi {

    @Override
    public Contract contract() {
      return new ContractHandler();
    }

    @Override
    public AnotherContract anotherContract() {
      return new AnotherContractHandler();
    }
  }
}
