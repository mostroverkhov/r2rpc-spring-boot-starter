package com.github.mostroverkhov.r2.example.svc;

public class Bar {

  private String bar;

  public Bar(String bar) {
    this.bar = bar;
  }

  public Bar() {
  }

  public String getBar() {
    return bar;
  }

  public void setBar(String bar) {
    this.bar = bar;
  }

  @Override
  public String toString() {
    return "Bar{" +
        "bar='" + bar + '\'' +
        '}';
  }
}
