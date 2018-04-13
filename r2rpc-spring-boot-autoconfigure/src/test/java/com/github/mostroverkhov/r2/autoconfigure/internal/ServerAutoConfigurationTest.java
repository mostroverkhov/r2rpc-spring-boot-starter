package com.github.mostroverkhov.r2.autoconfigure.internal;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.R2ServerAutoConfiguration;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.R2ServersLifecycle;
import com.github.mostroverkhov.r2.autoconfigure.server.R2ServerTransport;
import com.github.mostroverkhov.r2.autoconfigure.server.endpoints.ServerControls;
import com.github.mostroverkhov.r2.codec.jackson.JacksonJsonDataCodec;
import io.rsocket.RSocketFactory.ServerRSocketFactory;
import io.rsocket.transport.netty.NettyDuplexConnection;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class ServerAutoConfigurationTest {
  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(R2ServerAutoConfiguration.class));

  @Test
  public void defaultBeans() {
    contextRunner.run(ctx -> {
      assertThat(ctx).hasSingleBean(ServerRSocketFactory.class);
      String beanName = ctx.getBeanNamesForType(ServerRSocketFactory.class)[0];
      assertThat(ctx.isPrototype(beanName)).isTrue();
      assertThat(ctx).hasSingleBean(R2ServersLifecycle.class);
      assertThat(ctx).hasSingleBean(ServerControls.class);
      assertThat(ctx).hasSingleBean(R2DataCodecJacksonJson.class);
      assertThat(ctx).hasSingleBean(R2ServerTransport.class);
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
        .run((context) -> assertThat(context).doesNotHaveBean(R2ServerTransport.class));
  }
}
