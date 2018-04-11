package com.github.mostroverkhov.r2.autoconfigure.server;

import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import java.util.function.Function;

public interface ResponderApiProvider<T> extends Function<ConnectionContext, T> {

}
