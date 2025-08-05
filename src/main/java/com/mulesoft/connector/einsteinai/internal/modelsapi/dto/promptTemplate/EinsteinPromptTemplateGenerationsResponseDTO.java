package com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mulesoft.connector.einsteinai.api.metadata.WrappedMap;
import com.mulesoft.connector.einsteinai.api.metadata.promptTemplate.MaskContentRepresentation;
import com.mulesoft.connector.einsteinai.api.metadata.promptTemplate.MaskDataRepresentation;
import com.mulesoft.connector.einsteinai.api.metadata.promptTemplate.einsteinLlm.generation.Citation;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.einsteinLlm.generation.Item;
import org.json.JSONPropertyName;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EinsteinPromptTemplateGenerationsResponseDTO {

  private Citation citations;
  private List<AttachmentDTO> fileData;
  private List<GenerationsErrorDTO> generationErrors;
  private List<Item> generations;
  private Boolean isSummarized;
  private WrappedMap parameters;
  private String prompt;
  private String promptTemplateDevName;
  private String requestId;
  private List<MaskContentRepresentation> requestMessages;
  private List<MaskContentRepresentation> responseMessages;
  private List<MaskDataRepresentation> slotsMaskingInformation;

  public Citation getCitations() {
    return citations;
  }

  public List<AttachmentDTO> getFileData() {
    return fileData;
  }

  public List<GenerationsErrorDTO> getGenerationErrors() {
    return generationErrors;
  }

  public List<Item> getGenerations() {
    return generations;
  }

  @JSONPropertyName("isSummarized")
  public Boolean getSummarized() {
    return isSummarized;
  }

  public WrappedMap getParameters() {
    return parameters;
  }

  public String getPrompt() {
    return prompt;
  }

  public String getPromptTemplateDevName() {
    return promptTemplateDevName;
  }

  public String getRequestId() {
    return requestId;
  }

  public List<MaskContentRepresentation> getRequestMessages() {
    return requestMessages;
  }

  public List<MaskContentRepresentation> getResponseMessages() {
    return responseMessages;
  }

  public List<MaskDataRepresentation> getSlotsMaskingInformation() {
    return slotsMaskingInformation;
  }
}
