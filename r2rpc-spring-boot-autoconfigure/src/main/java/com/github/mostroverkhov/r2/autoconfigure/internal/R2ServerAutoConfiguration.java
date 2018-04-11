package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;
import com.github.mostroverkhov.r2.autoconfigure.server.R2ServerTransport;
import com.github.mostroverkhov.r2.autoconfigure.server.ServerTransportFactory;
import com.github.mostroverkhov.r2.autoconfigure.internal.endpoints.EndpointSupport;
import com.github.mostroverkhov.r2.autoconfigure.server.endpoints.ServerControls;
import com.github.mostroverkhov.r2.codec.jackson.JacksonJsonDataCodec;
import com.github.mostroverkhov.r2.core.DataCodec;
import io.rsocket.RSocketFactory;
import io.rsocket.RSocketFactory.ServerRSocketFactory;
import io.rsocket.transport.netty.NettyDuplexConnection;
import io.rsocket.transport.netty.server.NettyContextCloseable;
import io.rsocket.transport.netty.server.TcpServerTransport;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.GenericApplicationContext;

@Configuration
@EnableConfigurationProperties({
    R2DefaultProperties.class,
    R2RootProperties.class
})
public class R2ServerAutoConfiguration {

  @Autowired
  private GenericApplicationContext applicationContext;
  @Autowired
  private R2DefaultProperties defaultProperties;
  @Autowired
  private R2RootProperties r2RootProperties;

  @ConditionalOnMissingBean
  @Bean
  @Scope("prototype")
  public ServerRSocketFactory defaultRSocketFactory() {
    return RSocketFactory.receive();
  }

  @Bean
  @ConditionalOnClass(ObjectMapper.class)
  @R2DataCodec("jackson")
  public DataCodec defaultCodec() {
    return new JacksonJsonDataCodec();
  }

  @Bean
  @ConditionalOnClass(NettyDuplexConnection.class)
  @R2ServerTransport("tcp")
  public ServerTransportFactory<NettyContextCloseable> defaultTransport() {
    return TcpServerTransport::create;
  }

  @Bean
  EndpointSupport endpointSupport() {
    return new EndpointSupport();
  }

  @Bean
  public ServerControls serverControls(EndpointSupport endpointSupport) {
    return new ServerControls(
        endpointSupport.starts(),
        endpointSupport.stops());
  }

  @Bean
  public ServersLifecycle serversStarter() {
    return new ServersLifecycle.Builder(
        fallbackProps(),
        applicationContext)
        .build(
            defaultProperties,
            r2RootProperties.getEndpoints(),
            endpointSupport());
  }

  static R2DefaultProperties fallbackProps() {
    R2DefaultProperties r2DefaultProperties = new R2DefaultProperties();
    r2DefaultProperties.setTransport("tcp");
    r2DefaultProperties.setCodecs(Collections.singletonList("jackson"));
    return r2DefaultProperties;
  }
}
