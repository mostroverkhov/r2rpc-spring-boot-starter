package com.github.mostroverkhov.r2.autoconfigure.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.mostroverkhov.r2.autoconfigure.R2Api;
import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;
import com.github.mostroverkhov.r2.autoconfigure.ServerApiProvider;
import com.github.mostroverkhov.r2.autoconfigure.R2ServerTransport;
import com.github.mostroverkhov.r2.autoconfigure.ServerTransportFactory;
import com.github.mostroverkhov.r2.codec.jackson.JacksonJsonDataCodec;
import com.github.mostroverkhov.r2.core.DataCodec;
import com.github.mostroverkhov.r2.core.Metadata.Builder;
import com.github.mostroverkhov.r2.core.contract.RequestStream;
import com.github.mostroverkhov.r2.core.contract.Service;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import io.rsocket.RSocketFactory;
import io.rsocket.RSocketFactory.ServerRSocketFactory;
import io.rsocket.transport.netty.server.NettyContextCloseable;
import io.rsocket.transport.netty.server.TcpServerTransport;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerConfigResolverTest {

  private ServerConfigResolver resolver;

  @Autowired
  GenericApplicationContext ctx;

  @Before
  public void setUp() {
    resolver = new ServerConfigResolver(ctx);
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

    R2Properties props = validProps();
    props.setApi(null);
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
    R2Properties props = validProps();
    props.setTransport("absent");
    resolver.resolve(Collections.singleton(props));
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveMissingCodec() {
    R2Properties props = validProps();
    props.setCodecs(Collections.singletonList("absent"));
    resolver.resolve(Collections.singleton(props));
  }

  private static R2Properties validProps() {
    R2Properties props = new R2Properties();
    props.setName("test");
    props.setPort(8081);
    props.setApi(Arrays.asList("baz"));
    props.setTransport("tcp");
    props.setCodecs(Collections.singletonList("jackson"));
    return props;
  }

  @Configuration
  static class TestConfig {

    @Bean
    public BazServerApiProvider handlers() {
      return new BazServerApiProvider();
    }

    @Bean
    public ServerRSocketFactory defaultRSocketFactory() {
      return RSocketFactory.receive();
    }

    @Bean
    @R2DataCodec("jackson")
    public DataCodec defaultCodec() {
      return new JacksonJsonDataCodec();
    }

    @Bean
    @R2ServerTransport("tcp")
    public ServerTransportFactory<NettyContextCloseable> defaultTransport() {
      return TcpServerTransport::create;
    }
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

  static class BazServerApiProvider implements ServerApiProvider<BazApiImpl> {

    @Override
    public BazApiImpl apply(ConnectionContext connectionContext) {
      return new BazApiImpl(connectionContext);
    }
  }
}
