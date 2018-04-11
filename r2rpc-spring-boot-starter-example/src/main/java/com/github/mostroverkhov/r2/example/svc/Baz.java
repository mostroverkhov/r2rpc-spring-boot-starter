package com.github.mostroverkhov.r2.example.svc;

public class Baz {

  private String baz;

  public Baz(String baz) {
    this.baz = baz;
  }

  public Baz() {
  }

  public String getBaz() {
    return baz;
  }

  public void setBaz(String baz) {
    this.baz = baz;
  }

  @Override
  public String toString() {
    return "Baz{" +
        "baz='" + baz + '\'' +
        '}';
  }
}
