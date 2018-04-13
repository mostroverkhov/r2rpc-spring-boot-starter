package com.github.mostroverkhov.r2.autoconfigure.internal.server;

import com.github.mostroverkhov.r2.autoconfigure.internal.NamedBeanResolver;
import com.github.mostroverkhov.r2.autoconfigure.server.R2ServerTransport;

import java.util.List;

public class ServerTransportResolver extends NamedBeanResolver<R2ServerTransport> {

  public ServerTransportResolver(List<R2ServerTransport> serverTransports) {
    super(serverTransports, ServerTransportResolver::error);
  }

  private static String error(String transport) {
    return String.format("No R2 Server transport for name: %s", transport);
  }
}
