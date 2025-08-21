package com.mulesoft.connector.einsteinai.internal.modelsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mulesoft.connector.einsteinai.api.metadata.quality.Categories;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Internal DTO for handling Categories data during JSON deserialization. Uses BigDecimal for the score field to preserve full
 * precision of small decimal values like 0.0003, 0.001, 3.0E-4, or 0.0025 without loss.
 * 
 * This DTO is mapped to the public API Categories class to maintain backward compatibility.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoriesDTO {

  private String categoryName;
  private BigDecimal score;

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public BigDecimal getScore() {
    return score;
  }

  public void setScore(BigDecimal score) {
    this.score = score;
  }

  /**
   * Converts this DTO to the public API Categories object. The score is converted to a string representation that preserves
   * precision and uses decimal notation (not scientific).
   */
  public Categories toCategories() {

    return Optional.ofNullable(this.score)
        .map(scr -> new Categories(this.categoryName, scr.toPlainString()))
        .orElse(
                new Categories(this.categoryName, null));
  }
}
