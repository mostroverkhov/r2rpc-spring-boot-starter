package com.github.mostroverkhov.r2.autoconfigure.internal;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BeanLocatorTest {

  @Test
  public void resolvePresent() {
    HashMap<String, Object> foos = new HashMap<>();
    List<Object> fooList = Arrays.asList(new Foo("foo1"), new AnotherFoo("foo2"));
    R2BeanUtils.cacheBeansWithAnnotation(fooList, Bar.class, foos);
    Assertions.assertThat(foos).hasSize(2);
  }

  @Test
  public void resolveMissing() {
    HashMap<String, Object> foos = new HashMap<>();
    List<Object> objList = Arrays.asList(new Object(), new Object());
    R2BeanUtils.cacheBeansWithAnnotation(objList, Bar.class, foos);
    Assertions.assertThat(foos).hasSize(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveErrorZeroArgs() {
    HashMap<String, FooZero> foos = new HashMap<>();
    List<FooZero> fooList = Arrays.asList(new FooZero("foo1"), new FooZero("foo2"));
    R2BeanUtils.cacheBeansWithAnnotation(fooList, Bar0.class, foos);
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveErrorManyArgs() {
    HashMap<String, FooTwo> foos = new HashMap<>();
    List<FooTwo> fooList = Arrays.asList(new FooTwo("foo1"), new FooTwo("foo2"));
    R2BeanUtils.cacheBeansWithAnnotation(fooList, Bar2.class, foos);
    Assertions.assertThat(foos).hasSize(0);
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @interface Bar {

    String value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @interface Bar2 {

    String value1();

    String value2();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @interface Bar0 {
  }

  @Bar("bar")
  static class Foo {

    String foo;

    public Foo(String foo) {
      this.foo = foo;
    }

    public String getFoo() {
      return foo;
    }
  }

  @Bar("another-bar")
  static class AnotherFoo {

    String foo;

    public AnotherFoo(String foo) {
      this.foo = foo;
    }

    public String getFoo() {
      return foo;
    }
  }


  @Bar0
  static class FooZero {

    String foo;

    public FooZero(String foo) {
      this.foo = foo;
    }

    public String getFoo() {
      return foo;
    }
  }

  @Bar2(value1 = "bar1", value2 = "bar2")
  static class FooTwo {

    String foo;

    public FooTwo(String foo) {
      this.foo = foo;
    }

    public String getFoo() {
      return foo;
    }
  }


}
