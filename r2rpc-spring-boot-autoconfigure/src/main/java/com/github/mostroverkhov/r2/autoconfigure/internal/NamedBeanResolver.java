package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.Name;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class NamedBeanResolver<Type>
    extends CachingResolver<String, Type> {

  private final List<Type> beans;

  public NamedBeanResolver(List<Type> beans,
                           Function<String, String> errorMessage) {
    super(errorMessage);
    this.beans = beans;
  }

  @Override
  public void resolveAll(Map<String, Type> cache) {
    R2BeanUtils.cacheBeansWithAnnotation(beans, Name.class, cache);
  }
}
