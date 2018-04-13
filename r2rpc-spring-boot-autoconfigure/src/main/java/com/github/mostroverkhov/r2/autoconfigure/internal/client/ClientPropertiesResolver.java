package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.github.mostroverkhov.r2.autoconfigure.internal.PropertiesResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.R2DefaultProperties;
import com.github.mostroverkhov.r2.autoconfigure.internal.Verifications;

public class ClientPropertiesResolver extends PropertiesResolver<R2ClientProperties> {


  public ClientPropertiesResolver(R2DefaultProperties fallbackServerProps) {
    super(fallbackServerProps);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void verifications(Verifications<R2ClientProperties> verifications) {
    verifications.addVerifications(name());
  }
}
