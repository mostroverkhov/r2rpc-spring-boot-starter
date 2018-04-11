package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.github.mostroverkhov.r2.autoconfigure.client.R2ClientTransport;
import com.github.mostroverkhov.r2.autoconfigure.internal.NamedBeanResolver;

import java.util.List;

public class ClientTransportResolver extends
    NamedBeanResolver<R2ClientTransport> {
  public ClientTransportResolver(List<R2ClientTransport> beans) {
    super(beans, ClientTransportResolver::error);
  }

  private static String error(String transport) {
    return String.format("No R2 Client transport for name: %s", transport);
  }
}
