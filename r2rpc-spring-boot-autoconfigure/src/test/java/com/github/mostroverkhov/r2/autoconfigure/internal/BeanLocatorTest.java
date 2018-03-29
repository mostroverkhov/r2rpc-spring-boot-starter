package com.github.mostroverkhov.r2.autoconfigure.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BeanLocatorTest {

  @Autowired
  private GenericApplicationContext ctx;
  private R2BeanLocator beanLocator;

  @Before
  public void setUp() {
    beanLocator = new R2BeanLocator(ctx);
  }

  @Test
  public void resolvePresent() {
    HashMap<String, Foo> foos = new HashMap<>();
    beanLocator.getBeansByTypeAndAnnotation(Foo.class, Bar.class, foos);
    Assertions.assertThat(foos).hasSize(2);
  }

  @Test
  public void resolveMissing() {
    HashMap<String, List> foos = new HashMap<>();
    beanLocator.getBeansByTypeAndAnnotation(List.class, Bar.class, foos);
    Assertions.assertThat(foos).hasSize(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveErrorManyArgs() {
    HashMap<String, Foo> foos = new HashMap<>();
    beanLocator.getBeansByTypeAndAnnotation(Foo.class, Bar0.class, foos);
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveErrorZeroArgs() {
    HashMap<String, Foo> foos = new HashMap<>();
    beanLocator.getBeansByTypeAndAnnotation(Foo.class, Bar2.class, foos);
    Assertions.assertThat(foos).hasSize(0);
  }

  @Configuration
  static class TestConfig {

    @Bean
    @Bar("bar1")
    public Foo foo1() {
      return new Foo("foo1");
    }

    @Bean
    @Bar("bar2")
    public Foo foo2() {
      return new Foo("foo2");
    }

    @Bean
    @Bar2(value1 = "bar1",value2 = "bar2")
    public Foo foo3() {
      return new Foo("foo3");
    }

    @Bean
    @Bar0
    public Foo foo4() {
      return new Foo("foo4");
    }
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  @interface Bar {

    String value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  @interface Bar2 {

    String value1();

    String value2();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  @interface Bar0 {
  }

  static class Foo {

    String foo;

    public Foo(String foo) {
      this.foo = foo;
    }

    public String getFoo() {
      return foo;
    }
  }
}
