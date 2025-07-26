package com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate;

public class GenerationsErrorDTO {

  private String errorMessage;
  private String httpErrorCode;
  private String localizedErrorMessage;
  private String messageCode;

  public String getErrorMessage() {
    return errorMessage;
  }

  public String getHttpErrorCode() {
    return httpErrorCode;
  }

  public String getLocalizedErrorMessage() {
    return localizedErrorMessage;
  }

  public String getMessageCode() {
    return messageCode;
  }
}
