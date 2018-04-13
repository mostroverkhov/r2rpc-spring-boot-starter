package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.github.mostroverkhov.r2.autoconfigure.internal.PropertiesResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.DefaultProperties;
import com.github.mostroverkhov.r2.autoconfigure.internal.Verifications;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.RequesterEndpointProperties;

public class ClientPropertiesResolver extends PropertiesResolver<RequesterEndpointProperties> {


  public ClientPropertiesResolver(DefaultProperties fallbackServerProps) {
    super(fallbackServerProps);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void verifications(Verifications<RequesterEndpointProperties> verifications) {
    verifications.addVerifications(name());
  }
}
