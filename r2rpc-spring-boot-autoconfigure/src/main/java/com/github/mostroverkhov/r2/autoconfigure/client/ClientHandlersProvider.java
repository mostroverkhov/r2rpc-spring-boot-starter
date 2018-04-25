package com.github.mostroverkhov.r2.autoconfigure.client;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;

import java.util.function.Function;

public interface ClientHandlersProvider<T> extends Function<ApiRequesterFactory, T> {
}
