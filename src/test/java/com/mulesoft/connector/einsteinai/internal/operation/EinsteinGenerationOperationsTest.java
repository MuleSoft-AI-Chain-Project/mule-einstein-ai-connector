package com.mulesoft.connector.einsteinai.internal.operation;

import com.mulesoft.connector.einsteinai.api.metadata.EinsteinPromptTemplateGenerationsResponseAttributes;
import com.mulesoft.connector.einsteinai.api.metadata.EinsteinResponseAttributes;
import com.mulesoft.connector.einsteinai.api.metadata.ResponseParameters;
import com.mulesoft.connector.einsteinai.internal.connection.EinsteinConnection;
import com.mulesoft.connector.einsteinai.internal.modelsapi.helpers.RequestHelper;
import com.mulesoft.connector.einsteinai.internal.modelsapi.models.EinsteinLlmAdditionalConfigInputRepresentation;
import com.mulesoft.connector.einsteinai.internal.modelsapi.models.ParamsModelDetails;
import com.mulesoft.connector.einsteinai.internal.modelsapi.models.PromptParamsDetails;
import com.mulesoft.connector.einsteinai.internal.modelsapi.models.WrappedValue;
import com.mulesoft.connector.einsteinai.internal.params.ReadTimeoutParams;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mulesoft.connector.einsteinai.internal.error.EinsteinErrorType.CHAT_FAILURE;
import static com.mulesoft.connector.einsteinai.internal.error.EinsteinErrorType.PROMPT_TEMPLATE_GENERATIONS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
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

  @Mock
  private CompletionCallback<InputStream, EinsteinPromptTemplateGenerationsResponseAttributes> callbackMock3;

  private ReadTimeoutParams readTimeout;

  private AutoCloseable closeable;


  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    einsteinGenerationOperations = new EinsteinGenerationOperations();
    readTimeout = new ReadTimeoutParams(30, TimeUnit.SECONDS);
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
        .when(requestHelperMock).executeGenerateText(anyString(), any(), eq(readTimeout), any());

    einsteinGenerationOperations.definePromptTemplate(connectionMock, template, instructions, dataset, paramDetailsMock,
                                                      readTimeout,
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
        .when(requestHelperMock).executeGenerateText(anyString(), any(), eq(readTimeout), any());

    einsteinGenerationOperations.generateText(connectionMock, prompt, paramDetailsMock, readTimeout, callbackMock);

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
                                                          anyString(), any(), eq(readTimeout), any());

    einsteinGenerationOperations.generateChatFromMessages(connectionMock, messages, paramDetailsMock, readTimeout, callbackMock2);

    verify(callbackMock2).error(argThat(exception -> exception instanceof ModuleException &&
        exception.getMessage().equals("Error while generating the chat from messages Test Messages") &&
        ((ModuleException) exception).getType() == CHAT_FAILURE));
  }

  @Test
  void testPromptTemplateFailure() {
    String promptTemplateDevName = "einstein_gpt__summarizeAccountDefault";
    PromptParamsDetails promptParamsDetails = new PromptParamsDetails();
    EinsteinLlmAdditionalConfigInputRepresentation einsteinAdditionConfig = new EinsteinLlmAdditionalConfigInputRepresentation();
    Map<String, WrappedValue> promptInputParams = new HashMap<>();
    WrappedValue wrappedValue = new WrappedValue();
    Map<String, String> value = new HashMap<>();
    value.put("Id", "");
    wrappedValue.setValue(value);
    promptInputParams.put("Input:Account", wrappedValue);

    when(connectionMock.getRequestHelper()).thenReturn(requestHelperMock);

    doThrow(new RuntimeException("Test exception"))
        .when(requestHelperMock).generateChatFromMessages(
                                                          anyString(), any(), eq(readTimeout), any());

    einsteinGenerationOperations.promptTemplateGenerations(connectionMock, promptTemplateDevName, promptInputParams,
                                                           promptParamsDetails, einsteinAdditionConfig,
                                                           readTimeout, callbackMock3);

    verify(callbackMock3).error(argThat(exception -> exception instanceof ModuleException &&
        exception.getMessage().equals("Error while generating prompt template generations") &&
        ((ModuleException) exception).getType() == PROMPT_TEMPLATE_GENERATIONS));
  }
}

