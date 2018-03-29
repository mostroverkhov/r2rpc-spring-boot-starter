package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

import com.github.mostroverkhov.r2.autoconfigure.internal.R2BeanLocator;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.function.Function;

class BeanTypeAndAnnotationResolver<
    Type,
    Anno extends Annotation> extends Resolver<String, Type> {

  private final R2BeanLocator beanLocator;
  private final Class<Type> type;
  private final Class<Anno> anno;

  public BeanTypeAndAnnotationResolver(R2BeanLocator beanLocator,
      Class<Type> type,
      Class<Anno> anno,
      Function<String, String> errorMessage) {
    super(errorMessage);
    this.beanLocator = beanLocator;
    this.type = type;
    this.anno = anno;
  }

  @Override
  protected void resolveAll(Map<String, Type> cache) {
    beanLocator.getBeansByTypeAndAnnotation(type, anno, cache);
  }
}
