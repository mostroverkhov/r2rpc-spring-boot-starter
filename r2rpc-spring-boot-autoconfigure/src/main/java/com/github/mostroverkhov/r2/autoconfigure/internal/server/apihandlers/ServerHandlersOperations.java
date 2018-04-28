package com.github.mostroverkhov.r2.autoconfigure.internal.server.apihandlers;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.autoconfigure.internal.ApiRequesterFactoryProxy;
import com.github.mostroverkhov.r2.autoconfigure.internal.HandlersOperations;
import com.github.mostroverkhov.r2.autoconfigure.server.ServerHandlersProvider;
import com.github.mostroverkhov.r2.core.ConnectionContext;

import java.lang.reflect.Method;
import java.util.Optional;

class ServerHandlersOperations extends
    HandlersOperations<ServerHandlersProvider<?>, ServerHandlersInfo> {

  @Override
  protected Class<?> findApiImplType(ServerHandlersProvider apiImplProvider) {
    try {
      Method method = apiImplProvider
          .getClass()
          .getMethod(
              "apply",
              ConnectionContext.class,
              ApiRequesterFactory.class);
      return method.getReturnType();
    } catch (NoSuchMethodException e) {
      throw new AssertionError(
          "ServerHandlersProvider is expected "
              + "to have T apply(ConnectionContext c) method",
          e);
    }
  }

  @Override
  protected ServerHandlersInfo overrideApiName(ServerHandlersInfo responderInfo,
                                               Class<?> apiImplType) {
    return findApiImplName(apiImplType)
        .map(responderInfo::copy)
        .orElse(responderInfo);
  }

  @Override
  protected Optional<ServerHandlersInfo> findApi(
      Class<?> apiCandidateType,
      ServerHandlersProvider<?> serverHandlersProvider) {
    return findApiName(apiCandidateType)
        .map(name ->
            new ServerHandlersInfo(
                name,
                apiCandidateType,
                serverHandlersProvider));
  }
}
