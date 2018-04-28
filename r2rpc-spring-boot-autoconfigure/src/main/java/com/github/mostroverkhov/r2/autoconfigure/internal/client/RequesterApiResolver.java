package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.github.mostroverkhov.r2.autoconfigure.R2Api;
import com.github.mostroverkhov.r2.autoconfigure.RequestersProvider;
import com.github.mostroverkhov.r2.autoconfigure.internal.CachingResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class RequesterApiResolver extends CachingResolver<String, Class<?>> {
  private static final Logger logger = LoggerFactory.getLogger(RequesterApiResolver.class);

  private final List<RequestersProvider> providers;

  public RequesterApiResolver(List<RequestersProvider> providers) {
    super(RequesterApiResolver::error);
    this.providers = providers;
  }

  private static String error(String key) {
    return String.format("R2 Client: Missing API for key: [%s]", key);
  }

  public List<Class<?>> resolve(List<String> apis) {
    return apis
        .stream()
        .map(this::resolve)
        .collect(toList());
  }

  @Override
  public void resolveAll(Map<String, Class<?>> cache) {
    providers.forEach(apiProvider ->
        resolveApis(apiProvider, cache));
  }

  void resolveApis(RequestersProvider<?> requestersProvider,
                   Map<String, Class<?>> cache) {
    String apiPackage = findApiPackage(requestersProvider);
    try {
      cacheApis(apiPackage, cache);
    } catch (IOException e) {
      String msg = "Error while reading package %s contents";
      throw new RuntimeException(String.format(msg, apiPackage), e);
    }
  }

  private String findApiPackage(RequestersProvider<?> requestersProvider) {
    Type superClass = requestersProvider.getClass().getGenericSuperclass();
    if (superClass instanceof ParameterizedType) {
      Type[] typeArgs = ((ParameterizedType) superClass).getActualTypeArguments();
      if (typeArgs.length == 1) {
        Type packageHint = typeArgs[0];
        if (packageHint instanceof Class) {
          return ((Class) packageHint).getPackage().getName();
        }
      }
    }
    throw new IllegalArgumentException("Impl must directly inherit from RequestersProvider, " +
        "and have non-parameterized type parameter");
  }

  private void cacheApis(String basePackage,
                         Map<String, Class<?>> cache) throws IOException {
    ResourcePatternResolver resourcePatternResolver =
        new PathMatchingResourcePatternResolver();
    MetadataReaderFactory metadataReaderFactory =
        new CachingMetadataReaderFactory(resourcePatternResolver);

    String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
        resolveBasePackage(basePackage) + "/" + "**/*.class";
    Resource[] resources = resourcePatternResolver
        .getResources(packageSearchPath);
    for (Resource resource : resources) {
      if (resource.isReadable()) {
        MetadataReader metadataReader = metadataReaderFactory
            .getMetadataReader(resource);
        tryAddApi(cache, metadataReader);
      }
    }
  }

  private String resolveBasePackage(String basePackage) {
    return ClassUtils.convertClassNameToResourcePath(
        SystemPropertyUtils.resolvePlaceholders(basePackage));
  }

  private void tryAddApi(Map<String, Class<?>> cache,
                         MetadataReader metadataReader) {
    try {
      Class<?> c = Class.forName(metadataReader.getClassMetadata().getClassName());
      if (c.isInterface()) {
        R2Api r2Api = c.getAnnotation(R2Api.class);
        if (r2Api != null) {
          String apiName = r2Api.value();
          Class<?> prev = cache.put(apiName, c);
          if (prev != null) {
            throw new IllegalArgumentException("Duplicate R2 Api: " + apiName);
          }
        }
      }
    } catch (Throwable e) {
      logger.debug("Error while reading R2 Api candidate", e);
    }
  }
}
