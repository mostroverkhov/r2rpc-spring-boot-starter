package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.internal.PropertiesResolver.Resolved;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.R2ServerProperties;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class PropertiesResolverTest<T extends R2Properties> {

  protected PropertiesResolver<T> propertiesResolver;
  protected R2DefaultProperties mockDefProps;
  protected T mockProps;

  @Before
  public void setUp() {
    mockDefProps = defProps();
    mockProps = createProps();
  }

  @Test
  public void resolveBothEmpty() {
    Resolved<Set<T>> resolved = propertiesResolver
        .resolve(null, null);
    assertThat(resolved.isErr()).isFalse();
    assertThat(resolved.succ()).isEmpty();
  }

  @Test
  public void resolvePropsEmpty() {
    Resolved<Set<T>> resolved = propertiesResolver
        .resolve(null, mockDefProps);
    assertThat(resolved.isErr()).isFalse();
    assertThat(resolved.succ()).isEmpty();
  }

  @Test
  public void resolvePropsNoDef() {

    Resolved<Set<T>> resolved = propertiesResolver
        .resolve(Collections.singletonList(mockProps), null);
    assertThat(resolved.isErr()).isFalse();
    assertThat(resolved.succ()).hasSize(1);
  }

  @Test
  public void resolveCompletePropsHasDef() {
    Resolved<Set<T>> resolved = propertiesResolver
        .resolve(Collections.singletonList(copy(mockProps)), mockDefProps);
    assertThat(resolved.isErr()).isFalse();
    assertThat(resolved.succ()).hasSize(1);
    T properties = resolved.succ().iterator().next();
    assertThat(properties.getCodecs()).isEqualTo(mockProps.getCodecs());
    assertThat(properties.getTransport()).isEqualTo(mockProps.getTransport());
  }

  @Test
  public void resolveNonCompletePropsHasDef() {
    T props = copy(mockProps);
    props.setTransport(null);
    props.setCodecs(null);
    Resolved<Set<T>> resolved = propertiesResolver
        .resolve(Collections.singletonList(props), mockDefProps);
    assertThat(resolved.isErr()).isFalse();
    assertThat(resolved.succ()).hasSize(1);
    T properties = resolved.succ().iterator().next();
    assertThat(properties.getCodecs()).isEqualTo(mockDefProps.getCodecs());
    assertThat(properties.getTransport()).isEqualTo(mockDefProps.getTransport());
  }

  @Test
  public void resolveMissingApisProps() {
    T props = copy(mockProps);
    props.setApi(null);
    Resolved<Set<T>> resolved = propertiesResolver
        .resolve(Collections.singletonList(props), mockDefProps);
    assertThat(resolved.isErr()).isFalse();
    assertThat(resolved.succ()).hasSize(1);
  }

  @Test
  public void resolveInvalidNameProps() {
    T props = copy(mockProps);
    props.setName(null);
    Resolved<Set<T>> resolved = propertiesResolver
        .resolve(Collections.singletonList(props), mockDefProps);
    assertThat(resolved.isErr()).isTrue();
    assertThat(resolved.err()).hasSize(1);
  }

  @Test
  public void resolveDuplicateNameProps() {
    T props1 = copy(mockProps);
    T props2 = copy(mockProps);
    Resolved<Set<T>> resolved = propertiesResolver
        .resolve(Arrays.asList(props1, props2), mockDefProps);
    assertThat(resolved.isErr()).isTrue();
    assertThat(resolved.err()).hasSize(1);
  }

  @Test
  public void resolveDisabledProps() {
    T props = copy(mockProps);
    props.setEnabled(false);
    Resolved<Set<T>> resolved = propertiesResolver
        .resolve(Collections.singletonList(props), mockDefProps);
    assertThat(resolved.isErr()).isFalse();
    assertThat(resolved.succ()).hasSize(0);
  }

  @Test
  public void propsAreEnabled() {
    R2ServerProperties props = new R2ServerProperties();
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

  public abstract T createProps();

  public abstract T copy(T t);

  protected static class MockProps {
    public static String api = "api";
    public static String codec = "json";
    public static String transport = "tcp";
    public static String name = "name";
  }
}