package com.mulesoft.connector.einsteinai.internal.error;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.mule.runtime.extension.api.error.MuleErrors;

import java.util.Optional;

public enum EinsteinErrorType implements ErrorTypeDefinition<EinsteinErrorType> {

  CHAT_FAILURE(MuleErrors.ANY), EMBEDDING_OPERATIONS_FAILURE(MuleErrors.ANY), RAG_FAILURE(
      MuleErrors.ANY), TOOLS_OPERATION_FAILURE(MuleErrors.ANY), MODELS_API_ERROR(
          MuleErrors.ANY), INVALID_CONNECTION(MuleErrors.CONNECTIVITY), PROMPT_TEMPLATE_GENERATIONS(MuleErrors.ANY);

  private final ErrorTypeDefinition<? extends Enum<?>> parent;

  @Override
  public Optional<ErrorTypeDefinition<? extends Enum<?>>> getParent() {
    return Optional.of(parent);
  }

  EinsteinErrorType(ErrorTypeDefinition<? extends Enum<?>> parent) {
    this.parent = parent;
  }
}
