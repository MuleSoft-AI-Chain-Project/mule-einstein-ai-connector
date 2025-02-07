package com.mulesoft.connector.einsteinai.internal.helpers;

public class EinsteinConstantUtil {

  private EinsteinConstantUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static final String HTTP_METHOD_POST = "POST";

  public static final String CONTENT_TYPE_STRING = "Content-Type";

  public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json;charset=utf-8";

  public static final String AUTHORIZATION = "Authorization";

  public static final Integer CONNECTION_TIME_OUT = 15000;

  public static final Integer READ_TIME_OUT = 20000;

}
