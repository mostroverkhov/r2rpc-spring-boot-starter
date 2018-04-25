package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.R2Api;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public abstract class HandlersOperations<ApiProvider, HandlersInfo extends ApiHandlersInfo<ApiProvider>> {

  public final Optional<HandlersInfo> findProviderApi(
      ApiProvider apiImplProvider) {

    Class<?> apisImplType = findApiImplType(apiImplProvider);
    Class<?>[] apiCandidateTypes = apisImplType.getInterfaces();
    List<HandlersInfo> responderInfos = stream(apiCandidateTypes)
        .map(apiCandidateType -> findApi(apiCandidateType, apiImplProvider))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(toList());

    assertApisSize(responderInfos, apisImplType);

    return responderInfos.isEmpty()
        ? Optional.empty()
        : Optional.of(overrideApiName(responderInfos.get(0), apisImplType));
  }

  protected abstract HandlersInfo overrideApiName(HandlersInfo responderInfo,
                                        Class<?> apiImplType);

  protected abstract Class<?> findApiImplType(ApiProvider apiImplProvider);

  protected abstract Optional<HandlersInfo> findApi(
      Class<?> apiCandidateType,
      ApiProvider apiProvider);

  protected final Optional<String> findApiName(Class<?> maybeApi) {
    return Optional.ofNullable(
        findAnnotation(
            maybeApi,
            R2Api.class))
        .map(R2Api::value);
  }

  protected final Optional<String> findApiImplName(Class<?> apiImplType) {
    return Optional
        .ofNullable(apiImplType.getDeclaredAnnotation(R2Api.class))
        .map(R2Api::value);
  }

  private static void assertApisSize(List<?> responderInfos,
                                     Class<?> apisImplType) {
    int apisSize = responderInfos.size();
    if (apisSize > 1) {
      String msg = String
          .format("API implementation %s has of multiple APIs: %d",
              apisImplType.getName(),
              apisSize);
      throw new IllegalArgumentException(msg);
    }
  }

  private static <T extends Annotation> T findAnnotation(Class<?> target,
                                                         Class<T> anno) {
    return AnnotationUtils.findAnnotation(target, anno);
  }
}
