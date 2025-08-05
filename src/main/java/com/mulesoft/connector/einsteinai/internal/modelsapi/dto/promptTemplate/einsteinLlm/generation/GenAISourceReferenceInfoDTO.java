package com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.einsteinLlm.generation;

public class GenAISourceReferenceInfoDTO {

  private String link;
  private String sourceObjectApiName;
  private String sourceObjectRecordId;

  public String getLink() {
    return link;
  }

  public String getSourceObjectApiName() {
    return sourceObjectApiName;
  }

  public String getSourceObjectRecordId() {
    return sourceObjectRecordId;
  }
}
