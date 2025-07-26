package com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.WrappedMapDTO;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.einsteinLlm.generation.CitationDTO;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.einsteinLlm.generation.Item;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EinsteinPromptTemplateGenerationsResponseDTO {

  private CitationDTO citations;
  private List<AttachmentDTO> fileData;
  private List<GenerationsErrorDTO> generationErrors;
  private List<Item> generations;
  private Boolean isSummarized;
  private WrappedMapDTO parameters;
  private String prompt;
  private String promptTemplateDevName;
  private String requestId;
  private MaskContentRepresentationDTO requestMessages;
  private MaskContentRepresentationDTO responseMessages;
  private MaskDataRepresentationDTO slotsMaskingInformation;

  public CitationDTO getCitations() {
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

  public Boolean getSummarized() {
    return isSummarized;
  }

  public WrappedMapDTO getParameters() {
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

  public MaskContentRepresentationDTO getRequestMessages() {
    return requestMessages;
  }

  public MaskContentRepresentationDTO getResponseMessages() {
    return responseMessages;
  }

  public MaskDataRepresentationDTO getSlotsMaskingInformation() {
    return slotsMaskingInformation;
  }
}
