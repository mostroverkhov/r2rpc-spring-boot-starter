package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.internal.client.ClientPropertiesResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.ClientEndpointProperties;
import org.junit.Before;

import java.util.Arrays;
import java.util.Collections;

public class ClientPropertiesResolverTest extends PropertiesResolverTest<ClientEndpointProperties>{

  @Override
  @Before
  public void setUp() {
    super.setUp();
    propertiesResolver = new ClientPropertiesResolver(mockDefProps);
  }

  @Override
  public ClientEndpointProperties copy(ClientEndpointProperties props) {
    ClientEndpointProperties copy = new ClientEndpointProperties();
    copy.setTransport(props.getTransport());
    copy.setResponders(props.getResponders());
    copy.setCodecs(props.getCodecs());
    copy.setName(props.getName());
    copy.setEnabled(props.isEnabled());
    return copy;
  }

  @Override
  public ClientEndpointProperties createProps() {
    ClientEndpointProperties mock = new ClientEndpointProperties();

    String api = MockProps.api;
    String codec = MockProps.codec;
    String transport = MockProps.transport;
    String name = MockProps.name;

    mock.setName(name);
    mock.setCodecs(Collections.singletonList(codec));
    mock.setResponders(Arrays.asList(api));
    mock.setTransport(transport);

    return mock;
  }
}
