package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

import io.rsocket.RSocketFactory.ServerRSocketFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.Set;

public class ServerRSocketFactoryResolver implements
    Resolver<ApplicationContext, ServerRSocketFactory> {
  @Override
  public ServerRSocketFactory resolve(ApplicationContext ctx) {
    try {
      return ctx.getBean(ServerRSocketFactory.class);
    } catch (NoUniqueBeanDefinitionException e) {
      Set<String> beanNames = ctx
              .getBeansOfType(ServerRSocketFactory.class)
              .keySet();
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
}
