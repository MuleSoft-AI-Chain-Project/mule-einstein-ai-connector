package com.mulesoft.connector.einsteinai.internal.modelsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mulesoft.connector.einsteinai.api.metadata.ResponseParameters;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EinsteinGenerationResponseDTO {

  private String id;
  private GenerationDTO generation;
  private Object moreGenerations;
  private Object prompt;
  private ResponseParameters parameters;

  public String getId() {
    return id;
  }

  public GenerationDTO getGeneration() {
    return generation;
  }

  public Object getMoreGenerations() {
    return moreGenerations;
  }

  public Object getPrompt() {
    return prompt;
  }

  public ResponseParameters getParameters() {
    return parameters;
  }
}
