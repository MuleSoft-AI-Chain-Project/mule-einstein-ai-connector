package com.mulesoft.connector.einsteinai.api.metadata;

import com.mulesoft.connector.einsteinai.api.metadata.quality.ContentQuality;

import java.io.Serializable;
import java.util.Objects;

public class EinsteinResponseAttributes implements Serializable {

  private final String responseId;
  private final String generationId;
  private final ContentQuality contentQuality;
  private final GenerationParameters generationParameters;
  private final ResponseParameters responseParameters;

  public EinsteinResponseAttributes(String responseId, String generationId, ContentQuality contentQuality,
                                    GenerationParameters generationParameters, ResponseParameters responseParameters) {
    this.responseId = responseId;
    this.generationId = generationId;
    this.contentQuality = contentQuality;
    this.generationParameters = generationParameters;
    this.responseParameters = responseParameters;
  }

  public String getResponseId() {
    return responseId;
  }

  public String getGenerationId() {
    return generationId;
  }

  public ContentQuality getContentQuality() {
    return contentQuality;
  }

  public GenerationParameters getGenerationParameters() {
    return generationParameters;
  }

  public ResponseParameters getResponseParameters() {
    return responseParameters;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    EinsteinResponseAttributes that = (EinsteinResponseAttributes) o;
    return Objects.equals(responseId, that.responseId) && Objects.equals(generationId, that.generationId)
        && Objects.equals(contentQuality, that.contentQuality) && Objects.equals(generationParameters, that.generationParameters)
        && Objects.equals(responseParameters, that.responseParameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(responseId, generationId, contentQuality, generationParameters, responseParameters);
  }
}
