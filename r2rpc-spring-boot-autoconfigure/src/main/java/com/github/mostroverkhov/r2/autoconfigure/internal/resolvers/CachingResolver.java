package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

abstract class CachingResolver<K, V> implements Resolver<K, V> {

  private Map<K, V> cache;
  private final Function<K, String> errorMessage;

  CachingResolver(Function<K, String> errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public V resolve(K key) {
    if (cache == null) {
      cache = new HashMap<>();
      resolveAll(cache);
    }
    V v = cache.get(key);
    if (v == null) {
      throw new IllegalArgumentException(errorMessage.apply(key));
    }
    return v;
  }

  abstract void resolveAll(Map<K, V> cache);
}
