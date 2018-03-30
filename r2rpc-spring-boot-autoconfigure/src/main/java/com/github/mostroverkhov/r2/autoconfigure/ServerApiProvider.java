package com.github.mostroverkhov.r2.autoconfigure;

import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import java.util.function.Function;

public interface ServerApiProvider<T> extends Function<ConnectionContext, T> {

}
