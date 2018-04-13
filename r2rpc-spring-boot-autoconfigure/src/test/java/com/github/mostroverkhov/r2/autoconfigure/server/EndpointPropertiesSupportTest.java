package com.github.mostroverkhov.r2.autoconfigure.server;

import com.github.mostroverkhov.r2.autoconfigure.internal.server.endpoints.EndpointResult;
import com.github.mostroverkhov.r2.autoconfigure.internal.server.endpoints.EndpointSupport;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Predicate;

public class EndpointPropertiesSupportTest {

  private EndpointSupport endpointSupport;

  @Before
  public void setUp() {
    endpointSupport = new EndpointSupport();
  }

  @Test
  public void startTest() {
    endpointSupport.startSucceeded("foo");
    endpointSupport.startFailed("bar", new RuntimeException("err"));
    endpointSupport.startCompleted();

    StepVerifier.create(endpointSupport.starts())
        .expectNextMatches(isSuccMessage("foo"))
        .expectNextMatches(isErrorMsg("bar", "err"))
        .expectComplete()
        .verify(Duration.ofSeconds(1));
  }

  @Test
  public void stopTest() {
    endpointSupport.stopSucceeded("foo");
    endpointSupport.stopSucceeded("bar");
    endpointSupport.stopCompleted();

    StepVerifier.create(endpointSupport.stops())
        .expectNextMatches(isSuccMessage("foo"))
        .expectNextMatches(isSuccMessage("bar"))
        .expectComplete()
        .verify(Duration.ofSeconds(1));
  }

  @NotNull
  private Predicate<EndpointResult> isSuccMessage(String msg) {
    return endpointResult ->
        !endpointResult.isError() &&
            msg.equals(endpointResult.getName());
  }

  @NotNull
  private Predicate<EndpointResult> isErrorMsg(String name, String errMsg) {
    return endpointResult -> {
      boolean isSameName = name.equals(endpointResult.getName());
      Throwable err = endpointResult.getError();
      boolean isRuntimeErr = err instanceof RuntimeException;
      boolean isExpectedErrMsg = errMsg.equals(err.getMessage());
      return isSameName && isRuntimeErr && isExpectedErrMsg;
    };
  }
}
