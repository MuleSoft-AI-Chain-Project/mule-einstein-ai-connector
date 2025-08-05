package com.mulesoft.connector.einsteinai.internal.connection;

import com.mulesoft.connector.einsteinai.internal.modelsapi.helpers.RequestHelper;
import com.mulesoft.connectors.commons.template.connection.ConnectorConnection;
import org.mule.runtime.http.api.client.HttpClient;

/**
 * This class represents a connection to the external system.
 */
// In future if we are adding new connection types, then common parameters of connection types will go here
public interface EinsteinConnection extends ConnectorConnection {

  String getInstanceUrl();

  String getApiVersion();

  String getApiInstanceUrl();

  RequestHelper getRequestHelper();

  String getAccessToken();

  HttpClient getHttpClient();
}
