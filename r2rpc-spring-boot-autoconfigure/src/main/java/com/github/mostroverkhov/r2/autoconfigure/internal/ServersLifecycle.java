package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.internal.PropertiesResolver.Resolved;
import com.github.mostroverkhov.r2.core.DataCodec;
import com.github.mostroverkhov.r2.core.responder.Codecs;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import com.github.mostroverkhov.r2.core.responder.Services;
import com.github.mostroverkhov.r2.java.R2Server;
import io.rsocket.Closeable;
import io.rsocket.RSocketFactory.ServerRSocketFactory;
import io.rsocket.transport.ServerTransport;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.NestedExceptionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

class ServersLifecycle implements SmartLifecycle {

  private static final Logger logger = LoggerFactory.getLogger(ServersLifecycle.class);

  private static final Runnable NOOP = () -> {
  };

  private volatile boolean isRunning;
  private final Set<ServerConfig> configs;
  private final ExecutorService serversRunner = Executors.newSingleThreadExecutor();
  private volatile ServersStarter serversStarter;

  public ServersLifecycle(Set<ServerConfig> configs) {
    this.configs = configs;
  }

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  @Override
  public void start() {
    serversStarter = new ServersStarter(serverStarts(configs));
    serversRunner.execute(
        () -> serversStarter
            .start()
            .doOnSubscribe(s -> logServerStarting(configs))
            .doOnNext(closeables -> isRunning = true)
            .doOnNext(this::logServersStarted)
            .then(serversStarter.onStop())
            .block());
  }

  @Override
  public void stop(Runnable callback) {
    doStop(callback);
  }

  @Override
  public void stop() {
    doStop(NOOP);
  }

  @Override
  public boolean isRunning() {
    return isRunning;
  }

  @Override
  public int getPhase() {
    return Integer.MAX_VALUE;
  }

  private List<Mono<Closeable>> serverStarts(Set<ServerConfig> serverConfigs) {
    return serverConfigs.stream()
        .map(this::serverStart)
        .collect(Collectors.toList());
  }

  private Mono<Closeable> serverStart(ServerConfig serverConfig) {
    ServerTransport<Closeable> transport = serverConfig.transport();
    Function<ConnectionContext, List<Object>> handlers = serverConfig.handlers();
    List<DataCodec> codecs = serverConfig.codecs();
    ServerRSocketFactory rSocketFactory = serverConfig.rSocketFactory();

    return new R2Server<>()
        .configureAcceptor(
            acceptor ->
                acceptor
                    .codecs(addCodecs(codecs))
                    .services(ctx -> addHandlers(ctx, handlers)
                    ))
        .connectWith(rSocketFactory)
        .transport(transport)
        .start();
  }

  private Codecs addCodecs(List<DataCodec> codecList) {
    Codecs codecs = new Codecs();
    codecList.forEach(codecs::add);
    return codecs;
  }

  private Services addHandlers(
      ConnectionContext ctx,
      Function<ConnectionContext, List<Object>> handlerFactory) {
    Services services = new Services();
    List<Object> handlers = handlerFactory.apply(ctx);
    handlers.forEach(services::add);
    return services;
  }

  private void doStop(Runnable onStop) {
    serversStarter
        .stop()
        .doOnSubscribe(s -> logServerStopping())
        .doFinally(signal -> onStop.run())
        .doFinally(signal -> isRunning = false)
        .subscribe(Void -> {
            }, this::logServerStopError,
            this::logServerStopped);
  }

  private void logServerStopError(Throwable err) {
    String msg = NestedExceptionUtils
        .buildMessage("Error while stopping R2 servers",
            err);
    logger.error(msg);
  }

  private void logServerStopped() {
    logger.debug("Stopped R2 servers");
  }

  private void logServerStopping() {
    logger.debug("Stopping R2 Servers");
  }

  private void logServerStarting(Set<ServerConfig> configs) {
    logger.debug(String
        .format("Starting R2 Servers with %d endpoints",
            configs.size()));
  }

  private void logServersStarted(List<Closeable> closeables) {
    logger.debug(String
        .format("Started R2 Servers with %d endpoints",
            closeables.size()));
  }

  static class ServersStarter {

    private final MonoProcessor<List<Closeable>> started = MonoProcessor.create();
    private final List<Mono<Closeable>> starts;

    public ServersStarter(List<Mono<Closeable>> starts) {
      this.starts = starts;
    }

    public Mono<List<Closeable>> start() {
      Flux.fromIterable(starts)
          .flatMap(start -> start.onErrorResume(err -> Mono.empty()))
          .collectList()
          .subscribe(started);

      return started;
    }

    public Mono<Void> stop() {
      return started.flatMap(closeableList -> {
        Flux<Closeable> closeables = Flux.fromIterable(closeableList);
        return closeables
            .flatMap(Closeable::close)
            .then(closeables
                .flatMap(Closeable::onClose)
                .then());
      });
    }

    public Mono<Void> onStop() {
      return started
          .flatMap(closeableList ->
              Flux.fromIterable(closeableList)
                  .flatMap(Closeable::onClose)
                  .then());
    }
  }

  static class Builder {

    private final PropertiesResolver propsResolver;
    private final ServerConfigResolver configResolver;

    public Builder(
        R2DefaultProperties fallbackProps,
        GenericApplicationContext ctx) {
      Objects.requireNonNull(fallbackProps, "Fallback properties must not be null");
      Objects.requireNonNull(ctx, "ApplicationContext must not be null");
      this.propsResolver = new PropertiesResolver(fallbackProps);
      this.configResolver = new ServerConfigResolver(ctx);
    }

    public ServersLifecycle build(R2DefaultProperties defProps,
        List<R2Properties> props) {

      Resolved<Set<String>, Set<R2Properties>> resolvedProps =
          propsResolver
              .resolve(props, defProps);
      if (resolvedProps.isErr()) {
        throw new IllegalArgumentException(
            "R2Server config is not complete: " + resolvedProps.err());
      }

      Set<R2Properties> serverProps = resolvedProps.succ();
      Set<ServerConfig> serverConfigs = configResolver.resolve(serverProps);

      return new ServersLifecycle(serverConfigs);
    }
  }
}
