package com.github.mostroverkhov.r2.autoconfigure.internal.client.apihandlers;

import com.github.mostroverkhov.r2.core.RequesterFactory;

import java.util.Collection;
import java.util.function.Function;

public interface ClientApiHandlersFactory extends Function<RequesterFactory, Collection<Object>> {
}
