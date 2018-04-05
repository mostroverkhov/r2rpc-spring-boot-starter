package com.github.mostroverkhov.r2.example;

import com.github.mostroverkhov.r2.autoconfigure.server.controls.ServerControls;
import com.github.mostroverkhov.r2.codec.jackson.JacksonJsonDataCodec;
import com.github.mostroverkhov.r2.core.requester.RequesterFactory;
import com.github.mostroverkhov.r2.example.api.BarApiProvider;
import com.github.mostroverkhov.r2.example.api.bar.Bar;
import com.github.mostroverkhov.r2.example.api.bar.BarContract;
import com.github.mostroverkhov.r2.example.api.baz.Baz;
import com.github.mostroverkhov.r2.example.api.baz.BazContract;
import com.github.mostroverkhov.r2.java.R2Client;
import io.rsocket.RSocketFactory.ClientRSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class R2rpcStarterExampleApplication {

  private static final Logger logger = LoggerFactory
      .getLogger(R2rpcStarterExampleApplication.class);

  private final ServerControls serverControls;

  @Autowired
  public R2rpcStarterExampleApplication(ServerControls serverControls) {
    this.serverControls = serverControls;
  }

  public static void main(String[] args) {
    SpringApplication.run(R2rpcStarterExampleApplication.class, args);
  }

  @Bean
  public BazApiProvider bazApi() {
    return new BazApiProvider();
  }

  @Bean
  public BarApiProvider barApi() {
    return new BarApiProvider();
  }

  @Bean
  public CommandLineRunner runner() {
    return this::simpleR2Client;
  }

  @NotNull
  private Disposable simpleR2Client(String... args) {
    Mono<RequesterFactory> requesterFactory = new R2Client()
        .configureRequester(b ->
            b.codec(new JacksonJsonDataCodec()))
        .connectWith(new ClientRSocketFactory())
        .transport(TcpClientTransport.create(8083))
        .start()
        .delaySubscription(
            serverControls
                .endpoint("foo")
                .started()
                .doOnError(err -> logger.error("Server failed to start", err))
                .onErrorResume(err -> Mono.never()))
        .cache();

    Mono<BazContract> bazContract = requesterFactory
        .map(req -> req.create(BazContract.class));

    Mono<BarContract> barContract = requesterFactory
        .map(req -> req.create(BarContract.class));

    return barContract
        .flatMapMany(barSvc ->
            bazContract.flatMapMany(bazSvc ->
                Flux.combineLatest(
                    barSvc.bar(newBar()),
                    bazSvc.baz(newBaz()),
                    BarAndBaz::new)
            ))
        .subscribe(
            baz -> logger.debug("Got BarBaz response: " + baz),
            err -> logger.error("BarBaz error: ", err),
            () -> logger.debug("BarBaz completed"));
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
