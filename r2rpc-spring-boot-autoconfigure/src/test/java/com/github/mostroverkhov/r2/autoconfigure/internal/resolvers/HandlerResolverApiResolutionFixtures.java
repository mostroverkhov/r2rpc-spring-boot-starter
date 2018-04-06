package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

import com.github.mostroverkhov.r2.autoconfigure.R2Api;
import com.github.mostroverkhov.r2.autoconfigure.server.ServerApiProvider;
import com.github.mostroverkhov.r2.core.contract.RequestStream;
import com.github.mostroverkhov.r2.core.contract.Service;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import java.util.ArrayList;
import reactor.core.publisher.Flux;

class HandlerResolverApiResolutionFixtures {

  static class RenamedValidApiProvider implements ServerApiProvider<RenamedValidApiImpl> {

    @Override
    public RenamedValidApiImpl apply(ConnectionContext connectionContext) {
      return new RenamedValidApiImpl();
    }
  }

  static class TwoApisProvider implements ServerApiProvider<TwoApisImpl> {

    @Override
    public TwoApisImpl apply(ConnectionContext connectionContext) {
      return new TwoApisImpl();
    }
  }

  static class NoApisProvider implements ServerApiProvider<ArrayList<String>> {

    @Override
    public ArrayList<String> apply(ConnectionContext connectionContext) {
      return new ArrayList<>();
    }
  }

  static class ValidApiProvider implements ServerApiProvider<ValidApiImpl> {

    @Override
    public ValidApiImpl apply(ConnectionContext connectionContext) {
      return new ValidApiImpl();
    }
  }

  static class AnotherValidApiProvider implements ServerApiProvider<AnotherValidApiImpl> {

    @Override
    public AnotherValidApiImpl apply(ConnectionContext connectionContext) {
      return new AnotherValidApiImpl(connectionContext);
    }
  }

  static class ValidNamedApiProvider implements ServerApiProvider<NamedValidApiImpl> {

    @Override
    public NamedValidApiImpl apply(ConnectionContext connectionContext) {
      return new NamedValidApiImpl(connectionContext);
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
