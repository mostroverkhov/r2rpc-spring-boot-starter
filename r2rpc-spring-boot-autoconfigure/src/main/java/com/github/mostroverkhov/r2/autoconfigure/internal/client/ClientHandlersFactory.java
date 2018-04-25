package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.github.mostroverkhov.r2.core.RequesterFactory;

import java.util.Collection;
import java.util.function.Function;

public interface ClientHandlersFactory extends
    Function<RequesterFactory, Collection<Object>> {
}
