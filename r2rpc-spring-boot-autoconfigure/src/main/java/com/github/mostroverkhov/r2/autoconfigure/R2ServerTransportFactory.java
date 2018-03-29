package com.github.mostroverkhov.r2.autoconfigure;

import io.rsocket.Closeable;
import io.rsocket.transport.ServerTransport;
import java.util.function.Function;

public interface R2ServerTransportFactory<T extends Closeable> extends
    Function<Integer, ServerTransport<T>> {

}
