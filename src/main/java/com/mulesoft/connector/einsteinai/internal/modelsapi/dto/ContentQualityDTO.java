package com.mulesoft.connector.einsteinai.internal.modelsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mulesoft.connector.einsteinai.api.metadata.quality.ContentQuality;

import java.util.Optional;

/**
 * Internal DTO for handling ContentQuality data during JSON deserialization. Uses ScanToxicityDTO to preserve precision of
 * toxicity scores.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentQualityDTO {

  private ScanToxicityDTO scanToxicity;

  public ScanToxicityDTO getScanToxicity() {
    return scanToxicity;
  }

  public void setScanToxicity(ScanToxicityDTO scanToxicity) {
    this.scanToxicity = scanToxicity;
  }

  /**
   * Converts this DTO to the public API ContentQuality object.
   */
  public ContentQuality toContentQuality() {

    return Optional.ofNullable(this.scanToxicity)
        .map(scanToxicityDTO -> new ContentQuality(scanToxicityDTO.toScanToxicity()))
        .orElse(new ContentQuality(null));
  }
}
