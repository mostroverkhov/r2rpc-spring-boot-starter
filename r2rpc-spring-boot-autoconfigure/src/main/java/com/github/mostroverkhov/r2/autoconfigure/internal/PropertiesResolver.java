package com.github.mostroverkhov.r2.autoconfigure.internal;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public abstract class PropertiesResolver<T extends R2Properties>
    implements Verifications<T> {
  private static final Logger logger = LoggerFactory
      .getLogger(PropertiesResolver.class);

  private final R2DefaultProperties fallbackProps;
  private final List<Function<T, Optional<String>>> verif = new ArrayList<>();

  public PropertiesResolver(R2DefaultProperties fallbackProps) {
    Objects.requireNonNull(fallbackProps);
    this.fallbackProps = fallbackProps;
    verifications(this);
  }

  public Resolved<Set<T>> resolve(List<T> props, R2DefaultProperties defProps) {
    if (props == null) {
      return resolvedEmptyProps();
    }
    R2DefaultProperties defaultProperties = createDefaultProperties(defProps);
    List<Resolved<T>> allResults = resolveAllProperties(props, defaultProperties);

    List<String> allErrors = allErrors(allResults);
    if (!allErrors.isEmpty()) {
      return propertiesResolveError(allErrors);
    }

    Set<T> allProperties = allProperties(allResults);
    Set<String> configNames = configNames(allProperties);
    if (configNames.size() != allProperties.size()) {
      return nonUniqueNamesError();
    }
    return resolvedProperties(allProperties);
  }

  public abstract void verifications(Verifications<T> verifications);

  @SuppressWarnings("unchecked")
  @Override
  public void addVerifications(Function<T, Optional<String>>... functions) {
    verif.addAll(Arrays.asList(functions));
  }

  private List<String> verifyProps(T props) {
    return verif
        .stream()
        .map(v -> v.apply(props))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(toList());
  }

  @NotNull
  private Resolved<Set<T>> resolvedProperties(Set<T> allProperties) {
    logger.debug("Resolving R2 Properties:success " + allProperties);
    return Resolved.newSucc(allProperties);
  }

  @NotNull
  private Resolved<Set<T>> resolvedEmptyProps() {
    logger.debug("Resolving missing R2 Properties with empty Set");
    return Resolved.newSucc(Collections.emptySet());
  }

  @NotNull
  private Resolved<Set<T>> propertiesResolveError(List<String> resolvedErrors) {
    logger.debug("Error while resolving R2 Properties " + resolvedErrors);
    return Resolved.newErr(resolvedErrors);
  }

  @NotNull
  private Resolved<Set<T>> nonUniqueNamesError() {
    logger.debug("R2 Properties error: non-unique names");
    return Resolved.newErr(
        Collections.singletonList("Configuration names must be unique"));
  }

  private Set<String> configNames(Set<T> props) {
    return props
        .stream()
        .map(T::getName)
        .collect(toSet());
  }

  private Set<T> allProperties(List<Resolved<T>> resolved) {
    return resolved
        .stream()
        .map(Resolved::succ)
        .collect(toSet());
  }

  private List<String> allErrors(List<Resolved<T>> resolved) {
    return resolved.stream()
        .filter(Resolved::isErr)
        .map(Resolved::err)
        .flatMap(Collection::stream)
        .collect(toList());
  }

  private List<Resolved<T>> resolveAllProperties(List<T> props,
                                                 R2DefaultProperties defaultProperties) {
    logger.debug("Resolving R2 Properties " + props);
    return props
        .stream()
        .filter(T::isEnabled)
        .map(prop -> resolve(prop, defaultProperties))
        .collect(toList());
  }

  private Resolved<T> resolve(T props,
                              R2DefaultProperties defProps) {

    T propsWithDefaults = withDefaults(props, defProps);
    List<String> errors = verifyProps(propsWithDefaults);
    if (!errors.isEmpty()) {
      return Resolved.newErr(errors);
    }
    return
        Resolved.newSucc(propsWithDefaults);
  }

  private T withDefaults(T props, R2DefaultProperties defProps) {
    if (absent(props.getCodecs())) {
      props.setCodecs(defProps.getCodecs());
    }
    if (absent(props.getTransport())) {
      props.setTransport(defProps.getTransport());
    }
    if (absent(props.getApi())) {
      props.setApi(Collections.emptyList());
    }
    return props;
  }

  private R2DefaultProperties createDefaultProperties(R2DefaultProperties
                                                          defProps) {
    logger.debug("Default R2 Properties " + defProps);
    if (defProps == null) {
      defProps = new R2DefaultProperties();
    }
    if (absent(defProps.getCodecs())) {
      defProps.setCodecs(fallbackProps.getCodecs());
    }
    if (absent(defProps.getTransport())) {
      defProps.setTransport(fallbackProps.getTransport());
    }
    return defProps;
  }

  private static boolean absent(Object prop) {
    return prop == null;
  }

  public static class Resolved<R> {

    private final List<String> error;
    private final R success;

    private Resolved(List<String> error, R success) {
      this.error = error;
      this.success = success;
    }

    public static <R> Resolved<R> newSucc(R success) {
      return new Resolved<>(null, success);
    }

    public static <R> Resolved<R> newErr(List<String> errors) {
      return new Resolved<>(errors, null);
    }

    public List<String> err() {
      return error;
    }

    public R succ() {
      return success;
    }

    public boolean isErr() {
      return error != null;
    }
  }
}
