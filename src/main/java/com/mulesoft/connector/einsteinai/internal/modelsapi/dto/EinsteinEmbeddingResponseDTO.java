package com.mulesoft.connector.einsteinai.internal.modelsapi.dto;

import com.mulesoft.connector.einsteinai.api.metadata.ResponseParameters;

import java.util.List;

public class EinsteinEmbeddingResponseDTO {

  private List<EinsteinEmbeddingDTO> embeddings;
  private ResponseParameters parameters;

  public List<EinsteinEmbeddingDTO> getEmbeddings() {
    return embeddings;
  }

  public ResponseParameters getParameters() {
    return parameters;
  }
}
