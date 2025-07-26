package com.mulesoft.connector.einsteinai.internal.modelsapi.models;

import com.mulesoft.connector.einsteinai.internal.modelsapi.models.provider.CitationApiNameProvider;
import org.json.JSONPropertyName;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.values.OfValues;
import java.util.Map;

public class PromptParamsDetails {

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional(defaultValue = "off")
  @DisplayName("Citation Mode")
  @Placement(tab = Placement.ADVANCED_TAB)
  @OfValues(CitationApiNameProvider.class)
  private String citationMode;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional(defaultValue = "false")
  @DisplayName("Is Preview?")
  @Placement(tab = Placement.ADVANCED_TAB)
  private boolean isPreview;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional
  @DisplayName("Output Language")
  @Placement(tab = Placement.ADVANCED_TAB)
  private String outputLanguage;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional
  @DisplayName("Tags")
  @Placement(tab = Placement.ADVANCED_TAB)
  private Map<String, WrappedValue> tags;

  public String getCitationMode() {
    return citationMode;
  }

  @JSONPropertyName("isPreview")
  public Boolean getPreview() {
    return isPreview;
  }

  public String getOutputLanguage() {
    return outputLanguage;
  }

  public Map<String, WrappedValue> getTags() {
    return tags;
  }
}
