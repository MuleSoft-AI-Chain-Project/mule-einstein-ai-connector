package com.mulesoft.connector.einsteinai.internal.modelsapi.dto;

public class EinsteinChatFromMessagesResponseDTO {

  private String id;
  private GenerationDetailsDTO generationDetails;

  public String getId() {
    return id;
  }

  public GenerationDetailsDTO getGenerationDetails() {
    return generationDetails;
  }

}
