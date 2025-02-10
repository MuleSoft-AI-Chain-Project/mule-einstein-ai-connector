package com.mulesoft.connector.einsteinai.internal.modelsapi.helpers.chatmemory;

import com.mulesoft.connector.einsteinai.api.metadata.EinsteinResponseAttributes;
import com.mulesoft.connector.einsteinai.internal.modelsapi.helpers.RequestHelper;
import com.mulesoft.connector.einsteinai.internal.modelsapi.models.ParamsModelDetails;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ChatMemoryHelper {

  private final RequestHelper requestHelper;

  public ChatMemoryHelper(RequestHelper requestHelper) {
    this.requestHelper = requestHelper;
  }

  public void chatWithMemory(String prompt, String memoryPath, String memoryName, Integer keepLastMessages,
                             ParamsModelDetails parameters,
                             CompletionCallback<InputStream, EinsteinResponseAttributes> callback) {

    // Chat memory initialization
    ChatMemoryUtil chatMemory = intializeChatMemory(memoryPath, memoryName);

    // Get last Messages To Keep
    List<String> lastMessages = getLastMessagesToKeep(chatMemory, keepLastMessages);
    lastMessages.add(prompt);
    String memoryPrompt = formatMemoryPrompt(lastMessages);

    // Execute async text generation
    requestHelper.executeGenerateText(memoryPrompt, parameters,
                                      new CompletionCallback<InputStream, EinsteinResponseAttributes>() {

                                        @Override
                                        public void success(Result<InputStream, EinsteinResponseAttributes> result) {
                                          // Add message to memory only after a successful response
                                          addMessageToMemory(chatMemory, prompt);
                                          callback.success(result);
                                        }

                                        @Override
                                        public void error(Throwable throwable) {
                                          callback.error(throwable);
                                        }
                                      });
  }

  private ChatMemoryUtil intializeChatMemory(String memoryPath, String memoryName) {
    return new ChatMemoryUtil(memoryPath, memoryName);
  }

  private List<String> getLastMessagesToKeep(ChatMemoryUtil chatMemory, Integer keepLastMessages) {

    // Retrieve all messages in ascending order of messageId
    List<String> messages = chatMemory.getAllMessagesByMessageIdAsc();

    // Keep only the last index messages
    if (messages.size() > keepLastMessages) {
      messages = messages.subList(messages.size() - keepLastMessages, messages.size());
    }
    return messages;
  }

  private void addMessageToMemory(ChatMemoryUtil chatMemory, String prompt) {
    if (!isQuestion(prompt)) {
      chatMemory.addMessage(chatMemory.getMessageCount() + 1L, prompt);
    }
  }

  private boolean isQuestion(String message) {
    // Check if the message ends with a question mark
    if (message.trim().endsWith("?")) {
      return true;
    }
    // Check if the message starts with a question word (case-insensitive)
    String[] questionWords = {"who", "what", "when", "where", "why", "how", "tell", "tell me", "do you",};
    String lowerCaseMessage = message.trim().toLowerCase();
    for (String questionWord : questionWords) {
      if (lowerCaseMessage.startsWith(questionWord + " ")) {
        return true;
      }
    }
    return false;
  }

  private String formatMemoryPrompt(List<String> messages) {
    StringBuilder formattedPrompt = new StringBuilder();
    for (String message : messages) {
      formattedPrompt.append(message).append("\n");
    }
    return formattedPrompt.toString().trim();
  }
}
