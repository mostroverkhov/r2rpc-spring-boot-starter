package com.github.mostroverkhov.r2.example;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.autoconfigure.client.ClientConnectors;
import com.github.mostroverkhov.r2.autoconfigure.server.endpoints.Endpoint;
import com.github.mostroverkhov.r2.autoconfigure.server.endpoints.ServerControls;
import com.github.mostroverkhov.r2.example.api.*;
import com.github.mostroverkhov.r2.example.client.BarBazRequestersProvider;
import com.github.mostroverkhov.r2.example.client.BazClientHandlersProvider;
import com.github.mostroverkhov.r2.example.server.BarServerHandlersProvider;
import com.github.mostroverkhov.r2.example.server.BazServerHandlersProvider;
import com.github.mostroverkhov.r2.example.svc.Bar;
import com.github.mostroverkhov.r2.example.svc.Baz;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class R2rpcStarterExampleApplication {
  private static final Logger logger = LoggerFactory
      .getLogger(R2rpcStarterExampleApplication.class);

  @Autowired
  private ClientConnectors clientConnectors;
  @Autowired
  private ServerControls serverControls;

  public static void main(String[] args) {
    SpringApplication.run(R2rpcStarterExampleApplication.class, args);
  }
  /*baz server API handlers*/
  @Bean
  public BazServerHandlersProvider bazServerApi() {
    return new BazServerHandlersProvider();
  }

  /*baz client API handlers*/
  @Bean
  public BazClientHandlersProvider bazClientApi() {
    return new BazClientHandlersProvider();
  }
  /*bar API handlers*/
  @Bean
  public BarServerHandlersProvider barApi() {
    return new BarServerHandlersProvider();
  }
  /*hint used by R2RPC autoconfiguration to find APIs. In this case,
  * BarBazApiToken location determines APIs package*/
  @Bean
  BarBazRequestersProvider clientApiProvider() {
    return new BarBazRequestersProvider();
  }

  @Bean
  public CommandLineRunner r2ConnectorsClient() {
    return args -> {

      Mono<Endpoint> untilServerStarted =
          serverControls
          .endpoint("foo")
          .started()
          .doOnError(err -> logger.error("Server failed to start", err))
          .onErrorResume(err -> Mono.never());

      Mono<ApiRequesterFactory> apiFactory =
          clientConnectors
          .name("foo")
          .connect("localhost", 8083)
          .delaySubscription(untilServerStarted)
          .cache();

      Mono<BazApi> bazApiMono = apiFactory
          .map(af -> af.create(BazApi.class));
      Mono<BarApi> barApiMono = apiFactory
          .map(af -> af.create(BarApi.class));

      barApiMono
          .flatMapMany(barApi ->
              bazApiMono.flatMapMany(bazApi ->
                  Flux.combineLatest(
                      barApi.barContract().bar(newBar()),
                      bazApi.bazContract().baz(newBaz()),
                      BarAndBaz::new)
              ))
          .subscribe(
              baz -> logger.debug("Client Got BarBaz response: " + baz),
              err -> logger.error("Client BarBaz error: ", err),
              () -> logger.debug("Client BarBaz completed"));
    };
  }

  @NotNull
  private Baz newBaz() {
    return new Baz("24");
  }

  @NotNull
  private Bar newBar() {
    return new Bar("42");
  }

  static class BarAndBaz {

    private final Bar bar;
    private final Baz baz;

    public BarAndBaz(Bar bar, Baz baz) {
      this.bar = bar;
      this.baz = baz;
    }

    @Override
    public String toString() {
      return "BarAndBaz{" +
          "bar=" + bar +
          ", baz=" + baz +
          '}';
    }
  }
}
