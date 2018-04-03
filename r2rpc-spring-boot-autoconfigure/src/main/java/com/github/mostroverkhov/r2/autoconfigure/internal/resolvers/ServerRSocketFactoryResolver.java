package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

import io.rsocket.RSocketFactory.ServerRSocketFactory;
import java.util.Map;
import java.util.Set;
import javax.net.ServerSocketFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;

public class ServerRSocketFactoryResolver extends
    CachingResolver<ApplicationContext, ServerRSocketFactory> {

  private final ApplicationContext ctx;

  public ServerRSocketFactoryResolver(
      ApplicationContext ctx) {
    super(ServerRSocketFactoryResolver::error);
    this.ctx = ctx;
  }

  @Override
  public void resolveAll(Map<ApplicationContext, ServerRSocketFactory> cache) {
    try {
      ServerRSocketFactory bean = ctx.getBean(ServerRSocketFactory.class);
      cache.put(ctx, bean);
    } catch (NoUniqueBeanDefinitionException e) {
      Set<String> beanNames = ctx.getBeansOfType(ServerSocketFactory.class).keySet();
      String msg = "ApplicationContext must contain "
          + "unique ServerRSocketFactory bean, but contains %s";
      throw new IllegalStateException(
          String.format(msg, beanNames), e);
    }
    catch (NoSuchBeanDefinitionException e) {
      throw new IllegalStateException("ServerRSocketFactory bean is not"
          + " present in ApplicationContext", e);
    }
  }

  private static String error(ApplicationContext ctx) {
    return "Should not happen: resolver is expected to have exactly "
        + "one ServerRSocketFactory instance, or fail earlier";
  }
}
