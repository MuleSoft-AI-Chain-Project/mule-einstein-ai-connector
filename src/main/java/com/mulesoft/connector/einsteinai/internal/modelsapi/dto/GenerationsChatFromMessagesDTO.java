package com.mulesoft.connector.einsteinai.internal.modelsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mulesoft.connector.einsteinai.api.metadata.GenerationParameters;
import com.mulesoft.connector.einsteinai.api.metadata.quality.ContentQuality;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerationsChatFromMessagesDTO {

  private String id;
  private String role;
  private String content;
  private ContentQualityDTO contentQuality;
  private GenerationParameters parameters;

  public String getId() {
    return id;
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

  public String getRole() {
    return role;
  }

  public String getContent() {
    return content;
  }
}
