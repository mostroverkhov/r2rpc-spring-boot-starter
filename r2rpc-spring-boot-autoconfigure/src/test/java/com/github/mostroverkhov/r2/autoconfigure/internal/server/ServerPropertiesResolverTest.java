package com.github.mostroverkhov.r2.autoconfigure.internal.server;

import com.github.mostroverkhov.r2.autoconfigure.internal.PropertiesResolver.Resolved;
import com.github.mostroverkhov.r2.autoconfigure.internal.PropertiesResolverTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ServerPropertiesResolverTest extends PropertiesResolverTest<R2ServerProperties> {

  @Override
  @Before
  public void setUp() {
    super.setUp();
    propertiesResolver = new ServerPropertiesResolver(mockDefProps);
  }

  @Test
  public void resolveInvalidPortProps() {
    R2ServerProperties props = copy(mockProps);
    props.setPort(0);
    Resolved<Set<R2ServerProperties>> resolved = propertiesResolver
        .resolve(Collections.singletonList(props), mockDefProps);
    assertThat(resolved.isErr()).isTrue();
    assertThat(resolved.err()).hasSize(1);
  }

  @Test
  public void resolveMultipleInvalidProps() {
    R2ServerProperties props = copy(mockProps);
    props.setName(null);
    props.setPort(0);
    Resolved<Set<R2ServerProperties>> resolved = propertiesResolver
        .resolve(Collections.singletonList(props), mockDefProps);
    assertThat(resolved.isErr()).isTrue();
    assertThat(resolved.err()).hasSize(2);
  }


  @Override
  public R2ServerProperties createProps() {

    String api = MockProps.api;
    String codec = MockProps.codec;
    String transport = MockProps.transport;
    String name = MockProps.name;
    int port = 8081;

    R2ServerProperties mockProps = new R2ServerProperties();
    mockProps.setName(name);
    mockProps.setCodecs(Collections.singletonList(codec));
    mockProps.setApi(Arrays.asList(api));
    mockProps.setTransport(transport);
    mockProps.setPort(port);

    return mockProps;
  }

  @Override
  public R2ServerProperties copy(R2ServerProperties props) {
    R2ServerProperties copy = new R2ServerProperties();
    copy.setPort(props.getPort());
    copy.setTransport(props.getTransport());
    copy.setApi(props.getApi());
    copy.setCodecs(props.getCodecs());
    copy.setName(props.getName());
    copy.setEnabled(props.isEnabled());
    return copy;
  }
}
