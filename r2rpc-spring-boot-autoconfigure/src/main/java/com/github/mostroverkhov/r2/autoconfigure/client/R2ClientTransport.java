package com.github.mostroverkhov.r2.autoconfigure.client;

import io.rsocket.transport.ClientTransport;

import java.util.function.BiFunction;

public interface R2ClientTransport extends BiFunction<String, Integer, ClientTransport> {
}
