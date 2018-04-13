package com.github.mostroverkhov.r2.autoconfigure.internal.server;

import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import java.util.Collection;
import java.util.function.Function;

public interface ServiceHandlersFactory extends Function<ConnectionContext, Collection<Object>> {

}
