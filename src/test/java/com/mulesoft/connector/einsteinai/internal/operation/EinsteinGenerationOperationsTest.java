package com.mulesoft.connector.einsteinai.internal.operation;

import com.mulesoft.connector.einsteinai.api.metadata.EinsteinResponseAttributes;
import com.mulesoft.connector.einsteinai.api.metadata.ResponseParameters;
import com.mulesoft.connector.einsteinai.internal.connection.EinsteinConnection;
import com.mulesoft.connector.einsteinai.internal.modelsapi.helpers.RequestHelper;
import com.mulesoft.connector.einsteinai.internal.modelsapi.models.ParamsModelDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;

import java.io.InputStream;

import static com.mulesoft.connector.einsteinai.internal.error.EinsteinErrorType.CHAT_FAILURE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EinsteinGenerationOperationsTest {

  private EinsteinGenerationOperations einsteinGenerationOperations;

  @Mock
  private RequestHelper requestHelperMock;

  @Mock
  private EinsteinConnection connectionMock;

  @Mock
  private ParamsModelDetails paramDetailsMock;

  @Mock
  private CompletionCallback<InputStream, EinsteinResponseAttributes> callbackMock;

  @Mock
  private CompletionCallback<InputStream, ResponseParameters> callbackMock2;


  private AutoCloseable closeable;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    einsteinGenerationOperations = new EinsteinGenerationOperations();
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close(); // Close the resource to avoid resource leaks
  }

  @Test
  void testDefinePromptTemplateFailure() {
    String template = "Template";
    String instructions = "Instructions";
    String dataset = "Dataset";

    when(connectionMock.getRequestHelper()).thenReturn(requestHelperMock);
    doThrow(new RuntimeException("Test exception"))
        .when(requestHelperMock).executeGenerateText(anyString(), any(), any());

    einsteinGenerationOperations.definePromptTemplate(connectionMock, template, instructions, dataset, paramDetailsMock,
                                                      callbackMock);

    // Verify that callback.error() was called with the expected ModuleException
    verify(callbackMock).error(argThat(exception -> exception instanceof ModuleException &&
        exception.getMessage()
            .equals("Error while generating prompt from template Template, instructions Instructions, dataset Dataset")
        &&
        ((ModuleException) exception).getType().equals(CHAT_FAILURE)));
  }


  @Test
  void testGenerateTextFailure() {
    String prompt = "Test Prompt";
    when(connectionMock.getRequestHelper()).thenReturn(requestHelperMock);

    doThrow(new RuntimeException("Test exception"))
        .when(requestHelperMock).executeGenerateText(anyString(), any(), any());

    einsteinGenerationOperations.generateText(connectionMock, prompt, paramDetailsMock, callbackMock);

    verify(callbackMock).error(argThat(exception -> exception instanceof ModuleException &&
        exception.getMessage().equals("Error while generating text for prompt Test Prompt") &&
        ((ModuleException) exception).getType().equals(CHAT_FAILURE)));
  }

  @Test
  void testGenerateChatFailure() {
    String messages = "Test Messages";
    when(connectionMock.getRequestHelper()).thenReturn(requestHelperMock);

    doThrow(new RuntimeException("Test exception"))
        .when(requestHelperMock).generateChatFromMessages(
                                                          anyString(), any(), any());

    einsteinGenerationOperations.generateChatFromMessages(connectionMock, messages, paramDetailsMock, callbackMock2);

    verify(callbackMock2).error(argThat(exception -> exception instanceof ModuleException &&
        exception.getMessage().equals("Error while generating the chat from messages Test Messages") &&
        ((ModuleException) exception).getType() == CHAT_FAILURE));
  }
}

