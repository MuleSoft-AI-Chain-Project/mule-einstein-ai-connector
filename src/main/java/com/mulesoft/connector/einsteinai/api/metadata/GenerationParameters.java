package com.mulesoft.connector.einsteinai.api.metadata;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GenerationParameters implements Serializable {

  private final String finishReason;
  private final String refusal;
  private final int index;
  private final String logprobs;
  private final List<String> annotations;

  public GenerationParameters(String finishReason, String refusal, int index, String logprobs) {
    this(finishReason, refusal, index, logprobs, null);
  }

  @ConstructorProperties({"finishReason", "refusal", "index", "logprobs", "annotations"})
  public GenerationParameters(String finishReason, String refusal, int index, String logprobs, List<String> annotations) {
    this.finishReason = finishReason;
    this.refusal = refusal;
    this.index = index;
    this.logprobs = logprobs;
    this.annotations = annotations;
  }

  public String getFinishReason() {
    return finishReason;
  }

  public String getRefusal() {
    return refusal;
  }

  public int getIndex() {
    return index;
  }

  public String getLogprobs() {
    return logprobs;
  }

  public List<String> getAnnotations() { // Getter for annotations
    return annotations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof GenerationParameters))
      return false;
    GenerationParameters that = (GenerationParameters) o;
    return getIndex() == that.getIndex() && Objects.equals(getFinishReason(), that.getFinishReason())
        && Objects.equals(getRefusal(), that.getRefusal()) && Objects.equals(getLogprobs(), that.getLogprobs())
        && Objects.equals(getAnnotations(), that.getAnnotations()); // Include annotations in equals check
  }

  @Override
  public int hashCode() {
    return Objects.hash(getFinishReason(), getRefusal(), getIndex(), getLogprobs(), getAnnotations()); // Include annotations in
                                                                                                       // hashCode
  }
}
