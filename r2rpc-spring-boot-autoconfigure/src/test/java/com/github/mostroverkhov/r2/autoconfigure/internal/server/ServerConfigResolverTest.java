package com.github.mostroverkhov.r2.autoconfigure.internal.server;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;
import com.github.mostroverkhov.r2.autoconfigure.RequestersProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.R2DataCodecJacksonJson;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.ServerEndpointProperties;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.apihandlers.ServerApiHandlersFactory;
import com.github.mostroverkhov.r2.autoconfigure.server.R2ServerTransport;
import com.github.mostroverkhov.r2.autoconfigure.server.ServerHandlersProvider;
import com.github.mostroverkhov.r2.codec.jackson.JacksonJsonDataCodec;
import com.github.mostroverkhov.r2.contract.R2Api;
import com.github.mostroverkhov.r2.core.ConnectionContext;
import com.github.mostroverkhov.r2.core.DataCodec;
import com.github.mostroverkhov.r2.core.Metadata;
import com.github.mostroverkhov.r2.core.RequesterFactory;
import com.github.mostroverkhov.r2.core.contract.RequestStream;
import com.github.mostroverkhov.r2.core.contract.Service;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ServerConfigResolverTest {

  private ServerConfigResolver resolver;

  @Before
  public void setUp() {
    RSocketFactory.ServerRSocketFactory serverRSocketFactory = RSocketFactory.receive();
    List<ServerHandlersProvider<?>> handlerProviders = asList(new BazServerHandlersProvider());
    List<R2DataCodec> dataCodecs = asList(new R2DataCodecJacksonJson());
    List<R2ServerTransport> transport = asList(new R2ServerTransportTcp());
    List<RequestersProvider> requestersProviders = asList(new BazRequestersProvider());
    resolver = new ServerConfigResolver(
        serverRSocketFactory,
        handlerProviders,
        dataCodecs,
        transport,
        requestersProviders);
  }

  @Test
  public void resolveAll() {
    Set<ServerConfig> resolved = resolver
        .resolve(Collections.singleton(validProps()));

    assertThat(resolved)
        .isNotNull()
        .hasSize(1);

    ServerConfig serverConfig = resolved.iterator().next();

    assertThat(serverConfig.transport())
        .isNotNull()
        .isExactlyInstanceOf(TcpServerTransport.class);

    List<DataCodec> codecs = serverConfig.codecs();
    assertThat(codecs)
        .isNotNull()
        .hasSize(1);
    assertThat(codecs.get(0)).isExactlyInstanceOf(JacksonJsonDataCodec.class);

    assertThat(serverConfig.rSocketFactory()).isNotNull();

    ServerApiHandlersFactory handlers = serverConfig.handlers();
    assertThat(handlers).isNotNull();

    ConnectionContext ctx = new ConnectionContext(
        new Metadata.Builder().build());
    MockRequesterFactory requesterFactory = new MockRequesterFactory();
    Collection<Object> actualHandlers = handlers
        .apply(ctx, requesterFactory);
    assertThat(actualHandlers).isNotNull().hasSize(1);
  }

  @Test
  public void resolveNoAPi() {

    ServerEndpointProperties props = validProps();
    props.setResponders(Collections.emptyList());
    Set<ServerConfig> resolved = resolver.resolve(Collections.singleton(props));

    assertThat(resolved)
        .isNotNull()
        .hasSize(1);

    ServerApiHandlersFactory handlers = resolved
        .iterator()
        .next()
        .handlers();
    assertThat(handlers).isNotNull();

    ConnectionContext ctx = new ConnectionContext(
        new Metadata.Builder().build());
    MockRequesterFactory requesterFactory = new MockRequesterFactory();

    Collection<Object> actualHandlers = handlers
        .apply(ctx, requesterFactory);
    assertThat(actualHandlers).isNotNull().hasSize(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveMissingTransport() {
    ServerEndpointProperties props = validProps();
    props.setTransport("absent");
    resolver.resolve(Collections.singleton(props));
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveMissingCodec() {
    ServerEndpointProperties props = validProps();
    props.setCodecs(Collections.singletonList("absent"));
    resolver.resolve(Collections.singleton(props));
  }

  private static ServerEndpointProperties validProps() {
    ServerEndpointProperties props = new ServerEndpointProperties();
    props.setName("test");
    props.setPort(8081);
    props.setResponders(asList("baz"));
    props.setTransport("tcp");
    props.setCodecs(Collections.singletonList("json"));
    props.setRequesters(Collections.emptyList());
    return props;
  }

  static class Baz {

    private String baz;

    public Baz(String baz) {
      this.baz = baz;
    }

    public Baz() {
    }

    public String getBaz() {
      return baz;
    }

    public void setBaz(String baz) {
      this.baz = baz;
    }
  }

  @R2Api(value = "baz")
  public interface BazApi {

    BazContract baz();
  }

  static class BazApiImpl implements BazApi {

    private final ConnectionContext connectionContext;

    public BazApiImpl(
        ConnectionContext connectionContext) {
      this.connectionContext = connectionContext;
    }

    @Override
    public BazContract baz() {
      return new BazHandler();
    }
  }

  @Service("baz")
  public interface BazContract {

    @RequestStream("bazStream")
    Flux<Baz> baz(Baz baz);
  }

  static class BazHandler implements BazContract {

    @Override
    public Flux<Baz> baz(Baz baz) {
      return Flux.just(new Baz("resp"));
    }
  }

  static class BazServerHandlersProvider implements ServerHandlersProvider<BazApiImpl> {

    @Override
    public BazApiImpl apply(ConnectionContext ctx, ApiRequesterFactory requesterFactory) {
      return new BazApiImpl(ctx);
    }
  }

  static class BazRequestersProvider extends RequestersProvider<BazRequestersProvider> {
  }

  static class MockRequesterFactory implements RequesterFactory {
    @Override
    public <T> T create(Class<T> clazz) {
      throw new UnsupportedOperationException("Mock!");
    }
  }
}
