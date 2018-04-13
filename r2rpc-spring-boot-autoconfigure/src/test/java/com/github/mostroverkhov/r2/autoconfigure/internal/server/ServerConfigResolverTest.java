package com.github.mostroverkhov.r2.autoconfigure.internal.server;

import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;
import com.github.mostroverkhov.r2.autoconfigure.R2Api;
import com.github.mostroverkhov.r2.autoconfigure.internal.R2DataCodecJacksonJson;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.ResponderEndpointProperties;
import com.github.mostroverkhov.r2.autoconfigure.server.ResponderApiProvider;
import com.github.mostroverkhov.r2.autoconfigure.server.R2ServerTransport;
import com.github.mostroverkhov.r2.codec.jackson.JacksonJsonDataCodec;
import com.github.mostroverkhov.r2.core.DataCodec;
import com.github.mostroverkhov.r2.core.Metadata.Builder;
import com.github.mostroverkhov.r2.core.contract.RequestStream;
import com.github.mostroverkhov.r2.core.contract.Service;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import io.rsocket.RSocketFactory;
import io.rsocket.RSocketFactory.ServerRSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ServerConfigResolverTest {

  private ServerConfigResolver resolver;

  @Before
  public void setUp() {
    ServerRSocketFactory serverRSocketFactory = RSocketFactory.receive();
    List<ResponderApiProvider> apiProviders = asList(new BazResponderApiProvider());
    List<R2DataCodec> dataCodecs = asList(new R2DataCodecJacksonJson());
    List<R2ServerTransport> transport = asList(new R2ServerTransportTcp());

    resolver = new ServerConfigResolver(
        serverRSocketFactory,
        apiProviders,
        dataCodecs,
        transport);
  }

  @Test
  public void resolveAll() {
    Set<ServerConfig> resolved = resolver.resolve(Collections.singleton(validProps()));

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

    Function<ConnectionContext, Collection<Object>> handlers =
        serverConfig.handlers();
    assertThat(handlers).isNotNull();
    Collection<Object> actualHandlers = handlers
        .apply(new ConnectionContext(new Builder().build()));
    assertThat(actualHandlers).isNotNull().hasSize(1);
  }

  @Test
  public void resolveNoAPi() {

    ResponderEndpointProperties props = validProps();
    props.setApi(Collections.emptyList());
    Set<ServerConfig> resolved = resolver.resolve(Collections.singleton(props));

    assertThat(resolved)
        .isNotNull()
        .hasSize(1);

    Function<ConnectionContext, Collection<Object>> handlers = resolved
        .iterator()
        .next()
        .handlers();
    assertThat(handlers).isNotNull();
    Collection<Object> actualHandlers = handlers
        .apply(new ConnectionContext(new Builder().build()));
    assertThat(actualHandlers).isNotNull().hasSize(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveMissingTransport() {
    ResponderEndpointProperties props = validProps();
    props.setTransport("absent");
    resolver.resolve(Collections.singleton(props));
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveMissingCodec() {
    ResponderEndpointProperties props = validProps();
    props.setCodecs(Collections.singletonList("absent"));
    resolver.resolve(Collections.singleton(props));
  }

  private static ResponderEndpointProperties validProps() {
    ResponderEndpointProperties props = new ResponderEndpointProperties();
    props.setName("test");
    props.setPort(8081);
    props.setApi(asList("baz"));
    props.setTransport("tcp");
    props.setCodecs(Collections.singletonList("jackson-json"));
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

  static class BazResponderApiProvider implements ResponderApiProvider<BazApiImpl> {

    @Override
    public BazApiImpl apply(ConnectionContext connectionContext) {
      return new BazApiImpl(connectionContext);
    }
  }
}
