package com.github.mostroverkhov.r2.autoconfigure.client;

import com.github.mostroverkhov.r2.autoconfigure.Name;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;

@Name("tcp")
public class R2ClientTransportTcp implements R2ClientTransport {
  @Override
  public ClientTransport apply(String address, Integer port) {
    return TcpClientTransport.create(address, port);
  }
}
