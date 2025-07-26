package com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate;

public class MaskContentRepresentationDTO {

  private String content;
  private MaskSettingsRepresentationDTO moderationSettings;
  private String role;

  public String getContent() {
    return content;
  }

  public MaskSettingsRepresentationDTO getModerationSettings() {
    return moderationSettings;
  }

  public String getRole() {
    return role;
  }
}
