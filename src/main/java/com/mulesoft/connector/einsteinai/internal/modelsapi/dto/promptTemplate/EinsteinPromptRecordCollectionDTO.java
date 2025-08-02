package com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate;

import java.util.List;

public class EinsteinPromptRecordCollectionDTO {

  private boolean hasMoreRecords;
  private List<EinsteinPromptRecordDTO> promptRecords;
  private int totalPromptRecords;

  public boolean isHasMoreRecords() {
    return hasMoreRecords;
  }

  public List<EinsteinPromptRecordDTO> getPromptRecords() {
    return promptRecords;
  }

  public int getTotalPromptRecords() {
    return totalPromptRecords;
  }
}
