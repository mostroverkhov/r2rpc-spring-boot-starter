package com.github.mostroverkhov.r2.autoconfigure.internal;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.MethodMetadata;

public class R2BeanLocator {

  private final GenericApplicationContext applicationContext;

  public R2BeanLocator(GenericApplicationContext applicationContext) {
    Objects.requireNonNull(applicationContext, "applicationContext is null");
    this.applicationContext = applicationContext;
  }

  public <T> void getBeansByTypeAndAnnotation(
      Class<T> type
      , Class<? extends Annotation> anno,
      Map<String, T> res) {

    GenericApplicationContext c = this.applicationContext;

    c.getBeansOfType(type).forEach((beanName, bean) -> {
      BeanDefinition beanDefinition = c.getBeanDefinition(beanName);
      if (beanDefinition instanceof AnnotatedBeanDefinition) {
        if (beanDefinition.getSource() instanceof MethodMetadata) {
          MethodMetadata beanMethod = (MethodMetadata) beanDefinition.getSource();
          String annotationType = anno.getName();
          if (beanMethod.isAnnotated(annotationType)) {
            Map<String, Object> annotationAttributes =
                beanMethod
                    .getAnnotationAttributes(annotationType);
            String key = extractKey(annotationAttributes)
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "R2-boot-starter annotations should have"
                                + " exactly 1 method with String return value"));
            res.put(key, bean);
          }
        }
      }
    });
  }

  private Optional<String> extractKey(Map<String, Object> annotationAttributes) {

    if (annotationAttributes == null || annotationAttributes.size() != 1) {
      return Optional.empty();
    }
    Entry<String, Object> attr = annotationAttributes
        .entrySet()
        .iterator()
        .next();
    Object value = attr.getValue();
    if (value instanceof String) {
      return Optional.of((String) value);
    } else {
      return Optional.empty();
    }
  }
}