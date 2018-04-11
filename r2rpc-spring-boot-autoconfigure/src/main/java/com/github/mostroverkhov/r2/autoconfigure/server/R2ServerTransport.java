package com.github.mostroverkhov.r2.autoconfigure.server;

import io.rsocket.Closeable;
import io.rsocket.transport.ServerTransport;
import java.util.function.Function;

public interface R2ServerTransport<T extends Closeable> extends
    Function<Integer, ServerTransport<T>> {

}
