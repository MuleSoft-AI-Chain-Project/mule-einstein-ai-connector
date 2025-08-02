package com.mulesoft.connector.einsteinai.internal.connection;

import com.mulesoft.connector.einsteinai.internal.error.EinsteinErrorType;
import com.mulesoft.connector.einsteinai.internal.modelsapi.helpers.RequestHelper;
import org.apache.commons.lang3.StringUtils;
import org.mule.runtime.extension.api.connectivity.oauth.ClientCredentialsState;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.http.api.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomOAuthClientCredentialsConnection implements EinsteinConnection {

  private static final Logger logger = LoggerFactory.getLogger(CustomOAuthClientCredentialsConnection.class);

  private final ClientCredentialsState clientCredentialsState;
  private final String instanceUrl;
  private final String apiVersion;
  private final String apiInstanceUrl;
  private final RequestHelper requestHelper;
  private final HttpClient httpClient;

  public CustomOAuthClientCredentialsConnection(ClientCredentialsState clientCredentialsState,
                                                String instanceUrl, String apiVersion, String apiInstanceUrl,
                                                HttpClient httpClient) {
    this.clientCredentialsState = clientCredentialsState;
    this.instanceUrl = instanceUrl;
    this.apiVersion = apiVersion;
    this.apiInstanceUrl = apiInstanceUrl;
    this.httpClient = httpClient;
    this.requestHelper = new RequestHelper(this);
  }

  @Override
  public void disconnect() {
    // Nothing to dispose
    logger.info("Inside CustomOAuthClientCredentialsConnection disconnect");
  }

  @Override
  public void validate() {
    logger.info("Inside CustomOAuthClientCredentialsConnection validate");
    if (StringUtils.isBlank(apiInstanceUrl)) {
      logger.error("Missing Configuration. Api Instance Url is empty");
      throw new ModuleException("Connection failed. Missing Configuration: Empty API Instance URL",
                                EinsteinErrorType.INVALID_CONNECTION);
    }
  }

  @Override
  public String getInstanceUrl() {
    return instanceUrl;
  }

  @Override
  public String getApiVersion() {
    return apiVersion;
  }

  public String getApiInstanceUrl() {
    return apiInstanceUrl;
  }

  @Override
  public RequestHelper getRequestHelper() {
    return requestHelper;
  }

  @Override
  public String getAccessToken() {
    return clientCredentialsState.getAccessToken();
  }

  @Override
  public HttpClient getHttpClient() {
    return httpClient;
  }
}
