package com.github.mostroverkhov.r2.autoconfigure.internal;

import static java.util.Collections.*;
import static java.util.stream.Collectors.toSet;

import com.github.mostroverkhov.r2.autoconfigure.ServerTransportFactory;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.CodecResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.HandlersResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.ServerRSocketFactoryResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.resolvers.TransportResolver;
import com.github.mostroverkhov.r2.core.DataCodec;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import io.rsocket.Closeable;
import io.rsocket.RSocketFactory.ServerRSocketFactory;
import io.rsocket.transport.ServerTransport;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

class ServerConfigResolver {

  private final TransportResolver transportResolver;
  private final CodecResolver codecResolver;
  private final ServerRSocketFactoryResolver serverRSocketFactoryResolver;
  private final HandlersResolver handlersResolver;
  private final GenericApplicationContext ctx;

  public ServerConfigResolver(ApplicationContext ctx) {
    if (!(ctx instanceof GenericApplicationContext)) {
      throw new IllegalArgumentException(
          "Expected to be GenericApplicationContext, but got: " +
              ctx.getClass().getName());
    }
    GenericApplicationContext c = (GenericApplicationContext) ctx;
    R2BeanLocator beanLocator = new R2BeanLocator(c);
    this.transportResolver = new TransportResolver(beanLocator);
    this.codecResolver = new CodecResolver(beanLocator);
    this.serverRSocketFactoryResolver = new ServerRSocketFactoryResolver(c);
    this.handlersResolver = new HandlersResolver(c);
    this.ctx = c;
  }

  public Set<ServerConfig> resolve(Set<R2Properties> r2Properties) {
    return r2Properties
        .stream()
        .map(this::resolveServerConfig)
        .collect(toSet());
  }

  private ServerConfig resolveServerConfig(R2Properties props) {

    String name = props.getName();

    ServerRSocketFactory rSocketFactory = serverRSocketFactoryResolver.resolve(ctx);

    @SuppressWarnings("unchecked")
    ServerTransportFactory<Closeable> transportFactory =
        transportResolver
            .resolve(props.getTransport());

    ServerTransport<Closeable> transport = transportFactory
        .apply(props.getPort());

    List<DataCodec> codecs = codecResolver
        .resolve(props.getCodecs());

    List<String> apis = Optional.ofNullable(props.getApi())
        .orElse(emptyList());
    Function<ConnectionContext, Collection<Object>> handlers =
        handlersResolver.resolve(apis);

    return new ServerConfig(name, rSocketFactory, transport, codecs, handlers);
  }
}
