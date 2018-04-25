package com.github.mostroverkhov.r2.autoconfigure.internal.server;

import com.github.mostroverkhov.r2.autoconfigure.internal.PropertiesResolver.Resolved;
import com.github.mostroverkhov.r2.autoconfigure.internal.PropertiesResolverTest;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.ServerEndpointProperties;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ServerPropertiesResolverTest extends PropertiesResolverTest<ServerEndpointProperties> {

  @Override
  @Before
  public void setUp() {
    super.setUp();
    propertiesResolver = new ServerPropertiesResolver(mockDefProps);
  }

  @Test
  public void resolveInvalidPortProps() {
    ServerEndpointProperties props = copy(mockProps);
    props.setPort(0);
    Resolved<Set<ServerEndpointProperties>> resolved = propertiesResolver
        .resolve(Collections.singletonList(props), mockDefProps);
    assertThat(resolved.isErr()).isTrue();
    assertThat(resolved.err()).hasSize(1);
  }

  @Test
  public void resolveMultipleInvalidProps() {
    ServerEndpointProperties props = copy(mockProps);
    props.setName(null);
    props.setPort(0);
    Resolved<Set<ServerEndpointProperties>> resolved = propertiesResolver
        .resolve(Collections.singletonList(props), mockDefProps);
    assertThat(resolved.isErr()).isTrue();
    assertThat(resolved.err()).hasSize(2);
  }


  @Override
  public ServerEndpointProperties createProps() {

    String api = MockProps.api;
    String codec = MockProps.codec;
    String transport = MockProps.transport;
    String name = MockProps.name;
    int port = 8081;

    ServerEndpointProperties mockProps = new ServerEndpointProperties();
    mockProps.setName(name);
    mockProps.setCodecs(Collections.singletonList(codec));
    mockProps.setResponders(Arrays.asList(api));
    mockProps.setTransport(transport);
    mockProps.setPort(port);

    return mockProps;
  }

  @Override
  public ServerEndpointProperties copy(ServerEndpointProperties props) {
    ServerEndpointProperties copy = new ServerEndpointProperties();
    copy.setPort(props.getPort());
    copy.setTransport(props.getTransport());
    copy.setResponders(props.getResponders());
    copy.setCodecs(props.getCodecs());
    copy.setName(props.getName());
    copy.setEnabled(props.isEnabled());
    return copy;
  }
}
