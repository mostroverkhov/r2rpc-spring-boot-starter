package com.github.mostroverkhov.r2.autoconfigure.internal.client;

import com.github.mostroverkhov.r2.autoconfigure.internal.PropertiesResolver;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.ClientEndpointProperties;
import com.github.mostroverkhov.r2.autoconfigure.internal.properties.DefaultProperties;
import com.github.mostroverkhov.r2.autoconfigure.internal.Verifications;

public class ClientPropertiesResolver extends PropertiesResolver<ClientEndpointProperties> {


  public ClientPropertiesResolver(DefaultProperties fallbackServerProps) {
    super(fallbackServerProps);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void verifications(Verifications<ClientEndpointProperties> verifications) {
    verifications.addVerifications(name());
  }
}
