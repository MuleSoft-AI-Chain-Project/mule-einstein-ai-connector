package com.mulesoft.connector.einsteinai.internal.helpers;

import com.mulesoft.connector.einsteinai.internal.error.EinsteinErrorType;
import org.junit.jupiter.api.Test;
import org.mule.runtime.extension.api.connectivity.oauth.AccessTokenExpiredException;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class HttpRequestHelperTest {

  @Test
  void readErrorStream_nullErrorStream() {
    String result = HttpRequestHelper.readErrorStream(null);
    assertEquals("No error details available.", result);
  }

  @Test
  void readErrorStream_ioException() throws Exception {
    InputStream mockErrorStream = mock(InputStream.class);
    when(mockErrorStream.read(any())).thenThrow(new IOException("Simulated IO Exception"));
    Logger mockLogger = mock(Logger.class);
    TestUtils.setFinalStatic(HttpRequestHelper.class, "log", mockLogger);

    String result = HttpRequestHelper.readErrorStream(mockErrorStream);

    assertEquals("Unable to get response from Einstein. Could not read reading error details as well.", result);
    verify(mockLogger).debug(eq("Error reading error stream"), any(IOException.class));

    // Reset the logger (optional, but good practice if other tests rely on it)
    TestUtils.resetFinalStatic(HttpRequestHelper.class, "log");
  }

  @Test
  void handleHttpResponse_okResponse_nullContent() {
    HttpResponse mockHttpResponse = mock(HttpResponse.class);
    HttpEntity mockHttpEntity = mock(HttpEntity.class);
    EinsteinErrorType errorType = EinsteinErrorType.MODELS_API_ERROR;

    when(mockHttpResponse.getStatusCode()).thenReturn(HttpURLConnection.HTTP_OK);
    when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
    when(mockHttpEntity.getContent()).thenReturn(null);

    ModuleException thrown =
        assertThrows(ModuleException.class, () -> HttpRequestHelper.handleHttpResponse(mockHttpResponse, errorType));
    assertEquals("Error: No response received from Einstein", thrown.getMessage());
    assertEquals(errorType, thrown.getType());
  }

  @Test
  void handleHttpResponse_unauthorizedResponse() {
    HttpResponse mockHttpResponse = mock(HttpResponse.class);

    when(mockHttpResponse.getStatusCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);

    assertThrows(AccessTokenExpiredException.class,
                 () -> HttpRequestHelper.handleHttpResponse(mockHttpResponse, EinsteinErrorType.MODELS_API_ERROR));
  }

  @Test
  void handleHttpResponse_withException_callsCallbackError() {
    HttpResponse mockHttpResponse = mock(HttpResponse.class);
    Throwable exception = new RuntimeException("Simulated Exception");
    CompletionCallback mockCallback = mock(CompletionCallback.class);
    ThrowingFunction mockConverter = mock(ThrowingFunction.class);

    HttpRequestHelper.handleHttpResponse(mockHttpResponse, exception, EinsteinErrorType.INVALID_CONNECTION, mockCallback,
                                         mockConverter);

    verify(mockCallback).error(exception);
    verifyNoInteractions(mockConverter);
  }

  @Test
  void handleHttpResponse_nullContentStream_returnsWithoutCallingConverter() throws IOException {
    HttpResponse mockHttpResponse = mock(HttpResponse.class);
    CompletionCallback mockCallback = mock(CompletionCallback.class);
    ThrowingFunction mockConverter = mock(ThrowingFunction.class);
    EinsteinErrorType errorType = EinsteinErrorType.MODELS_API_ERROR;

    when(mockHttpResponse.getStatusCode()).thenReturn(HttpURLConnection.HTTP_OK);
    when(mockHttpResponse.getEntity()).thenReturn(mock(HttpEntity.class));
    when(mockHttpResponse.getEntity().getContent()).thenReturn(null);

    HttpRequestHelper.handleHttpResponse(mockHttpResponse, null, errorType, mockCallback, mockConverter);

    verifyNoInteractions(mockConverter);
    verify(mockCallback).error(argThat(exception -> exception instanceof ModuleException &&
        "Error: No response received from Einstein".equals(exception.getMessage()) &&
        errorType.equals(((ModuleException) exception).getType())));
  }

  @Test
  void handleHttpResponse_successfulResponse_callsCallbackSuccess() throws IOException, ExecutionException {
    HttpResponse mockHttpResponse = mock(HttpResponse.class);
    HttpEntity mockHttpEntity = mock(HttpEntity.class);
    InputStream mockContentStream = new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8));
    CompletionCallback mockCallback = mock(CompletionCallback.class);
    ThrowingFunction<InputStream, Result<InputStream, String>> mockConverter = mock(ThrowingFunction.class);
    Result<InputStream, String> mockResult = mock(Result.class);
    EinsteinErrorType errorType = EinsteinErrorType.MODELS_API_ERROR;

    when(mockHttpResponse.getStatusCode()).thenReturn(HttpURLConnection.HTTP_OK);
    when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
    when(mockHttpEntity.getContent()).thenReturn(mockContentStream);
    when(mockConverter.apply(mockContentStream)).thenReturn(mockResult);

    HttpRequestHelper.handleHttpResponse(mockHttpResponse, null, errorType, mockCallback, mockConverter);

    verify(mockCallback).success(mockResult);
  }

  @Test
  void handleHttpResponse_responseConverterThrowsIOException_callsCallbackError() throws IOException, ExecutionException {
    HttpResponse mockHttpResponse = mock(HttpResponse.class);
    HttpEntity mockHttpEntity = mock(HttpEntity.class);
    InputStream mockContentStream = new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8));
    CompletionCallback mockCallback = mock(CompletionCallback.class);
    ThrowingFunction<InputStream, Result<InputStream, String>> mockConverter = mock(ThrowingFunction.class);
    IOException ioException = new IOException("Converter IO Error");
    EinsteinErrorType errorType = EinsteinErrorType.MODELS_API_ERROR;

    when(mockHttpResponse.getStatusCode()).thenReturn(HttpURLConnection.HTTP_OK);
    when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
    when(mockHttpEntity.getContent()).thenReturn(mockContentStream);
    when(mockConverter.apply(mockContentStream)).thenThrow(ioException);

    HttpRequestHelper.handleHttpResponse(mockHttpResponse, null, errorType, mockCallback, mockConverter);

    verify(mockCallback).error(ioException);
  }

  // Helper class to set final static fields for testing
  public static class TestUtils {

    public static void setFinalStatic(Class<?> clazz, String fieldName, Object newValue) throws Exception {
      java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);
      java.lang.reflect.Field modifiersField = java.lang.reflect.Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
      field.set(null, newValue);
    }

    public static void resetFinalStatic(Class<?> clazz, String fieldName) throws Exception {
      java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);
      java.lang.reflect.Field modifiersField = java.lang.reflect.Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(field, field.getModifiers() & java.lang.reflect.Modifier.FINAL);
      // You might need to reset to a default value depending on your needs
      field.set(null, null); // Or a specific default instance
    }
  }
}
