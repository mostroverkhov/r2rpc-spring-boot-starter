package com.github.mostroverkhov.r2.autoconfigure;

import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import java.util.function.Function;

public interface R2ServerApiHandlers<T> extends Function<ConnectionContext, T> {
}
