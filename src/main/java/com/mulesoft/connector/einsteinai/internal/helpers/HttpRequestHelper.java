package com.mulesoft.connector.einsteinai.internal.helpers;

import com.mulesoft.connector.einsteinai.internal.error.EinsteinErrorType;
import org.mule.runtime.extension.api.connectivity.oauth.AccessTokenExpiredException;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class HttpRequestHelper {

  private HttpRequestHelper() {
    throw new IllegalStateException("Utility class");
  }

  private static final Logger log = LoggerFactory.getLogger(HttpRequestHelper.class);

  public static String readResponseStream(InputStream inputStream) throws IOException {
    try (BufferedReader br = new BufferedReader(
                                                new InputStreamReader(inputStream,
                                                                      StandardCharsets.UTF_8))) {
      StringBuilder response = new StringBuilder();
      String responseLine;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
      return response.toString();
    }
  }

  public static String readErrorStream(InputStream errorStream) {
    if (errorStream == null) {
      return "No error details available.";
    }
    try (BufferedReader br = new BufferedReader(new InputStreamReader(errorStream, StandardCharsets.UTF_8))) {
      StringBuilder errorResponse = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        errorResponse.append(line.trim());
      }
      return errorResponse.toString();
    } catch (IOException e) {
      log.debug("Error reading error stream", e);
      return "Unable to get response from Einstein. Could not read reading error details as well.";
    }
  }

  public static InputStream handleHttpResponse(HttpResponse httpResponse, EinsteinErrorType errorType) {
    int responseCode = httpResponse.getStatusCode();

    if (responseCode == HttpURLConnection.HTTP_OK) {
      if (httpResponse.getEntity().getContent() == null) {
        throw new ModuleException(
                                  "Error: No response received from Einstein", errorType);
      }
      return httpResponse.getEntity().getContent();
    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
      throw new AccessTokenExpiredException();
    } else {
      String errorMessage = readErrorStream(httpResponse.getEntity().getContent());
      log.info("Error in HTTP request. Response code: {}, message: {}", responseCode, errorMessage);
      throw new ModuleException(
                                String.format("Error in HTTP request. ErrorCode: %d ," +
                                    " ErrorMessage: %s", responseCode, errorMessage),
                                errorType);
    }
  }

  public static <A> void handleHttpResponse(HttpResponse httpResponse, Throwable exception,
                                            EinsteinErrorType einsteinErrorType,
                                            CompletionCallback<InputStream, A> callback,
                                            ThrowingFunction<InputStream, Result<InputStream, A>> responseConverter) {
    if (exception != null) {
      callback.error(exception);
      return;
    }
    InputStream contentStream = parseHttpResponse(httpResponse, einsteinErrorType, callback);
    if (contentStream == null) {
      return;
    }
    try {
      callback.success(responseConverter.apply(contentStream));
    } catch (IOException e) {
      callback.error(e);
    }
  }

  public static String handleHttpResponseForTools(HttpResponse httpResponse) throws IOException {
    // Read the response stream
    InputStream responseStream = httpResponse.getEntity().getContent();

    try (BufferedReader in = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
      StringBuilder response = new StringBuilder();
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      return response.toString();
    }
  }

  private static InputStream parseHttpResponse(HttpResponse httpResponse, EinsteinErrorType einsteinErrorType,
                                               CompletionCallback callback) {

    int statusCode = httpResponse.getStatusCode();
    log.debug("Parsing Http Response, statusCode = {}", statusCode);

    if (statusCode == HttpURLConnection.HTTP_OK) {
      if (httpResponse.getEntity().getContent() == null) {
        callback.error(new ModuleException(
                                           "Error: No response received from Einstein", einsteinErrorType));
      }
      return httpResponse.getEntity().getContent();
    } else if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
      callback.error(new AccessTokenExpiredException());
    } else {
      String errorMessage = readErrorStream(httpResponse.getEntity().getContent());
      log.info("Error in HTTP request. Response code: {}, message: {}", statusCode, errorMessage);
      callback.error(new ModuleException(String.format("Error in HTTP request. ErrorCode: %d, ErrorMessage: %s", statusCode,
                                                       errorMessage),
                                         einsteinErrorType));
    }
    return null;
  }
}
