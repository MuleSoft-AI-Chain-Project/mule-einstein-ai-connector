package com.mulesoft.connector.einsteinai.api.metadata.promptTemplate;

import java.util.Objects;

public class MaskSettingsRepresentation {

  private boolean enableModeration;

  public MaskSettingsRepresentation() {}

  public MaskSettingsRepresentation(boolean enableModeration) {
    this.enableModeration = enableModeration;
  }

  public boolean isEnableModeration() {
    return enableModeration;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    MaskSettingsRepresentation that = (MaskSettingsRepresentation) o;
    return enableModeration == that.enableModeration;
  }

  @Override
  public int hashCode() {
    return Objects.hash(enableModeration);
  }
}
