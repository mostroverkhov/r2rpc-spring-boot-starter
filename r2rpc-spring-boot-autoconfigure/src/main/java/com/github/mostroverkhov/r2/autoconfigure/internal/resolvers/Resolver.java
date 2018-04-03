package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

interface Resolver<K, V> {

  V resolve(K key);
}
