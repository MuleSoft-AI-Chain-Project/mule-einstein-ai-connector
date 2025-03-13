package com.mulesoft.connector.einsteinai.internal.modelsapi.helpers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperProvider {


  // Private constructor to prevent instantiation
  private ObjectMapperProvider() {}

  public static ObjectMapper create() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }
}
