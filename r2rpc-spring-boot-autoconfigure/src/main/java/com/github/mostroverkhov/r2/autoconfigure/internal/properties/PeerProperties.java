package com.github.mostroverkhov.r2.autoconfigure.internal.properties;

public class PeerProperties {
  private RequesterProperties requester;
  private ResponderProperties responder;

  public RequesterProperties getRequester() {
    return requester;
  }

  public void setRequester(RequesterProperties requester) {
    this.requester = requester;
  }

  public ResponderProperties getResponder() {
    return responder;
  }

  public void setResponder(ResponderProperties responder) {
    this.responder = responder;
  }

  @Override
  public String toString() {
    return "PeerProperties{" +
        "requesterProperties=" + requester +
        ", responderProperties=" + responder +
        '}';
  }
}
