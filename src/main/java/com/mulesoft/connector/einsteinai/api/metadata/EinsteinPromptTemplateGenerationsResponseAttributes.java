package com.mulesoft.connector.einsteinai.api.metadata;

import com.mulesoft.connector.einsteinai.api.metadata.promptTemplate.MaskContentRepresentation;
import com.mulesoft.connector.einsteinai.api.metadata.promptTemplate.MaskDataRepresentation;
import com.mulesoft.connector.einsteinai.api.metadata.promptTemplate.einsteinLlm.generation.Citation;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class EinsteinPromptTemplateGenerationsResponseAttributes implements Serializable {

  private final String requestId;
  private final Boolean isSummarized;
  private final WrappedMap parameters;
  private final String prompt;
  private final String promptTemplateDevName;
  private final Citation citations;
  private final List<MaskContentRepresentation> requestMessages;
  private final List<MaskContentRepresentation> responseMessages;
  private final List<MaskDataRepresentation> slotsMaskingInformation;

  public EinsteinPromptTemplateGenerationsResponseAttributes(String requestId, Boolean isSummarized, WrappedMap parameters,
                                                             String prompt, String promptTemplateDevName, Citation citations,
                                                             List<MaskContentRepresentation> requestMessages,
                                                             List<MaskContentRepresentation> responseMessages,
                                                             List<MaskDataRepresentation> slotsMaskingInformation) {
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

  public WrappedMap getParameters() {
    return parameters;
  }

  public String getPrompt() {
    return prompt;
  }

  public String getPromptTemplateDevName() {
    return promptTemplateDevName;
  }

  public Citation getCitations() {
    return citations;
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
