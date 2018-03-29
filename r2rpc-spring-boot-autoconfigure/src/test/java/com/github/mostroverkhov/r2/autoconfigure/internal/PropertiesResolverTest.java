package com.github.mostroverkhov.r2.autoconfigure.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.mostroverkhov.r2.autoconfigure.internal.PropertiesResolver.Resolved;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class PropertiesResolverTest {

  private PropertiesResolver propertiesResolver;
  private R2Properties mockProps;
  private R2DefaultProperties mockDefProps;

  @Before
  public void setUp() {
    mockDefProps = defProps();
    mockProps = mockProps();
    propertiesResolver = new PropertiesResolver(mockDefProps);
  }

  @Test
  public void resolveBothEmpty() {
    Resolved<Set<String>, Set<R2Properties>> resolved = propertiesResolver
        .resolve(null, null);
    assertThat(resolved.isErr()).isFalse();
    assertThat(resolved.succ()).isEmpty();
  }

  @Test
  public void resolvePropsEmpty() {
    Resolved<Set<String>, Set<R2Properties>> resolved = propertiesResolver
        .resolve(null, mockDefProps);
    assertThat(resolved.isErr()).isFalse();
    assertThat(resolved.succ()).isEmpty();
  }

  @Test
  public void resolvePropsNoDef() {

    Resolved<Set<String>, Set<R2Properties>> resolved = propertiesResolver
        .resolve(Collections.singletonList(mockProps), null);
    assertThat(resolved.isErr()).isFalse();
    assertThat(resolved.succ()).hasSize(1);
  }

  @Test
  public void resolveCompletePropsHasDef() {
    Resolved<Set<String>, Set<R2Properties>> resolved = propertiesResolver
        .resolve(Collections.singletonList(mockProps.copy()), mockDefProps);
    assertThat(resolved.isErr()).isFalse();
    assertThat(resolved.succ()).hasSize(1);
    R2Properties properties = resolved.succ().iterator().next();
    assertThat(properties.getCodecs()).isEqualTo(mockProps.getCodecs());
    assertThat(properties.getTransport()).isEqualTo(mockProps.getTransport());
  }

  @Test
  public void resolveNonCompletePropsHasDef() {
    R2Properties props = mockProps.copy();
    props.setTransport(null);
    props.setCodecs(null);
    Resolved<Set<String>, Set<R2Properties>> resolved = propertiesResolver
        .resolve(Collections.singletonList(props), mockDefProps);
    assertThat(resolved.isErr()).isFalse();
    assertThat(resolved.succ()).hasSize(1);
    R2Properties properties = resolved.succ().iterator().next();
    assertThat(properties.getCodecs()).isEqualTo(mockDefProps.getCodecs());
    assertThat(properties.getTransport()).isEqualTo(mockDefProps.getTransport());
  }

  @Test
  public void resolveMissingApisProps() {
    R2Properties props = mockProps.copy();
    props.setApi(null);
    Resolved<Set<String>, Set<R2Properties>> resolved = propertiesResolver
        .resolve(Collections.singletonList(props), mockDefProps);
    assertThat(resolved.isErr()).isFalse();
    assertThat(resolved.succ()).hasSize(1);
  }

  @Test
  public void resolveInvalidNameProps() {
    R2Properties props = mockProps.copy();
    props.setName(null);
    Resolved<Set<String>, Set<R2Properties>> resolved = propertiesResolver
        .resolve(Collections.singletonList(props), mockDefProps);
    assertThat(resolved.isErr()).isTrue();
    assertThat(resolved.err()).hasSize(1);
  }

  @Test
  public void resolveInvalidPortProps() {
    R2Properties props = mockProps.copy();
    props.setPort(0);
    Resolved<Set<String>, Set<R2Properties>> resolved = propertiesResolver
        .resolve(Collections.singletonList(props), mockDefProps);
    assertThat(resolved.isErr()).isTrue();
    assertThat(resolved.err()).hasSize(1);
  }

  @Test
  public void resolveDuplicateNameProps() {
    R2Properties props1 = mockProps.copy();
    R2Properties props2 = mockProps.copy();
    Resolved<Set<String>, Set<R2Properties>> resolved = propertiesResolver
        .resolve(Arrays.asList(props1, props2), mockDefProps);
    assertThat(resolved.isErr()).isTrue();
    assertThat(resolved.err()).hasSize(1);
  }

  @Test
  public void resolveMultipleInvalidProps() {
    R2Properties props = mockProps.copy();
    props.setName(null);
    props.setPort(0);
    Resolved<Set<String>, Set<R2Properties>> resolved = propertiesResolver
        .resolve(Collections.singletonList(props), mockDefProps);
    assertThat(resolved.isErr()).isTrue();
    assertThat(resolved.err()).hasSize(2);
  }

  @Test
  public void resolveDisabledProps() {
    R2Properties props = mockProps.copy();
    props.setEnabled(false);
    Resolved<Set<String>, Set<R2Properties>> resolved = propertiesResolver
        .resolve(Collections.singletonList(props), mockDefProps);
    assertThat(resolved.isErr()).isFalse();
    assertThat(resolved.succ()).hasSize(0);
  }

  @Test
  public void propsAreEnabled() {
    R2Properties props = new R2Properties();
    assertThat(props.isEnabled()).isTrue();
  }

  private R2DefaultProperties defProps() {
    String codec = "codec";
    String transport = "transport";
    R2DefaultProperties defProps = new R2DefaultProperties();
    defProps.setCodecs(Collections.singletonList(codec));
    defProps.setTransport(transport);
    return defProps;
  }

  private R2Properties mockProps() {
    String api = "api";
    String codec = "json";
    String transport = "tcp";
    String name = "name";
    int port = 8081;

    mockProps = new R2Properties();
    mockProps.setName(name);
    mockProps.setCodecs(Collections.singletonList(codec));
    mockProps.setApi(api);
    mockProps.setTransport(transport);
    mockProps.setPort(port);

    return mockProps;
  }
}
