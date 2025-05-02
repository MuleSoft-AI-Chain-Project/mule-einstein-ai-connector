package com.mulesoft.connector.einsteinai.internal.connection;

import com.mulesoft.connector.einsteinai.internal.error.EinsteinErrorType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mule.runtime.extension.api.connectivity.oauth.ClientCredentialsState;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.http.api.client.HttpClient;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CustomOAuthClientCredentialsConnectionTest {

  @Mock
  private ClientCredentialsState mockClientCredentialsState;

  @Mock
  private HttpClient mockHttpClient;

  @Test
  void validate_emptyApiInstanceUrl_throwsModuleException() {
    String emptyApiInstanceUrl = "";
    CustomOAuthClientCredentialsConnection connection =
        new CustomOAuthClientCredentialsConnection(mockClientCredentialsState, emptyApiInstanceUrl, mockHttpClient);

    ModuleException thrown = assertThrows(ModuleException.class, connection::validate);

    assertEquals("Connection failed. Missing Configuration: Empty API Instance URL", thrown.getMessage());
    assertEquals(EinsteinErrorType.INVALID_CONNECTION, thrown.getType());
  }

  @Test
  void validate_nullApiInstanceUrl_throwsModuleException() {
    String nullApiInstanceUrl = null;
    CustomOAuthClientCredentialsConnection connection =
        new CustomOAuthClientCredentialsConnection(mockClientCredentialsState, nullApiInstanceUrl, mockHttpClient);

    ModuleException thrown = assertThrows(ModuleException.class, connection::validate);

    assertEquals("Connection failed. Missing Configuration: Empty API Instance URL", thrown.getMessage());
    assertEquals(EinsteinErrorType.INVALID_CONNECTION, thrown.getType());
  }
}
