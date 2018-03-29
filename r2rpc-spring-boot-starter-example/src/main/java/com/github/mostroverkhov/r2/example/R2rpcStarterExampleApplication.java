package com.github.mostroverkhov.r2.example;

import com.github.mostroverkhov.r2.codec.jackson.JacksonJsonDataCodec;
import com.github.mostroverkhov.r2.example.api.Baz;
import com.github.mostroverkhov.r2.example.api.BazContract;
import com.github.mostroverkhov.r2.java.R2Client;
import io.rsocket.RSocketFactory.ClientRSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import java.time.Duration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.Disposable;

@SpringBootApplication
public class R2rpcStarterExampleApplication {

  private static final Logger logger = LoggerFactory
      .getLogger(R2rpcStarterExampleApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(R2rpcStarterExampleApplication.class, args);
  }

  @Bean
  public BazServerApiHandlers handlers() {
    return new BazServerApiHandlers();
  }

  @Bean
  public CommandLineRunner runner() {
    return this::simpleR2Client;
  }

  @NotNull
  private Disposable simpleR2Client(String... args) {
    return new R2Client()
        .configureRequester(b ->
            b.codec(new JacksonJsonDataCodec()))
        .connectWith(new ClientRSocketFactory())
        .transport(TcpClientTransport.create(8083))
        .start()
        .delaySubscription(Duration.ofSeconds(1))
        .map(req -> req.create(BazContract.class))
        .flatMapMany(bazContract -> bazContract.baz(new Baz("42")))
        .subscribe(
            baz -> logger.debug("Got Buz response: " + baz),
            err -> logger.error("Buz error: ", err),
            () -> logger.debug("Buz completed"));
  }
}
