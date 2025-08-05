package com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate;

import java.util.Map;

public class EinsteinPromptRecordDTO {

  private String apiName;
  private Map<String, Object> childRelationships;
  private Map<String, EinsteinPromptRecordFieldDTO> fields;
  private String id;
  private boolean isStandard;

  public String getApiName() {
    return apiName;
  }

  public Map<String, Object> getChildRelationships() {
    return childRelationships;
  }

  public Map<String, EinsteinPromptRecordFieldDTO> getFields() {
    return fields;
  }

  public String getId() {
    return id;
  }

  public boolean isStandard() {
    return isStandard;
  }
}
