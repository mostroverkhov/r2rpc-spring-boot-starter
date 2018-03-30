package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.mostroverkhov.r2.autoconfigure.R2Api;
import com.github.mostroverkhov.r2.autoconfigure.R2ApiName;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlersResolver.ApiResolveSteps;
import java.util.Optional;
import org.junit.Test;

public class HandlersResolverStepsTest {

  @Test
  public void findNameByR2ApiAndR2ApiName() {
    Optional<String> apiName = ApiResolveSteps.findApiName(ApiName.class);
    assertThat(apiName).contains("apiname");
  }

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

  @Test(expected = IllegalArgumentException.class)
  public void findNameR2ApiNameOnly() {
    ApiResolveSteps.findApiName(R2ApiNameOnly.class);
  }

  @R2ApiName("apiname")
  interface ApiName extends Api {

  }

  @R2Api("api")
  interface Api {

  }

  interface SubApi extends Api {

  }

  interface NotR2Api {

  }

  @R2ApiName("apiname")
  interface R2ApiNameOnly {

  }
}
