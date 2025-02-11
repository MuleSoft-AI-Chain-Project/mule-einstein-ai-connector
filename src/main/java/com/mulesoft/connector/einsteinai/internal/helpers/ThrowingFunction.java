package com.mulesoft.connector.einsteinai.internal.helpers;

import java.io.IOException;

@FunctionalInterface
public interface ThrowingFunction<T, R> {

  R apply(T t) throws IOException;
}
