package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

import io.rsocket.RSocketFactory.ServerRSocketFactory;
import java.util.Map;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

public class ServerRSocketFactoryResolver extends
    Resolver<ApplicationContext, ServerRSocketFactory> {

  private final ApplicationContext ctx;

  public ServerRSocketFactoryResolver(
      ApplicationContext ctx) {
    super(ServerRSocketFactoryResolver::error);
    this.ctx = ctx;
  }

  @Override
  protected void resolveAll(Map<ApplicationContext, ServerRSocketFactory> cache) {
    ServerRSocketFactory bean = ctx.getBean(ServerRSocketFactory.class);
    if (bean != null) {
      cache.put(ctx, bean);
    }
  }

  private static String error(ApplicationContext ctx) {
    return "ServerRSocketFactory is not present in Context";
  }
}
