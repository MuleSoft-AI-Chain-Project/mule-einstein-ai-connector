package com.mulesoft.connector.einsteinai.api.metadata;

import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.WrappedMapDTO;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.MaskContentRepresentationDTO;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.MaskDataRepresentationDTO;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.einsteinLlm.generation.CitationDTO;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class EinsteinPromptTemplateGenerationsResponseAttributes implements Serializable {

  private final String requestId;
  private final Boolean isSummarized;
  private final WrappedMapDTO parameters;
  private final String prompt;
  private final String promptTemplateDevName;
  private final CitationDTO citations;
  private final List<MaskContentRepresentationDTO> requestMessages;
  private final List<MaskContentRepresentationDTO> responseMessages;
  private final List<MaskDataRepresentationDTO> slotsMaskingInformation;

  public EinsteinPromptTemplateGenerationsResponseAttributes(String requestId, Boolean isSummarized, WrappedMapDTO parameters,
                                                             String prompt, String promptTemplateDevName, CitationDTO citations,
                                                             List<MaskContentRepresentationDTO> requestMessages,
                                                             List<MaskContentRepresentationDTO> responseMessages,
                                                             List<MaskDataRepresentationDTO> slotsMaskingInformation) {
    this.requestId = requestId;
    this.isSummarized = isSummarized;
    System.out.println(this.isSummarized);
    this.parameters = parameters;
    this.prompt = prompt;
    this.promptTemplateDevName = promptTemplateDevName;
    this.citations = citations;
    this.requestMessages = requestMessages;
    this.responseMessages = responseMessages;
    this.slotsMaskingInformation = slotsMaskingInformation;
    System.out.println(this.slotsMaskingInformation.get(0));
  }

  public String getRequestId() {
    return requestId;
  }

  public Boolean isSummarized() {
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

  public CitationDTO getCitations() {
    return citations;
  }

  public List<MaskContentRepresentationDTO> getRequestMessages() {
    return requestMessages;
  }

  public List<MaskContentRepresentationDTO> getResponseMessages() {
    return responseMessages;
  }

  public List<MaskDataRepresentationDTO> getSlotsMaskingInformation() {
    return slotsMaskingInformation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    EinsteinPromptTemplateGenerationsResponseAttributes that = (EinsteinPromptTemplateGenerationsResponseAttributes) o;
    return Objects.equals(requestId, that.requestId) && Objects.equals(isSummarized, that.isSummarized)
        && Objects.equals(parameters, that.parameters) && Objects.equals(prompt, that.prompt)
        && Objects.equals(promptTemplateDevName, that.promptTemplateDevName) && Objects.equals(citations, that.citations)
        && Objects.equals(requestMessages, that.requestMessages) && Objects.equals(responseMessages, that.responseMessages)
        && Objects.equals(slotsMaskingInformation, that.slotsMaskingInformation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestId, isSummarized, parameters, prompt, promptTemplateDevName, citations, requestMessages,
                        responseMessages, slotsMaskingInformation);
  }
}
