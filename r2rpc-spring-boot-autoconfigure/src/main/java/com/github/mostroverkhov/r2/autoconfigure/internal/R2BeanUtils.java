package com.github.mostroverkhov.r2.autoconfigure.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.support.GenericApplicationContext;

final class R2BeanUtils {

  private R2BeanUtils() {
  }

  public static <T> void cacheBeansWithAnnotation(
      List<T> beans,
      Class<? extends Annotation> targetAnnotationType,
      Map<String, T> res) {

    beans.forEach(bean -> {
      Annotation annotation = bean.getClass()
          .getAnnotation(targetAnnotationType);
      if (annotation != null) {
        Method method = getAnnotationMethod(annotation);
        String key = getAnnotationValue(annotation, method);
        res.put(key, bean);
      }
    });
  }

  private static String getAnnotationValue(Annotation annotation,
                                    Method method) {
    Class<?> returnType = method.getReturnType();
    if (returnType != String.class) {
      String msg = "Annotation Method %s return type must be String, but was %s";
      throw new IllegalArgumentException(String.format(msg, method, returnType));
    }
    try {
      return (String) method.invoke(annotation);
    } catch (Exception e) {
      String msg = "Error while invoking annotation %s value() method";
      throw new RuntimeException(String.format(msg, annotation), e);
    }
  }

  private static Method getAnnotationMethod(Annotation annotation) {
    try {
      return annotation.getClass().getMethod("value");
    } catch (NoSuchMethodException e) {
      String msg = "Annotation %s must have value() method";
      throw new IllegalArgumentException(String.format(msg, annotation), e);
    }
  }
}