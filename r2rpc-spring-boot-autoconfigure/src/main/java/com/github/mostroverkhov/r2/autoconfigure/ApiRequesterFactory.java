package com.github.mostroverkhov.r2.autoconfigure;

public interface ApiRequesterFactory {

  <T> T create(Class<T> api);
}
