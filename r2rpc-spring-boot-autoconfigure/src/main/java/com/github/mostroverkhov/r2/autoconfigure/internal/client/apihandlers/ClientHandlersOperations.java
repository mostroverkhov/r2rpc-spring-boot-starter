package com.github.mostroverkhov.r2.autoconfigure.internal.client.apihandlers;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.autoconfigure.client.ClientHandlersProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.HandlersOperations;

import java.lang.reflect.Method;
import java.util.Optional;

public class ClientHandlersOperations extends
    HandlersOperations<ClientHandlersProvider<?>,
            ClientHandlersInfo> {
  @Override
  protected ClientHandlersInfo overrideApiName(ClientHandlersInfo responderInfo,
                                               Class<?> apiImplType) {
    return findApiImplName(apiImplType)
        .map(responderInfo::copy)
        .orElse(responderInfo);
  }

  @Override
  protected Class<?> findApiImplType(ClientHandlersProvider<?> apiImplProvider) {
    try {
      Method method = apiImplProvider
          .getClass()
          .getMethod(
              "apply",
              ApiRequesterFactory.class);
      return method.getReturnType();
    } catch (NoSuchMethodException e) {
      throw new AssertionError(
          "ClientHandlersProvider is expected "
              + "to have T apply(ConnectionContext c) method",
          e);
    }
  }

  @Override
  protected Optional<ClientHandlersInfo> findApi(Class<?> apiCandidateType,
                                                 ClientHandlersProvider<?> clientHandlersProvider) {
    return findApiName(apiCandidateType)
        .map(name ->
            new ClientHandlersInfo(
                name,
                apiCandidateType,
                clientHandlersProvider));

  }
}
