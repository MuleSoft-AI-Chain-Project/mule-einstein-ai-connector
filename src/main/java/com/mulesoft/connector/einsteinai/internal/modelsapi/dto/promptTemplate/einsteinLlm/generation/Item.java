package com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.einsteinLlm.generation;

public class Item {

  private ContentQualityRepresentationDTO contentQualityRepresentation;
  private String parameters;
  private String responseId;
  private SafetyScoreDTO safetyScoreRepresentation;
  private String text;

  public ContentQualityRepresentationDTO getContentQualityRepresentation() {
    return contentQualityRepresentation;
  }

  public String getParameters() {
    return parameters;
  }

  public String getResponseId() {
    return responseId;
  }

  public SafetyScoreDTO getSafetyScoreRepresentation() {
    return safetyScoreRepresentation;
  }

  public String getText() {
    return text;
  }
}
