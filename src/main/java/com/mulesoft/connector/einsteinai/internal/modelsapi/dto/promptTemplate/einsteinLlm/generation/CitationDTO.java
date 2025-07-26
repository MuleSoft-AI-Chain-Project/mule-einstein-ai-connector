package com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.einsteinLlm.generation;

import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.einsteinLlm.GenAISourceReferenceDTO;

import java.util.List;

public class CitationDTO {

  private List<GenAICitedReferenceDTO> citedReferences;
  private List<GenAISourceReferenceDTO> sourceReferences;

  public List<GenAICitedReferenceDTO> getCitedReferences() {
    return citedReferences;
  }

  public List<GenAISourceReferenceDTO> getSourceReferences() {
    return sourceReferences;
  }
}
