package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

public interface Resolver<K, V> {

  V resolve(K key);
}
