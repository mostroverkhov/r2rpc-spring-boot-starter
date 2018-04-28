package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.ApiRequesterFactory;
import com.github.mostroverkhov.r2.core.RequesterFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public abstract class BaseHandlersResolver<
    ApiHandlersFactory,
    ApiProvider,
    HandlersInfo extends ApiHandlersInfo<ApiProvider>> implements
    Resolver<ApiHandlersArgs, ApiHandlersFactory> {

  private final HandlersOperations<ApiProvider, HandlersInfo> ops;
  private final List<ApiProvider> providers;
  private Map<String, HandlersInfo> cache;

  public BaseHandlersResolver(List<ApiProvider> providers,
                              HandlersOperations<ApiProvider, HandlersInfo> ops) {
    Objects.requireNonNull(providers);
    Objects.requireNonNull(ops);
    this.providers = providers;
    this.ops = ops;
  }

  @Override
  public ApiHandlersFactory resolve(ApiHandlersArgs apiHandlers) {
    Objects.requireNonNull(apiHandlers);

    if (cache == null) {
      cache = new HashMap<>();
      resolveAll()
          .forEach(apiInfo -> {
            String name = apiInfo.name();
            HandlersInfo prev = cache.put(name, apiInfo);
            if (prev != null) {
              throw new IllegalArgumentException(
                  "Duplicate API implementation: " + name);
            }
          });
    }
    Collection<Class<?>> requesters = apiHandlers.requesters();
    Collection<String> apiHandlerNames = apiHandlers.handlerNames();

    Set<String> uniqueKeys = uniqueKeys(apiHandlerNames);
    Collection<HandlersInfo> handlerInfos = resolveApiHandlers(uniqueKeys);
    Function<RequesterFactory, ApiRequesterFactory> requesterFactoryConverter =
        converter(requesters);

    return handlersFactory(handlerInfos, requesterFactoryConverter);
  }

  public Collection<HandlersInfo> resolveAll() {
    return providers
        .stream()
        .map(ops::findProviderApi)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(toList());
  }

  protected abstract ApiHandlersFactory
  handlersFactory(Collection<HandlersInfo> apiInfos,
                  Function<RequesterFactory, ApiRequesterFactory>
                      requesterFactoryConverter);

  protected void assertTypesUnique(
      Collection<HandlersInfo> handlerInfos) {

    Set<Class<?>> apiTypes = handlerInfos
        .stream()
        .map(ApiHandlersInfo::type)
        .collect(Collectors.toSet());

    if (apiTypes.size() != handlerInfos.size()) {
      throw new IllegalArgumentException("There must be at most 1"
          + " implementation of API per endpoint");
    }
  }

  private static Set<String> uniqueKeys(Collection<String> handlerNames) {
    Set<String> uniqueKeys = new HashSet<>(handlerNames);
    if (uniqueKeys.size() != handlerNames.size()) {
      throw new IllegalArgumentException(
          "Handler names are not unique: " + handlerNames);
    }
    return uniqueKeys;
  }

  private static Function<RequesterFactory, ApiRequesterFactory> converter(
      Collection<Class<?>> apiRequesters) {
    return requesterFactory ->
        new ApiRequesterFactoryProxy(requesterFactory, apiRequesters);
  }

  private Collection<HandlersInfo> resolveApiHandlers(Set<String> apiNames) {
    return apiNames.stream()
        .map(name -> {
          HandlersInfo responderInfo = cache.get(name);
          if (responderInfo == null) {
            throw new IllegalArgumentException(
                "Absent API implementation for name: " + name);
          }
          return responderInfo;
        })
        .collect(toList());
  }
}
