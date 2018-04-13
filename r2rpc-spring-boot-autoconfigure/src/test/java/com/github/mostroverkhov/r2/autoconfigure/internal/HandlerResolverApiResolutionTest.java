package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.internal.HandlerResolverApiResolutionFixtures.*;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.HandlersResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.HandlersResolver.Api;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.ServiceHandlersFactory;
import com.github.mostroverkhov.r2.autoconfigure.server.ResponderApiProvider;
import com.github.mostroverkhov.r2.core.Metadata.Builder;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HandlerResolverApiResolutionTest {

  @Test
  public void resolveSingleValidProvider() {
    HandlersResolver handlersResolver = fromProviders(
        new ValidApiProvider());
    Collection<Api> apis = handlersResolver.resolveAll();
    assertThat(apis).hasSize(1);
    assertThat(apis)
        .flatExtracting(Api::name, Api::type)
        .containsExactly("contract", ValidApi.class);
  }

  @Test
  public void resolveSingleValidProviderApiAndApiName() {
    HandlersResolver handlersResolver = fromProviders(
        new ValidNamedApiProvider());
    Collection<Api> apis = handlersResolver.resolveAll();
    assertThat(apis).hasSize(1);
    assertThat(apis)
        .flatExtracting(Api::name, Api::type)
        .contains("named-contract", AnotherValidApi.class);
  }

  @Test
  public void resolveMultipleValidProviders() {
    HandlersResolver handlersResolver = fromProviders(
        new ValidApiProvider(),
        new ValidNamedApiProvider());
    Collection<Api> apis = handlersResolver.resolveAll();
    assertThat(apis).hasSize(2);
    assertThat(apis)
        .flatExtracting(Api::name, Api::type)
        .contains(
            "named-contract",
            AnotherValidApi.class,
            "contract",
            ValidApi.class);
  }

  @Test
  public void resolveProviderRenamed() {
    HandlersResolver handlersResolver = fromProviders(
        new RenamedValidApiProvider());
    Collection<Api> apis = handlersResolver.resolveAll();
    assertThat(apis).hasSize(1);
    assertThat(apis)
        .flatExtracting(Api::name, Api::type)
        .contains(
            "renamed-contract",
            RenamedValidApi.class);
  }

  @Test
  public void resolveSingleProviderNoApis() {
    HandlersResolver handlersResolver = fromProviders(
        new NoApisProvider());
    Collection<Api> apis = handlersResolver.resolveAll();
    assertThat(apis).isEmpty();
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveSingleProviderMultipleApis() {
    HandlersResolver handlersResolver = fromProviders(
        new TwoApisProvider());
    handlersResolver.resolveAll();
  }

  @Test
  public void resolveServiceFactoryMultipleApis() {
    HandlersResolver handlersResolver = fromProviders(
        new ValidApiProvider(),
        new ValidNamedApiProvider());
    List<String> keys = keys("contract", "named-contract");
    ServiceHandlersFactory serviceHandlersFactory = handlersResolver.resolve(keys);

    assertThat(serviceHandlersFactory).isNotNull();

    ConnectionContext connCtx = new ConnectionContext(new Builder().build());
    Collection<Object> svcHandlers = serviceHandlersFactory.apply(connCtx);
    assertThat(svcHandlers).hasSize(2);
    Iterator<Object> it = svcHandlers.iterator();
    assertThat(it.next()).isInstanceOfAny(Contract.class, AnotherContract.class);
    assertThat(it.next()).isInstanceOfAny(Contract.class, AnotherContract.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveServiceFactorySameApisTypePerEndpoint() {
    HandlersResolver handlersResolver = new HandlersResolver(
        Arrays.asList(
            new AnotherValidApiProvider(),
            new ValidNamedApiProvider()));

    List<String> keys = keys("another-contract", "named-contract");
    handlersResolver.resolve(keys);
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveMultipleProvidersDuplicateApi() {
    HandlersResolver handlersResolver = fromProviders(
        new ValidApiProvider(),
        new ValidApiProvider());
    handlersResolver.resolve(keys("contract"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveServiceFactoryDuplicateKeys() {
    HandlersResolver handlersResolver = fromProviders(
        new ValidApiProvider());
    handlersResolver.resolve(keys("contract", "contract"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveServiceFactoryMissingApi() {
    HandlersResolver handlersResolver = fromProviders(
        new ValidApiProvider());
    handlersResolver.resolve(keys("missing"));
  }

  static List<String> keys(String... keys) {
    return Arrays.asList(keys);
  }

  static HandlersResolver fromProviders(ResponderApiProvider<?>... providers) {
    return new HandlersResolver(Arrays.asList(providers));
  }
}
