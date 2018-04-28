package com.github.mostroverkhov.r2.autoconfigure;

import com.github.mostroverkhov.r2.core.RequesterFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.*;

public class ApiRequesterFactory {
  private final Set<Class<?>> apis;
  private final RequesterFactory requesterFactory;

  public ApiRequesterFactory(RequesterFactory requesterFactory,
                             Collection<Class<?>> apis) {
    this.apis = new HashSet<>(apis);
    this.requesterFactory = requesterFactory;
  }

  @SuppressWarnings("unchecked")
  public <T> T create(Class<T> api) {
    Objects.requireNonNull(api);
    checkSupportedApi(api);

    return (T) Proxy.newProxyInstance(
        api.getClassLoader(),
        new Class[]{api},
        (proxy, method, args) -> {
          if (method.getDeclaringClass() == Object.class) {
            return method.invoke(proxy, args);
          }
          Type returnType = method.getGenericReturnType();
          if (returnType instanceof Class<?>) {
            Class<?> contractType = (Class<?>) returnType;
            if (contractType.isInterface()) {
              return requesterFactory.create(contractType);
            } else {
              throw interfaceExpectedException(method);
            }
          } else {
            throw simpleClassExpectedException(method);
          }
        });
  }

  private static RuntimeException interfaceExpectedException(Method method) {
    String msg = "Method %s must return Interface";
    return new IllegalArgumentException(String.format(msg, method));
  }

  private static RuntimeException simpleClassExpectedException(Method method) {
    String msg = "Method %s return type must be of type Class<?>";
    return new IllegalArgumentException(String.format(msg, method));
  }

  private <T> void checkSupportedApi(Class<T> api) {
    if (!apis.contains(api)) {
      throw new IllegalArgumentException("Unsupported API: " + api.getName());
    }
  }
}
