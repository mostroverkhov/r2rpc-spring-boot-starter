package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import com.github.mostroverkhov.r2.autoconfigure.R2Api;
import com.github.mostroverkhov.r2.autoconfigure.R2ApiName;
import com.github.mostroverkhov.r2.autoconfigure.R2ServerApiHandlers;
import com.github.mostroverkhov.r2.core.responder.ConnectionContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

public class HandlersResolver extends
    Resolver<String, Function<ConnectionContext, List<Object>>> {

  private ApplicationContext ctx;

  public HandlersResolver(ApplicationContext ctx) {
    super(HandlersResolver::error);
    this.ctx = ctx;
  }

  @Override
  public Function<ConnectionContext, List<Object>> resolve(String key) {
    if (key == null) {
      return ctx -> Collections.emptyList();
    } else {
      return super.resolve(key);
    }
  }

  @Override
  void resolveAll(Map<String, Function<ConnectionContext, List<Object>>> cache) {
    Collection<R2ServerApiHandlers> apiHandlers = ctx
        .getBeansOfType(R2ServerApiHandlers.class)
        .values();

    apiHandlers.forEach(h -> {
      Optional<String> handlersName = getHandlersName(h);
      handlersName.ifPresent(name -> {
        Function<ConnectionContext, List<Object>> f = ctx -> {
          Object handlersInstance = h.apply(ctx);
          return stream(handlersInstance
              .getClass()
              .getMethods())
              .filter(m -> m.getDeclaringClass() != Object.class)
              .map(m -> call(m, handlersInstance))
              .collect(toList());
        };
        cache.put(name, f);
      });
    });
  }

  private Object call(Method m, Object target) {
    try {
      m.setAccessible(true);
      return m.invoke(target);
    } catch (Exception e) {
      String msg = String.format("Could not invoke %s on %s",
          m.getName(),
          target.getClass().getName());
      throw new IllegalStateException(msg, e);
    }
  }

  private Optional<String> getHandlersName(R2ServerApiHandlers h) {
    try {
      Method method = h.getClass()
          .getMethod(
              "apply",
              ConnectionContext.class);

      String apiName = Optional.ofNullable(findApiName(method))
          .map(R2ApiName::value)
          .orElseGet(() -> Optional.ofNullable(findApi(method))
              .map(R2Api::value).orElse(null));

      return Optional
          .ofNullable(apiName);

    } catch (NoSuchMethodException e) {
      throw new IllegalStateException(
          "R2ServerApiHandlers must have T apply(ConnectionContext c) method",
          e);
    }
  }

  private static R2Api findApi(Method method) {
    return findAnnotation(method, R2Api.class);
  }

  private static R2ApiName findApiName(Method method) {
    return findAnnotation(method, R2ApiName.class);
  }

  private static <T extends Annotation> T findAnnotation(Method method, Class<T> anno) {
    return AnnotationUtils
        .findAnnotation(
            method.getReturnType(),
            anno);
  }

  private static String error(String apiName) {
    return String.format("No API handlers registered for name: %s", apiName);
  }
}
