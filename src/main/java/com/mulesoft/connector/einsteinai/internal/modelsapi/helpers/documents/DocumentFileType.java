package com.mulesoft.connector.einsteinai.internal.modelsapi.helpers.documents;

import org.mule.runtime.api.value.Value;
import org.mule.runtime.extension.api.values.ValueBuilder;
import org.mule.runtime.extension.api.values.ValueProvider;
import org.mule.runtime.extension.api.values.ValueResolvingException;

import java.util.Set;

public class DocumentFileType implements ValueProvider {

  @Override
  public Set<Value> resolve() throws ValueResolvingException {
    return ValueBuilder.getValuesFor("PDF", "TEXT", "CSV");
  }

}
