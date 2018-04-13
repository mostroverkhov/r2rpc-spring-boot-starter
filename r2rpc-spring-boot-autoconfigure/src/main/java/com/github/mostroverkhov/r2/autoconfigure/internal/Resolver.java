package com.github.mostroverkhov.r2.autoconfigure.internal;

public interface Resolver<K, V> {

  V resolve(K key);
}
