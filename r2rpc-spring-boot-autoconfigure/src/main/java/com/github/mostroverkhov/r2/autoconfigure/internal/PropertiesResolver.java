package com.github.mostroverkhov.r2.autoconfigure.internal;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PropertiesResolver {

  private static final Logger logger = LoggerFactory.getLogger(PropertiesResolver.class);

  private final R2DefaultProperties fallbackServerProps;

  public PropertiesResolver(R2DefaultProperties fallbackServerProps) {
    Objects.requireNonNull(fallbackServerProps);
    this.fallbackServerProps = fallbackServerProps;
  }

  public Resolved<Set<String>, Set<R2Properties>> resolve(List<R2Properties> props,
      R2DefaultProperties defProps) {

    if (props == null) {
      logger.debug("Resolving missing R2 Properties with empty Set");
      return Resolved.newSucc(Collections.emptySet());
    }
    logger.debug("Resolving R2 Properties " + props);
    logger.debug("Default R2 Properties " + defProps);

    R2DefaultProperties resolvedDefProps = resolve(defProps);
    List<Resolved<String, R2Properties>> resolved = props.stream()
        .filter(R2Properties::isEnabled)
        .flatMap(prop -> resolve(prop, resolvedDefProps).stream())
        .collect(toList());

    Set<String> errors = resolved.stream()
        .filter(Resolved::isErr)
        .map(Resolved::err)
        .collect(toSet());
    if (!errors.isEmpty()) {
      logger.debug("Error while resolving R2 Properties " + errors);
      return Resolved.newErr(errors);
    }

    Set<String> configNames = resolved.stream()
        .map(r -> r.succ().getName())
        .collect(toSet());
    if (configNames.size() != resolved.size()) {
      logger.debug("Resolving R2 Properties error: non-unique names");
      return Resolved.newErr(Collections.singleton("Server configuration names should be unique"));
    }

    Set<R2Properties> serverProps = resolved.stream()
        .map(Resolved::succ)
        .collect(toSet());
    logger.debug("Resolving R2 Properties:success " + serverProps);
    return Resolved.newSucc(serverProps);
  }

  private static List<Resolved<String, R2Properties>> resolve(
      R2Properties props,
      R2DefaultProperties defProps) {

    List<Resolved<String, R2Properties>> res = new ArrayList<>();

    String name = props.getName();
    if (absent(name) || name.isEmpty()) {
      res.add(Resolved.newErr("Configuration name must be present"));
    }
    int port = props.getPort();
    if (port <= 0) {
      res.add(Resolved.newErr(String.format("%s: port must be positive: %d",
          props.getName(),
          port)));
    }
    if (!res.isEmpty()) {
      return res;
    }
    if (absent(props.getCodecs())) {
      props.setCodecs(defProps.getCodecs());
    }
    if (absent(props.getTransport())) {
      props.setTransport(defProps.getTransport());
    }
    res.add(Resolved.newSucc(props));
    return res;
  }

  private R2DefaultProperties resolve(R2DefaultProperties defProps) {
    if (defProps == null) {
      defProps = new R2DefaultProperties();
    }
    if (absent(defProps.getCodecs())) {
      defProps.setCodecs(fallbackServerProps.getCodecs());
    }
    if (absent(defProps.getTransport())) {
      defProps.setTransport(fallbackServerProps.getTransport());
    }
    return defProps;
  }

  private static boolean absent(Object prop) {
    return prop == null;
  }

  static class Resolved<L, R> {

    private final L error;
    private final R success;

    private Resolved(L error, R success) {
      this.error = error;
      this.success = success;
    }

    public static <L, R> Resolved<L, R> newSucc(R r) {
      return new Resolved<>(null, r);
    }

    public static <L, R> Resolved<L, R> newErr(L l) {
      return new Resolved<>(l, null);
    }

    public L err() {
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
