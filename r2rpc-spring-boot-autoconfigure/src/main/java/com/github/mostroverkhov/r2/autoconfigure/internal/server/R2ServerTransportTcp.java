package com.github.mostroverkhov.r2.autoconfigure.internal.server;

import com.github.mostroverkhov.r2.autoconfigure.Name;
import com.github.mostroverkhov.r2.autoconfigure.server.R2ServerTransport;
import io.rsocket.transport.ServerTransport;
import io.rsocket.transport.netty.server.NettyContextCloseable;
import io.rsocket.transport.netty.server.TcpServerTransport;

@Name("tcp")
public class R2ServerTransportTcp implements
    R2ServerTransport<NettyContextCloseable> {
  @Override
  public ServerTransport<NettyContextCloseable> apply(Integer port) {
    return TcpServerTransport.create(port);
  }
}
