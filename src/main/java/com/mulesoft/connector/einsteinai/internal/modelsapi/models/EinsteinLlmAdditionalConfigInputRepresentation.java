package com.mulesoft.connector.einsteinai.internal.modelsapi.models;

import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import java.util.List;
import java.util.Map;

public class EinsteinLlmAdditionalConfigInputRepresentation {

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional
  @DisplayName("Additional Parameters")
  @Placement(tab = Placement.ADVANCED_TAB)
  private Map<String, WrappedValue> additionalParameters;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional(defaultValue = "PromptTemplateGenerationsInvocable")
  @DisplayName("Application Name")
  @Placement(tab = Placement.ADVANCED_TAB)
  private String applicationName;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional
  @DisplayName("Enable Pii Masking")
  @Placement(tab = Placement.ADVANCED_TAB)
  private boolean enablePiiMasking;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional
  @DisplayName("Frequency Penalty")
  @Placement(tab = Placement.ADVANCED_TAB)
  private Double frequencyPenalty;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional
  @DisplayName("Frequency Penalty")
  @Placement(tab = Placement.ADVANCED_TAB)
  private Integer maxTokens;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional
  @DisplayName("Num Generations")
  @Placement(tab = Placement.ADVANCED_TAB)
  private Integer numGenerations;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional
  @DisplayName("Presence Penalty")
  @Placement(tab = Placement.ADVANCED_TAB)
  private Double presencePenalty;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional
  @DisplayName("Stop Sequences")
  @Placement(tab = Placement.ADVANCED_TAB)
  private List<String> stopSequences;

  @Parameter
  @Expression(ExpressionSupport.SUPPORTED)
  @Optional
  @DisplayName("Temperature")
  @Placement(tab = Placement.ADVANCED_TAB)
  private Double temperature;

  public Map<String, WrappedValue> getAdditionalParameters() {
    return additionalParameters;
  }

  public String getApplicationName() {
    return applicationName;
  }

  public Boolean getEnablePiiMasking() {
    return enablePiiMasking;
  }

  public Double getFrequencyPenalty() {
    return frequencyPenalty;
  }

  public Integer getMaxTokens() {
    return maxTokens;
  }

  public Integer getNumGenerations() {
    return numGenerations;
  }

  public Double getPresencePenalty() {
    return presencePenalty;
  }

  public List<String> getStopSequences() {
    return stopSequences;
  }

  public Double getTemperature() {
    return temperature;
  }
}
