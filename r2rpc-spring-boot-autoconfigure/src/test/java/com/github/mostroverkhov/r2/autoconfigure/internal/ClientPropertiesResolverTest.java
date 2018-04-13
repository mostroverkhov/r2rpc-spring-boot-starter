package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.internal.client.ClientPropertiesResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.RequesterEndpointProperties;
import org.junit.Before;

import java.util.Arrays;
import java.util.Collections;

public class ClientPropertiesResolverTest extends PropertiesResolverTest<RequesterEndpointProperties>{

  @Override
  @Before
  public void setUp() {
    super.setUp();
    propertiesResolver = new ClientPropertiesResolver(mockDefProps);
  }

  @Override
  public RequesterEndpointProperties copy(RequesterEndpointProperties props) {
    RequesterEndpointProperties copy = new RequesterEndpointProperties();
    copy.setTransport(props.getTransport());
    copy.setApi(props.getApi());
    copy.setCodecs(props.getCodecs());
    copy.setName(props.getName());
    copy.setEnabled(props.isEnabled());
    return copy;
  }

  @Override
  public RequesterEndpointProperties createProps() {
    RequesterEndpointProperties mock = new RequesterEndpointProperties();

    String api = MockProps.api;
    String codec = MockProps.codec;
    String transport = MockProps.transport;
    String name = MockProps.name;

    mock.setName(name);
    mock.setCodecs(Collections.singletonList(codec));
    mock.setApi(Arrays.asList(api));
    mock.setTransport(transport);

    return mock;
  }
}
