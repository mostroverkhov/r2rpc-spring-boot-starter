package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;
import com.github.mostroverkhov.r2.autoconfigure.client.R2ClientTransport;
import com.github.mostroverkhov.r2.autoconfigure.client.R2ClientTransportTcp;
import com.github.mostroverkhov.r2.autoconfigure.client.RequesterApiProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.*;
import com.github.mostroverkhov.r2.autoconfigure.internal.R2DataCodecJacksonJson;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.NettyDuplexConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Optional;

import static io.rsocket.RSocketFactory.ClientRSocketFactory;

@Configuration
@EnableConfigurationProperties(R2RpcProperties.class)
public class R2ClientAutoConfiguration {

  @Autowired
  private R2RpcProperties rootProperties;

  @ConditionalOnMissingBean
  @Bean
  @Scope("prototype")
  public ClientRSocketFactory defaultClientRSocketFactory() {
    return RSocketFactory.connect();
  }

  @ConditionalOnClass(ObjectMapper.class)
  @Bean
  public R2DataCodec defaultCodec() {
    return new R2DataCodecJacksonJson();
  }

  @ConditionalOnClass(NettyDuplexConnection.class)
  @Bean
  public R2ClientTransport defaultClientTransport() {
    return new R2ClientTransportTcp();
  }

  @Bean
  public ClientConnectors r2Client(
      ClientRSocketFactory clientRSocketFactory,
      Optional<List<RequesterApiProvider>> apiProviders,
      Optional<List<R2DataCodec>> dataCodecs,
      Optional<List<R2ClientTransport>> transports) {

    Optional<RequesterProperties>
        clientRequester =
        Optional.of(
            rootProperties)
            .map(R2RpcProperties::getClient)
            .map(PeerProperties::getRequester);

    DefaultProperties defaultProperties = clientRequester
        .map(RequesterProperties::getDefaults)
        .orElse(null);

    List<RequesterEndpointProperties> endpoints = clientRequester
        .map(RequesterProperties::getEndpoints)
        .orElse(null);

    return new R2ConnectorsBuilder(clientRSocketFactory)
        .apiProviders(apiProviders)
        .dataCodecs(dataCodecs)
        .transports(transports)
        .build(defaultProperties, endpoints);
  }
}
