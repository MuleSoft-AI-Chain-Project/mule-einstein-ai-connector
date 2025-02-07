package com.mulesoft.connector.einsteinai.internal.modelsapi.models.provider;

import org.mule.runtime.api.value.Value;
import org.mule.runtime.extension.api.values.ValueBuilder;
import org.mule.runtime.extension.api.values.ValueProvider;
import org.mule.runtime.extension.api.values.ValueResolvingException;

import java.util.Set;

public class EmbeddingModelApiNameProvider implements ValueProvider {

  private static final Set<Value> EMBEDDING_MODEL_API_VALUES = ValueBuilder.getValuesFor(
                                                                                         "sfdc_ai__DefaultAzureOpenAITextEmbeddingAda_002",
                                                                                         "sfdc_ai__DefaultOpenAITextEmbeddingAda_002");

  @Override
  public Set<Value> resolve() throws ValueResolvingException {

    return EMBEDDING_MODEL_API_VALUES;
  }

}
