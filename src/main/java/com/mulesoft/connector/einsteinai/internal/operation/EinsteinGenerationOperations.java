package com.mulesoft.connector.einsteinai.internal.operation;

import com.mulesoft.connector.einsteinai.api.metadata.EinsteinResponseAttributes;
import com.mulesoft.connector.einsteinai.api.metadata.ResponseParameters;
import com.mulesoft.connector.einsteinai.internal.connection.EinsteinConnection;
import com.mulesoft.connector.einsteinai.internal.error.provider.ChatErrorTypeProvider;
import com.mulesoft.connector.einsteinai.internal.modelsapi.helpers.PromptTemplateHelper;
import com.mulesoft.connector.einsteinai.internal.modelsapi.models.ParamsModelDetails;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.fixed.OutputJsonType;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static com.mulesoft.connector.einsteinai.internal.error.EinsteinErrorType.CHAT_FAILURE;
import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;

/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class EinsteinGenerationOperations {

  private static final Logger log = LoggerFactory.getLogger(EinsteinGenerationOperations.class);

  /**
   * Helps in defining an AI Agent with a prompt template
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("AGENT-define-prompt-template")
  @Throws(ChatErrorTypeProvider.class)
  @OutputJsonType(schema = "api/response/EinsteinOperationResponse.json")
  public void definePromptTemplate(@Connection EinsteinConnection connection,
                                   @Content(primary = true) String template,
                                   @Content String instructions,
                                   @Content String dataset,
                                   @ParameterGroup(
                                       name = "Additional properties") ParamsModelDetails paramDetails,
                                   CompletionCallback<InputStream, EinsteinResponseAttributes> callback) {
    log.info("Executing agent defined prompt template operation.");
    try {
      String finalPromptTemplate = PromptTemplateHelper.definePromptTemplate(template, instructions, dataset);
      connection.getRequestHelper().executeGenerateText(finalPromptTemplate, paramDetails, callback);
    } catch (Exception e) {
      callback.error(new ModuleException("Error while generating prompt from template " + template + ", instructions "
          + instructions + ", dataset " + dataset, CHAT_FAILURE, e));
    }
  }

  /**
   * Generate a response based on the prompt provided.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("CHAT-answer-prompt")
  @Throws(ChatErrorTypeProvider.class)
  @OutputJsonType(schema = "api/response/EinsteinOperationResponse.json")
  public void generateText(@Connection EinsteinConnection connection,
                           @Content String prompt,
                           @ParameterGroup(
                               name = "Additional properties") ParamsModelDetails paramDetails,
                           CompletionCallback<InputStream, EinsteinResponseAttributes> callback) {
    log.info("Executing chat answer prompt operation.");
    try {
      connection.getRequestHelper().executeGenerateText(prompt, paramDetails, callback);
    } catch (Exception e) {
      callback.error(new ModuleException("Error while generating text for prompt " + prompt, CHAT_FAILURE, e));
    }
  }

  /**
   * Generate a response based on a list of messages representing a chat conversation.
   */
  @MediaType(value = APPLICATION_JSON, strict = false)
  @Alias("CHAT-generate-from-messages")
  @Throws(ChatErrorTypeProvider.class)
  @OutputJsonType(schema = "api/response/EinsteinChatFromMessagesResponse.json")
  public void generateChatFromMessages(@Connection EinsteinConnection connection,
                                       @Content String messages,
                                       @ParameterGroup(
                                           name = "Additional properties") ParamsModelDetails paramDetails,
                                       CompletionCallback<InputStream, ResponseParameters> callback) {
    log.info("Executing chat generate from message operation.");
    try {
      connection.getRequestHelper().generateChatFromMessages(messages, paramDetails, callback);
    } catch (Exception e) {
      callback.error(new ModuleException("Error while generating the chat from messages " + messages, CHAT_FAILURE, e));
    }
  }
}
