package com.mulesoft.connector.einsteinai.internal.modelsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mulesoft.connector.einsteinai.api.metadata.quality.ScanToxicity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Internal DTO for handling ScanToxicity data during JSON deserialization. Uses CategoriesDTO to preserve precision of toxicity
 * scores.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScanToxicityDTO {

  private boolean isDetected;
  private List<CategoriesDTO> categories;

  public boolean getIsDetected() {
    return isDetected;
  }

  public void setIsDetected(boolean isDetected) {
    this.isDetected = isDetected;
  }

  public List<CategoriesDTO> getCategories() {
    return categories;
  }

  public void setCategories(List<CategoriesDTO> categories) {
    this.categories = categories;
  }

  /**
   * Converts this DTO to the public API ScanToxicity object.
   */
  public ScanToxicity toScanToxicity() {

    return Optional.ofNullable(this.categories)
        .map(categoriesDTOS -> new ScanToxicity(this.isDetected, categoriesDTOS
            .stream()
            .map(CategoriesDTO::toCategories)
            .collect(Collectors.toList())))
        .orElse(new ScanToxicity(this.isDetected, null));
  }
}
