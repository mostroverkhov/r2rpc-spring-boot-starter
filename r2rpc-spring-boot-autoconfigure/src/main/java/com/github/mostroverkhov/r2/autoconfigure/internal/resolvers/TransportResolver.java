package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

import com.github.mostroverkhov.r2.autoconfigure.R2ServerTransport;
import com.github.mostroverkhov.r2.autoconfigure.ServerTransportFactory;
import com.github.mostroverkhov.r2.autoconfigure.internal.R2BeanLocator;

public class TransportResolver extends
    BeanTypeAndAnnotationResolver<ServerTransportFactory, R2ServerTransport> {

  public TransportResolver(
      R2BeanLocator beanLocator) {
    super(beanLocator,
        ServerTransportFactory.class,
        R2ServerTransport.class,
        TransportResolver::error);
  }

  private static String error(String transport) {
    return String.format("No transport for name: %s", transport);
  }
}
