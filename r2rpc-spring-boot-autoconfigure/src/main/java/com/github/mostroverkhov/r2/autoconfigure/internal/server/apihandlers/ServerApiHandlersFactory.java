package com.github.mostroverkhov.r2.autoconfigure.internal.server.apihandlers;


import com.github.mostroverkhov.r2.core.ConnectionContext;
import com.github.mostroverkhov.r2.core.RequesterFactory;

import java.util.Collection;
import java.util.function.BiFunction;

public interface ServerApiHandlersFactory extends
    BiFunction<ConnectionContext, RequesterFactory, Collection<Object>> {
}
