package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;
import com.github.mostroverkhov.r2.autoconfigure.RequestersProvider;
import com.github.mostroverkhov.r2.autoconfigure.client.ClientHandlersProvider;
import com.github.mostroverkhov.r2.autoconfigure.client.R2ClientTransport;
import com.github.mostroverkhov.r2.autoconfigure.internal.R2DataCodecJacksonJson;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.ClientEndpointProperties;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.ClientProperties;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.DefaultProperties;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.R2RpcProperties;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.NettyDuplexConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Optional;

import static io.rsocket.RSocketFactory.ClientRSocketFactory;

@Configuration
@EnableConfigurationProperties(R2RpcProperties.class)
@ConditionalOnProperty(prefix = "r2rpc.client", name = "enabled", matchIfMissing = true)
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
      Optional<List<RequestersProvider>> apiProviders,
      Optional<List<ClientHandlersProvider<?>>> handlers,
      Optional<List<R2DataCodec>> dataCodecs,
      Optional<List<R2ClientTransport>> transports) {

    Optional<ClientProperties>
        clientProperties =
        Optional.of(
            rootProperties)
            .map(R2RpcProperties::getClient);

    DefaultProperties defaultClientProperties = clientProperties
        .map(ClientProperties::getDefaults)
        .orElse(null);

    List<ClientEndpointProperties> clientEndpoints = clientProperties
        .map(ClientProperties::getEndpoints)
        .orElse(null);

    return new R2ConnectorsBuilder(clientRSocketFactory)
        .apiProviders(apiProviders)
        .handlerProviders(handlers)
        .dataCodecs(dataCodecs)
        .transports(transports)
        .build(defaultClientProperties, clientEndpoints);
  }
}
