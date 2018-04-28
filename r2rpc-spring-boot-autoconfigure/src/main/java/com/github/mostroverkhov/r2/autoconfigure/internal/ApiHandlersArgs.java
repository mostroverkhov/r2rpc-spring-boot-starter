package com.github.mostroverkhov.r2.autoconfigure.internal;

import java.util.Collection;

public class ApiHandlersArgs {
  private final Collection<String> apiHandlerNames;
  private final Collection<Class<?>> apiRequesters;

  public ApiHandlersArgs(Collection<String> apiHandlerNames,
                         Collection<Class<?>> apiRequesters) {
    this.apiHandlerNames = apiHandlerNames;
    this.apiRequesters = apiRequesters;
  }

  public Collection<String> handlerNames() {
    return apiHandlerNames;
  }

  public Collection<Class<?>> requesters() {
    return apiRequesters;
  }
}
