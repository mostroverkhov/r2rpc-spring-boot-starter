package com.github.mostroverkhov.r2.autoconfigure.internal.resolvers;

import static java.util.stream.Collectors.toList;

import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;
import com.github.mostroverkhov.r2.autoconfigure.internal.R2BeanLocator;
import com.github.mostroverkhov.r2.core.DataCodec;
import java.util.List;

public class CodecResolver extends BeanTypeAndAnnotationResolver<DataCodec, R2DataCodec> {

  public CodecResolver(
      R2BeanLocator beanLocator) {
    super(beanLocator,
        DataCodec.class,
        R2DataCodec.class,
        CodecResolver::error);
  }

  public List<DataCodec> resolve(List<String> codecs) {
    return codecs.stream()
        .map(this::resolve)
        .collect(toList());
  }

  private static String error(String codec) {
    return String.format("No codec for name: %s", codec);
  }
}
