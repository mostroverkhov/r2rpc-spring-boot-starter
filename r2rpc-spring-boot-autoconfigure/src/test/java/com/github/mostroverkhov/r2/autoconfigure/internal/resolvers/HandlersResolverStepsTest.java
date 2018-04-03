package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.mostroverkhov.r2.autoconfigure.R2Api;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlersResolver.ApiResolveSteps;
import java.util.Optional;
import org.junit.Test;

public class HandlersResolverStepsTest {

  @Test
  public void findNameByR2Api() {
    Optional<String> apiName = ApiResolveSteps.findApiName(SubApi.class);
    assertThat(apiName).contains("api");
  }

  @Test
  public void findNameAbsentR2Annos() {
    Optional<String> apiName = ApiResolveSteps.findApiName(NotR2Api.class);
    assertThat(apiName).isEmpty();
  }

  @R2Api("api")
  interface Api {

  }

  interface SubApi extends Api {

  }

  interface NotR2Api {

  }
}
