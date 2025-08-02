package com.mulesoft.connector.einsteinai.api.metadata.promptTemplate;

import java.util.Objects;

public class MaskSettingsRepresentation {

  private Boolean enableModeration;

  public MaskSettingsRepresentation() {}

  public MaskSettingsRepresentation(Boolean enableModeration) {
    this.enableModeration = enableModeration;
  }

  public Boolean getEnableModeration() {
    return enableModeration;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    MaskSettingsRepresentation that = (MaskSettingsRepresentation) o;
    return Objects.equals(enableModeration, that.enableModeration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(enableModeration);
  }
}
