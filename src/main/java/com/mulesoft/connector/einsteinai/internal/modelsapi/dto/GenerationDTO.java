package com.mulesoft.connector.einsteinai.internal.modelsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mulesoft.connector.einsteinai.api.metadata.GenerationParameters;
import com.mulesoft.connector.einsteinai.api.metadata.quality.ContentQuality;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerationDTO {

  private String id;
  private String generatedText;
  private ContentQualityDTO contentQuality;
  private GenerationParameters parameters;

  public String getId() {
    return id;
  }

  public String getGeneratedText() {
    return generatedText;
  }

  public ContentQuality getContentQuality() {
    return contentQuality != null ? contentQuality.toContentQuality() : null;
  }

  public ContentQualityDTO getContentQualityDTO() {
    return contentQuality;
  }

  public GenerationParameters getParameters() {
    return parameters;
  }
}
