package com.mulesoft.connector.einsteinai.internal.error.provider;

import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.mulesoft.connector.einsteinai.internal.error.EinsteinErrorType.*;
import static java.util.Collections.unmodifiableSet;

public class PromptTemplateErrorTypeProvider implements ErrorTypeProvider {

  @SuppressWarnings("rawtypes")
  @Override
  public Set<ErrorTypeDefinition> getErrorTypes() {
    return unmodifiableSet(new HashSet<>(Arrays.asList(PROMPT_TEMPLATE_GENERATIONS, MODELS_API_ERROR)));
  }
}
