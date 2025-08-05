package com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.einsteinLlm.generation;

public class GenAISourceContentInfoDTO {

  private String content;
  private String fieldName;
  private String objectName;

  public String getContent() {
    return content;
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getObjectName() {
    return objectName;
  }
}
