package com.mulesoft.connector.einsteinai.api.metadata.promptTemplate.einsteinLlm.generation;

import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.einsteinLlm.GenAISourceReferenceDTO;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.einsteinLlm.generation.GenAICitedReferenceDTO;

import java.util.List;
import java.util.Objects;

public class Citation {

  private List<GenAICitedReferenceDTO> citedReferences;
  private List<GenAISourceReferenceDTO> sourceReferences;

  public Citation() {}

  public Citation(List<GenAICitedReferenceDTO> citedReferences, List<GenAISourceReferenceDTO> sourceReferences) {
    this.citedReferences = citedReferences;
    this.sourceReferences = sourceReferences;
  }

  public List<GenAICitedReferenceDTO> getCitedReferences() {
    return citedReferences;
  }

  public List<GenAISourceReferenceDTO> getSourceReferences() {
    return sourceReferences;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Citation citation = (Citation) o;
    return Objects.equals(citedReferences, citation.citedReferences)
        && Objects.equals(sourceReferences, citation.sourceReferences);
  }

  @Override
  public int hashCode() {
    return Objects.hash(citedReferences, sourceReferences);
  }
}
