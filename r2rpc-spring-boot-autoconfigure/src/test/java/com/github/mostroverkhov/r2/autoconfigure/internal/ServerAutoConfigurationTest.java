package com.github.mostroverkhov.r2.autoconfigure.internal;


import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mostroverkhov.r2.autoconfigure.ServerTransportFactory;
import com.github.mostroverkhov.r2.codec.jackson.JacksonJsonDataCodec;
import io.rsocket.RSocketFactory.ServerRSocketFactory;
import io.rsocket.transport.netty.NettyDuplexConnection;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

public class ServerAutoConfigurationTest {
  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(R2ServerAutoConfiguration.class));

  @Test
  public void defaultBeans() {
    contextRunner.run(ctx -> {
      assertThat(ctx).hasSingleBean(ServerRSocketFactory.class);
      assertThat(ctx).hasSingleBean(ServersLifecycle.class);
      assertThat(ctx).hasSingleBean(JacksonJsonDataCodec.class);
      assertThat(ctx).hasSingleBean(ServerTransportFactory.class);
    });
  }

  @Test
  public void noJacksonOnClasspath() {
    this.contextRunner.withClassLoader(new FilteredClassLoader(ObjectMapper.class))
        .run((context) -> assertThat(context).doesNotHaveBean(JacksonJsonDataCodec.class));
  }

  @Test
  public void noNettyTransportOnClasspath() {
    this.contextRunner.withClassLoader(new FilteredClassLoader(NettyDuplexConnection.class))
        .run((context) -> assertThat(context).doesNotHaveBean(ServerTransportFactory.class));
  }
}
