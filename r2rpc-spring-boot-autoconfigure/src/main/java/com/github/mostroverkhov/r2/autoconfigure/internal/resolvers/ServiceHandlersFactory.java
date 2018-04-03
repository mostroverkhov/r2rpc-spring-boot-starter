package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import java.util.Collection;
import java.util.function.Function;

interface ServiceHandlersFactory extends Function<ConnectionContext, Collection<Object>> {

}
