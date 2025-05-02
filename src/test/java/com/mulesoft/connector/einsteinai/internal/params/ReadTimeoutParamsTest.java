package com.mulesoft.connector.einsteinai.internal.params;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ReadTimeoutParamsTest {

  @Test
  void defaultConstructor() {
    ReadTimeoutParams params = new ReadTimeoutParams();
    assertNull(params.getReadTimeout());
    assertNull(params.getReadTimeoutUnit());
  }

  @Test
  void parameterizedConstructor() {
    Integer timeout = 10;
    TimeUnit unit = TimeUnit.SECONDS;
    ReadTimeoutParams params = new ReadTimeoutParams(timeout, unit);
    assertEquals(timeout, params.getReadTimeout());
    assertEquals(unit, params.getReadTimeoutUnit());
  }

  @Test
  void getReadTimeout() {
    Integer timeout = 30;
    ReadTimeoutParams params = new ReadTimeoutParams(timeout, TimeUnit.MILLISECONDS);
    assertEquals(timeout, params.getReadTimeout());
  }

  @Test
  void getReadTimeoutUnit() {
    TimeUnit unit = TimeUnit.MINUTES;
    ReadTimeoutParams params = new ReadTimeoutParams(5, unit);
    assertEquals(unit, params.getReadTimeoutUnit());
  }

  @Test
  void equalsSameObject() {
    ReadTimeoutParams params = new ReadTimeoutParams(5, TimeUnit.SECONDS);
    assertEquals(params, params);
  }

  @Test
  void equalsNullObject() {
    ReadTimeoutParams params = new ReadTimeoutParams(5, TimeUnit.SECONDS);
    assertNotEquals(null, params);
  }

  @Test
  void equalsDifferentClass() {
    ReadTimeoutParams params = new ReadTimeoutParams(5, TimeUnit.SECONDS);
    assertNotEquals("string", params);
  }

  @Test
  void equalsSameValues() {
    ReadTimeoutParams params1 = new ReadTimeoutParams(15, TimeUnit.NANOSECONDS);
    ReadTimeoutParams params2 = new ReadTimeoutParams(15, TimeUnit.NANOSECONDS);
    assertEquals(params1, params2);
  }

  @Test
  void equalsDifferentReadTimeout() {
    ReadTimeoutParams params1 = new ReadTimeoutParams(10, TimeUnit.SECONDS);
    ReadTimeoutParams params2 = new ReadTimeoutParams(20, TimeUnit.SECONDS);
    assertNotEquals(params1, params2);
  }

  @Test
  void equalsDifferentReadTimeoutUnit() {
    ReadTimeoutParams params1 = new ReadTimeoutParams(5, TimeUnit.MILLISECONDS);
    ReadTimeoutParams params2 = new ReadTimeoutParams(5, TimeUnit.SECONDS);
    assertNotEquals(params1, params2);
  }

  @Test
  void hashCodeSameValues() {
    ReadTimeoutParams params1 = new ReadTimeoutParams(25, TimeUnit.MINUTES);
    ReadTimeoutParams params2 = new ReadTimeoutParams(25, TimeUnit.MINUTES);
    assertEquals(params1.hashCode(), params2.hashCode());
  }

  @Test
  void hashCodeDifferentReadTimeout() {
    ReadTimeoutParams params1 = new ReadTimeoutParams(1, TimeUnit.HOURS);
    ReadTimeoutParams params2 = new ReadTimeoutParams(2, TimeUnit.HOURS);
    assertNotEquals(params1.hashCode(), params2.hashCode());
  }

  @Test
  void hashCodeDifferentReadTimeoutUnit() {
    ReadTimeoutParams params1 = new ReadTimeoutParams(30, TimeUnit.NANOSECONDS);
    ReadTimeoutParams params2 = new ReadTimeoutParams(30, TimeUnit.MILLISECONDS);
    assertNotEquals(params1.hashCode(), params2.hashCode());
  }
}
