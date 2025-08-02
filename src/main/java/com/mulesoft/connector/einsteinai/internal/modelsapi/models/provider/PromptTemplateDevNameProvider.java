package com.mulesoft.connector.einsteinai.internal.modelsapi.models.provider;

import com.mulesoft.connector.einsteinai.internal.config.EinsteinConfiguration;
import com.mulesoft.connector.einsteinai.internal.connection.EinsteinConnection;
import com.mulesoft.connector.einsteinai.internal.params.ReadTimeoutParams;
import org.mule.runtime.api.value.Value;
import org.mule.runtime.extension.api.values.ValueBuilder;
import org.mule.runtime.extension.api.values.ValueProvider;
import org.mule.runtime.extension.api.values.ValueResolvingException;
import org.mule.sdk.api.annotation.param.Config;
import org.mule.sdk.api.annotation.param.Connection;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class PromptTemplateDevNameProvider implements ValueProvider {

  @Connection
  EinsteinConnection einsteinConnection;

  @Config
  EinsteinConfiguration einsteinConfiguration;

  @Override
  public Set<Value> resolve() throws ValueResolvingException {
    ReadTimeoutParams readTimeoutParams =
        new ReadTimeoutParams(einsteinConfiguration.getReadTimeout(), einsteinConfiguration.getReadTimeoutUnit());
    List<String> promptTemplates;
    try {
      promptTemplates = this.einsteinConnection.getRequestHelper().getPromptTemplates(readTimeoutParams);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (TimeoutException e) {
      throw new RuntimeException(e);
    }

    return ValueBuilder.getValuesFor(promptTemplates);
  }
}
