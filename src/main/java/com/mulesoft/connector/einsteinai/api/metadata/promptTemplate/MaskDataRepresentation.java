package com.mulesoft.connector.einsteinai.api.metadata.promptTemplate;

import java.util.List;

public class MaskDataRepresentation {

  private String originalValue;
  private String placeHolder;
  private List<String> recognizers;

  public MaskDataRepresentation() {}

  public MaskDataRepresentation(String originalValue, String placeHolder, List<String> recognizers) {
    this.originalValue = originalValue;
    this.placeHolder = placeHolder;
    this.recognizers = recognizers;
  }

  public String getOriginalValue() {
    return originalValue;
  }

  public String getPlaceHolder() {
    return placeHolder;
  }

  public List<String> getRecognizers() {
    return recognizers;
  }
}
