package com.mulesoft.connector.einsteinai.internal.modelsapi.dto;

import java.beans.ConstructorProperties;
import java.util.List;

public class EinsteinEmbeddingDTO {

  private final int index;
  private final List<Double> embeddings;

  @ConstructorProperties({"index", "embedding"})
  public EinsteinEmbeddingDTO(int index, List<Double> embeddings) {
    this.index = index;
    this.embeddings = embeddings;
  }

  public int getIndex() {
    return index;
  }

  public List<Double> getEmbeddings() {
    return embeddings;
  }
}
