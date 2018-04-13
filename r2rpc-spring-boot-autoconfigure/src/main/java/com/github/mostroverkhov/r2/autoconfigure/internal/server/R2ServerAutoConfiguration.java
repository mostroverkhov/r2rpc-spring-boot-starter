package com.github.mostroverkhov.r2.autoconfigure.internal.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;
import com.github.mostroverkhov.r2.autoconfigure.internal.R2DataCodecJacksonJson;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.*;
import com.github.mostroverkhov.r2.autoconfigure.server.ResponderApiProvider;
import com.github.mostroverkhov.r2.autoconfigure.server.R2ServerTransport;
import com.github.mostroverkhov.r2.autoconfigure.server.endpoints.ServerControls;
import io.rsocket.RSocketFactory;
import io.rsocket.RSocketFactory.ServerRSocketFactory;
import io.rsocket.transport.netty.NettyDuplexConnection;
import io.rsocket.transport.netty.server.NettyContextCloseable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Optional;

@Configuration
@EnableConfigurationProperties(R2RpcProperties.class)
public class R2ServerAutoConfiguration {
  @Autowired
  private R2RpcProperties r2RpcProperties;

  @ConditionalOnMissingBean
  @Bean
  @Scope("prototype")
  public ServerRSocketFactory defaultRSocketFactory() {
    return RSocketFactory.receive();
  }

  @ConditionalOnClass(ObjectMapper.class)
  @Bean
  @Primary
  public R2DataCodec defaultCodec() {
    return new R2DataCodecJacksonJson();
  }

  @ConditionalOnClass(NettyDuplexConnection.class)
  @Bean
  public R2ServerTransport<NettyContextCloseable> defaultTransport() {
    return new R2ServerTransportTcp();
  }

  @Bean
  public R2ServersLifecycle serversLifecycle(
      Optional<List<ResponderApiProvider>> apiProviders,
      Optional<List<R2DataCodec>> dataCodecs,
      Optional<List<R2ServerTransport>> transports,
      ServerRSocketFactory serverRSocketFactory) {

    Optional<ResponderProperties> serverResponder =
        Optional.of(r2RpcProperties)
            .map(R2RpcProperties::getServer)
            .map(PeerProperties::getResponder);

    DefaultProperties defaultProperties = serverResponder
        .map(ResponderProperties::getDefaults)
        .orElse(null);

    List<ResponderEndpointProperties> endpoints = serverResponder
        .map(ResponderProperties::getEndpoints)
        .orElse(null);

    return new R2ServersLifecycle
        .Builder(serverRSocketFactory)
        .apiProviders(apiProviders)
        .dataCodecs(dataCodecs)
        .transports(transports)
        .build(defaultProperties, endpoints);
  }

  @Bean
  public ServerControls serverControls(R2ServersLifecycle serversLifecycle) {
    return serversLifecycle.serverControls();
  }
}
