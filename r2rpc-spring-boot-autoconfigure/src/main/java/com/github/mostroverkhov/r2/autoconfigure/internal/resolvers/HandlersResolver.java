package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.github.mostroverkhov.r2.autoconfigure.R2Api;
import com.github.mostroverkhov.r2.autoconfigure.R2ApiName;
import com.github.mostroverkhov.r2.autoconfigure.ServerApiProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.ServiceHandlersFactory;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

public class HandlersResolver implements
    Resolver<Set<String>, ServiceHandlersFactory> {

  private final Supplier<Collection<ServerApiProvider>> providersSupplier;
  private Map<String, Api> cache;

  public HandlersResolver(ApplicationContext ctx) {
    this.providersSupplier = () ->
        ctx.getBeansOfType(ServerApiProvider.class)
            .values();
  }

  HandlersResolver(
      Supplier<Collection<ServerApiProvider>> providersSupplier) {
    Objects.requireNonNull(providersSupplier);
    this.providersSupplier = providersSupplier;
  }

  @Override
  public ServiceHandlersFactory resolve(Set<String> keys) {
    if (cache == null) {
      cache = new HashMap<>();
      resolveAll(providersSupplier)
          .forEach(api -> {
            String name = api.name();
            Api prev = cache.put(name, api);
            if (prev != null) {
              throw new IllegalArgumentException("Duplicate API implementation: " + name);
            }
          });
    }
    return asFactory(resolveApiNames(keys));
  }

  static Collection<Api> resolveAll(
      Supplier<Collection<ServerApiProvider>> providersSupplier) {
    return providersSupplier
        .get()
        .stream()
        .map(ApiResolveSteps::findProviderApi)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(toList());
  }

  private static ServiceHandlersFactory asFactory(Collection<Api> apis) {
    Set<Class<?>> apiTypes = apis
        .stream()
        .map(Api::type)
        .collect(toSet());
    if (apiTypes.size() != apis.size()) {
      throw new IllegalStateException("There must be at most 1"
          + " implementation of API per endpoint");
    }
    return connCtx ->
        apis.stream()
            .flatMap(api ->
                api.svcHandlersFactory()
                    .apply(connCtx)
                    .stream())
            .collect(Collectors.toList());
  }

  private Collection<Api> resolveApiNames(Set<String> apiNames) {
    return apiNames.stream()
        .map(name -> {
          Api api = cache.get(name);
          if (api == null) {
            throw new IllegalArgumentException(
                "Absent API implementation for name: " + name);
          }
          return api;
        })
        .collect(toList());
  }

  static class ApiResolveSteps {

    private static Optional<Api> findProviderApi(ServerApiProvider apiImplProvider) {
      Class<?> apisImplType = apiImplType(apiImplProvider);
      Class<?>[] maybeApis = apisImplType.getInterfaces();
      List<Api> apis = stream(maybeApis)
          .map(maybeApi -> findApi(maybeApi, apiImplProvider))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(toList());

      int apisSize = apis.size();
      if (apisSize > 1) {
        String msg = String
            .format("API implementation %s has of multiple APIs: %d",
                apisImplType.getName(),
                apisSize);
        throw new IllegalArgumentException(msg);
      }
      return apis.isEmpty()
          ? Optional.empty()
          : Optional.of(overrideApiName(apis.get(0), apisImplType));
    }

    private static Api overrideApiName(Api api, Class<?> apiImplType) {
      return findApiImplName(apiImplType)
          .map(api::copyWithName)
          .orElse(api);
    }

    private static Class<?> apiImplType(ServerApiProvider apiImplProvider) {
      try {
        Method method = apiImplProvider
            .getClass()
            .getMethod(
                "apply",
                ConnectionContext.class);
        return method.getReturnType();
      } catch (NoSuchMethodException e) {
        throw new AssertionError(
            "ServerApiProvider is expected "
                + "to have T apply(ConnectionContext c) method",
            e);
      }
    }

    private static Optional<Api> findApi(
        Class<?> maybeApiType,
        ServerApiProvider<?> apiImplProvider) {
      return findApiName(maybeApiType)
          .map(name -> new Api(name, maybeApiType, apiImplProvider));
    }

    static Optional<String> findApiName(Class<?> maybeApi) {
      R2ApiName r2ApiName = findAnnotation(maybeApi, R2ApiName.class);
      R2Api r2Api = findAnnotation(maybeApi, R2Api.class);
      if (r2ApiName != null) {
        if (r2Api != null) {
          return Optional.of(r2ApiName.value());
        } else {
          String msg = String
              .format("%s: @R2ApiName is present without @R2Api",
                  maybeApi.getName());
          throw new IllegalArgumentException(msg);
        }
      } else if (r2Api != null) {
        return Optional.of(r2Api.value());
      } else {
        return Optional.empty();
      }
    }

    private static Optional<String> findApiImplName(Class<?> apiImplType) {
      return Optional.ofNullable(findAnnotation(apiImplType, R2ApiName.class))
          .map(R2ApiName::value);
    }

    private static <T extends Annotation> T findAnnotation(Class<?> target,
        Class<T> anno) {
      return AnnotationUtils
          .findAnnotation(target, anno);
    }
  }

  static final class Api {

    private final String name;
    private final Class<?> type;
    private final Function<ConnectionContext, Collection<Object>> svcHandlersFactory;

    Api(
        String name,
        Class<?> type,
        ServerApiProvider<?> apiImplProvider) {
      this(name,
          type,
          apiImplProvider.andThen(resolveSvcHandlers(type)));
    }

    private Api(
        String name,
        Class<?> type,
        Function<ConnectionContext, Collection<Object>> svcHandlersFactory) {
      Objects.requireNonNull(name);
      Objects.requireNonNull(type);
      Objects.requireNonNull(svcHandlersFactory);
      this.name = name;
      this.type = type;
      this.svcHandlersFactory = svcHandlersFactory;
    }

    public Api copyWithName(String name) {
      return new Api(name, type, svcHandlersFactory);
    }

    public String name() {
      return name;
    }

    public Class<?> type() {
      return type;
    }

    public Function<ConnectionContext, Collection<Object>> svcHandlersFactory() {
      return svcHandlersFactory;
    }

    private static Function<Object, Collection<Object>> resolveSvcHandlers(Class<?> apiType) {
      return apiImpl ->
          stream(
              apiType.getMethods())
              .map(m -> call(m, apiImpl))
              .collect(toList());
    }

    private static Object call(Method m, Object target) {
      try {
        return m.invoke(target);
      } catch (Exception e) {
        String msg = String.format("Could not invoke %s on %s",
            m.getName(),
            target.getClass().getName());
        throw new IllegalStateException(msg, e);
      }
    }
  }
}
