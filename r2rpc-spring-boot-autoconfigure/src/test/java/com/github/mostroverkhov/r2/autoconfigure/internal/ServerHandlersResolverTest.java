package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.internal.ServerHandlersResolverFixtures.*;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.apihandlers.ServerApiHandlersFactory;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.apihandlers.ServerHandlersResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.apihandlers.ServerHandlersInfo;
import com.github.mostroverkhov.r2.autoconfigure.server.ServerHandlersProvider;
import com.github.mostroverkhov.r2.core.ConnectionContext;
import com.github.mostroverkhov.r2.core.Metadata;
import com.github.mostroverkhov.r2.core.RequesterFactory;
import io.rsocket.RSocketFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ServerHandlersResolverTest {

  @Test
  public void resolveSingleValidProvider() {
    ServerHandlersResolver handlersResolver = fromProviders(
        new ValidServer());
    Collection<ServerHandlersInfo> responderInfos = handlersResolver.resolveAll();
    assertThat(responderInfos).hasSize(1);
    assertThat(responderInfos)
        .flatExtracting(ServerHandlersInfo::name, ServerHandlersInfo::type)
        .containsExactly("contract", ValidApi.class);
  }

  @Test
  public void resolveSingleValidProviderApiAndApiName() {
    ServerHandlersResolver handlersResolver = fromProviders(
        new ValidNamedServer());
    Collection<ServerHandlersInfo> responderInfos = handlersResolver.resolveAll();
    assertThat(responderInfos).hasSize(1);
    assertThat(responderInfos)
        .flatExtracting(ServerHandlersInfo::name, ServerHandlersInfo::type)
        .contains("named-contract", AnotherValidApi.class);
  }

  @Test
  public void resolveMultipleValidProviders() {
    ServerHandlersResolver handlersResolver = fromProviders(
        new ValidServer(),
        new ValidNamedServer());
    Collection<ServerHandlersInfo> responderInfos = handlersResolver.resolveAll();
    assertThat(responderInfos).hasSize(2);
    assertThat(responderInfos)
        .flatExtracting(ServerHandlersInfo::name, ServerHandlersInfo::type)
        .contains(
            "named-contract",
            AnotherValidApi.class,
            "contract",
            ValidApi.class);
  }

  @Test
  public void resolveProviderRenamed() {
    ServerHandlersResolver handlersResolver = fromProviders(
        new RenamedValidServer());
    Collection<ServerHandlersInfo> responderInfos = handlersResolver.resolveAll();
    assertThat(responderInfos).hasSize(1);
    assertThat(responderInfos)
        .flatExtracting(ServerHandlersInfo::name, ServerHandlersInfo::type)
        .contains(
            "renamed-contract",
            RenamedValidApi.class);
  }

  @Test
  public void resolveSingleProviderNoApis() {
    ServerHandlersResolver handlersResolver = fromProviders(
        new NoApisProviderServer());
    Collection<ServerHandlersInfo> responderInfos = handlersResolver.resolveAll();
    assertThat(responderInfos).isEmpty();
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveSingleProviderMultipleApis() {
    ServerHandlersResolver handlersResolver = fromProviders(
        new TwoApisProviderServer());
    handlersResolver.resolveAll();
  }

  @Test
  public void resolveServiceFactoryMultipleApis() {
    ServerHandlersResolver handlersResolver = fromProviders(
        new ValidServer(),
        new ValidNamedServer());
    List<String> keys = keys("contract", "named-contract");

    ServerApiHandlersFactory serverHandlersFactory = handlersResolver
        .resolve(args(keys));

    assertThat(serverHandlersFactory).isNotNull();

    ConnectionContext connCtx = new ConnectionContext(new Metadata.Builder().build());
    MockRequesterFactory requesterFactory = new MockRequesterFactory();
    Collection<Object> svcHandlers = serverHandlersFactory.apply(connCtx, requesterFactory);
    assertThat(svcHandlers).hasSize(2);
    Iterator<Object> it = svcHandlers.iterator();
    assertThat(it.next()).isInstanceOfAny(Contract.class, AnotherContract.class);
    assertThat(it.next()).isInstanceOfAny(Contract.class, AnotherContract.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveServiceFactorySameApisTypePerEndpoint() {
    ServerHandlersResolver handlersResolver = new ServerHandlersResolver(
        Arrays.asList(
            new AnotherValidServer(),
            new ValidNamedServer()));

    List<String> keys = keys("another-contract", "named-contract");
    handlersResolver.resolve(args(keys));
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveMultipleProvidersDuplicateApi() {
    ServerHandlersResolver handlersResolver = fromProviders(
        new ValidServer(),
        new ValidServer());
    handlersResolver.resolve(args(keys("contract")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveServiceFactoryDuplicateKeys() {
    ServerHandlersResolver handlersResolver = fromProviders(
        new ValidServer());
    handlersResolver.resolve(args(keys("contract", "contract")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveServiceFactoryMissingApi() {
    ServerHandlersResolver handlersResolver = fromProviders(
        new ValidServer());
    handlersResolver.resolve(args(keys("missing")));
  }

  @NotNull
  private ApiHandlersArgs args(List<String> keys) {
    return new ApiHandlersArgs(keys, Collections.emptyList());
  }

  static List<String> keys(String... keys) {
    return Arrays.asList(keys);
  }

  static ServerHandlersResolver fromProviders(ServerHandlersProvider<?>... providers) {
    return new ServerHandlersResolver(Arrays.asList(providers));
  }

  static class MockRequesterFactory implements RequesterFactory {
    @Override
    public <T> T create(Class<T> clazz) {
      throw new UnsupportedOperationException("Mock!");
    }
  }
}
