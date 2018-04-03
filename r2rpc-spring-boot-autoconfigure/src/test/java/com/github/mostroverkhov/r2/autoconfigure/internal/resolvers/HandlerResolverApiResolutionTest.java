package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

import static com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlerResolverApiResolutionFixtures.ValidApiProvider;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.mostroverkhov.r2.autoconfigure.ServerApiProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlerResolverApiResolutionFixtures.AnotherContract;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlerResolverApiResolutionFixtures.AnotherValidApi;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlerResolverApiResolutionFixtures.AnotherValidApiProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlerResolverApiResolutionFixtures.Contract;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlerResolverApiResolutionFixtures.DanglingApiNameProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlerResolverApiResolutionFixtures.NoApisProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlerResolverApiResolutionFixtures.RenamedValidApi;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlerResolverApiResolutionFixtures.RenamedValidApiProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlerResolverApiResolutionFixtures.TwoApisProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlerResolverApiResolutionFixtures.ValidApi;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlerResolverApiResolutionFixtures.ValidNamedApiProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlersResolver.Api;
import com.github.mostroverkhov.r2.core.Metadata.Builder;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.junit.Test;

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
  public void resolveSingleProviderApiNameOnly() {
    HandlersResolver handlersResolver = fromProviders(
            new DanglingApiNameProvider());
    handlersResolver.resolveAll();
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
    Set<String> keys = keys("contract", "named-contract");
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
    HandlersResolver handlersResolver = new HandlersResolver(() ->
        Arrays.asList(
            new AnotherValidApiProvider(),
            new ValidNamedApiProvider()));

    Set<String> keys = keys("another-contract", "named-contract");
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
  public void resolveProvidersMissingApi() {
    HandlersResolver handlersResolver = fromProviders(
        new ValidApiProvider());
    handlersResolver.resolve(keys("missing"));
  }

  static Set<String> keys(String... keys) {
    return new HashSet<>(Arrays.asList(keys));
  }

  static HandlersResolver fromProviders(ServerApiProvider<?>... providers) {
    return new HandlersResolver(() -> Arrays.asList(providers));
  }
}
