package com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.einsteinLlm.generation;

public class SafetyScoreDTO {

  private Double hateScore;
  private Double physicalScore;
  private Double profanityScore;
  private Double safetyScore;
  private Double sexualScore;
  private Double toxicityScore;
  private Double violenceScore;

  public Double getHateScore() {
    return hateScore;
  }

  public Double getPhysicalScore() {
    return physicalScore;
  }

  public Double getProfanityScore() {
    return profanityScore;
  }

  public Double getSafetyScore() {
    return safetyScore;
  }

  public Double getSexualScore() {
    return sexualScore;
  }

  public Double getToxicityScore() {
    return toxicityScore;
  }

  public Double getViolenceScore() {
    return violenceScore;
  }
}
