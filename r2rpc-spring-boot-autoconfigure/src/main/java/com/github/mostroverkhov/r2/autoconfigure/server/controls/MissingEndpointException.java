package com.github.mostroverkhov.r2.autoconfigure.server.controls;

public class MissingEndpointException extends RuntimeException {

  private String endpointName;

  MissingEndpointException() {
  }

  public MissingEndpointException(
      String endpointName) {
    super("Missing configuration for endpoint: " + endpointName);
    this.endpointName = endpointName;
  }

  public String getEndpointName() {
    return endpointName;
  }
}
