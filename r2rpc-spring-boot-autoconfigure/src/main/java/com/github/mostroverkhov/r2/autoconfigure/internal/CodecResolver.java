package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class CodecResolver extends NamedBeanResolver<R2DataCodec> {

  public CodecResolver(List<R2DataCodec> dataCodecs) {
    super(dataCodecs, CodecResolver::error
    );
  }

  public List<R2DataCodec> resolve(List<String> codecs) {
    return codecs.stream()
        .map(this::resolve)
        .collect(toList());
  }

  private static String error(String codec) {
    return String.format("No codec for name: %s", codec);
  }
}
