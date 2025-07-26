package com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate;

public class AttachmentDTO {

  private String fileExtension;
  private String id;
  private String latestPublishedVersion;
  private String title;

  public String getFileExtension() {
    return fileExtension;
  }

  public String getId() {
    return id;
  }

  public String getLatestPublishedVersion() {
    return latestPublishedVersion;
  }

  public String getTitle() {
    return title;
  }
}
