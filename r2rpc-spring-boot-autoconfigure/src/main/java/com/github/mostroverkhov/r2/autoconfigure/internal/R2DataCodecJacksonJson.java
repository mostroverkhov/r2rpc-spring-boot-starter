package com.github.mostroverkhov.r2.autoconfigure.internal;

import com.github.mostroverkhov.r2.autoconfigure.R2DataCodec;
import com.github.mostroverkhov.r2.autoconfigure.Name;
import com.github.mostroverkhov.r2.codec.jackson.JacksonJsonDataCodec;
import com.github.mostroverkhov.r2.core.DataCodec;

@Name("json")
public class R2DataCodecJacksonJson implements R2DataCodec {
  private final DataCodec dataCodec = new JacksonJsonDataCodec();

  @Override
  public DataCodec get() {
    return dataCodec;
  }
}
