package com.mulesoft.connector.einsteinai.internal.modelsapi.models;

import org.mule.runtime.api.metadata.TypedValue;

public class WrappedValue {

  private Object value;

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }
}
