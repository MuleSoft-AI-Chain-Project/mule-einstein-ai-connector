package com.mulesoft.connector.einsteinai.api.metadata.promptTemplate;

import java.util.Objects;

public class MaskContentRepresentation {

  private String content;
  private MaskSettingsRepresentation moderationSettings;
  private String role;

  public MaskContentRepresentation() {}

  public MaskContentRepresentation(String content, MaskSettingsRepresentation moderationSettings, String role) {
    this.content = content;
    this.moderationSettings = moderationSettings;
    this.role = role;
  }

  public String getContent() {
    return content;
  }

  public MaskSettingsRepresentation getModerationSettings() {
    return moderationSettings;
  }

  public String getRole() {
    return role;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    MaskContentRepresentation that = (MaskContentRepresentation) o;
    return Objects.equals(content, that.content) && Objects.equals(moderationSettings, that.moderationSettings)
        && Objects.equals(role, that.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, moderationSettings, role);
  }
}
