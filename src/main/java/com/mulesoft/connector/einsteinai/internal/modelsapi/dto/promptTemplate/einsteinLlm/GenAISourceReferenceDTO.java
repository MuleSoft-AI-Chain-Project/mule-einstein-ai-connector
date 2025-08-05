package com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.einsteinLlm;

import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.einsteinLlm.generation.GenAISourceContentInfoDTO;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.einsteinLlm.generation.GenAISourceReferenceInfoDTO;

import java.util.List;

public class GenAISourceReferenceDTO {

  private List<GenAISourceContentInfoDTO> contents;

  private List<GenAISourceReferenceInfoDTO> metadata;

  public List<GenAISourceContentInfoDTO> getContents() {
    return contents;
  }

  public List<GenAISourceReferenceInfoDTO> getMetadata() {
    return metadata;
  }
}
