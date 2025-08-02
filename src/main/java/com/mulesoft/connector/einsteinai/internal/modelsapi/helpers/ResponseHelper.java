package com.mulesoft.connector.einsteinai.internal.modelsapi.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulesoft.connector.einsteinai.api.metadata.EinsteinPromptTemplateGenerationsResponseAttributes;
import com.mulesoft.connector.einsteinai.api.metadata.EinsteinResponseAttributes;
import com.mulesoft.connector.einsteinai.api.metadata.ResponseParameters;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.EinsteinChatFromMessagesResponseDTO;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.EinsteinEmbeddingResponseDTO;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.EinsteinGenerationResponseDTO;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.EinsteinPromptRecordCollectionDTO;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.EinsteinPromptTemplateGenerationsResponseDTO;
import org.json.JSONObject;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.apache.commons.io.IOUtils.toInputStream;

public class ResponseHelper {

  private ResponseHelper() {
    throw new IllegalStateException("Utility class");
  }

  private static final Logger log = LoggerFactory.getLogger(RequestHelper.class);

  private static final ObjectMapper objectMapper = ObjectMapperProvider.create();

  public static Result<InputStream, Void> createEinsteinDefaultResponse(String response) {

    return Result.<InputStream, Void>builder()
        .output(toInputStream(response, StandardCharsets.UTF_8))
        .mediaType(MediaType.APPLICATION_JSON)
        .build();
  }

  public static Result<InputStream, EinsteinResponseAttributes> createEinsteinFormattedResponse(InputStream responseStream)
      throws IOException {

    EinsteinGenerationResponseDTO responseDTO = objectMapper.readValue(responseStream, EinsteinGenerationResponseDTO.class);

    String generatedText =
        responseDTO.getGeneration() != null ? responseDTO.getGeneration().getGeneratedText() : "";

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("response", generatedText);

    return Result.<InputStream, EinsteinResponseAttributes>builder()
        .output(toInputStream(jsonObject.toString(), StandardCharsets.UTF_8))
        .attributes(mapResponseAttributes(responseDTO))
        .attributesMediaType(MediaType.APPLICATION_JAVA)
        .mediaType(MediaType.APPLICATION_JSON)
        .build();
  }

  public static Result<InputStream, ResponseParameters> createEinsteinChatFromMessagesResponse(InputStream responseStream)
      throws IOException {

    EinsteinChatFromMessagesResponseDTO responseDTO =
        objectMapper.readValue(responseStream, EinsteinChatFromMessagesResponseDTO.class);

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("generations", responseDTO.getGenerationDetails().getGenerations());

    return Result.<InputStream, ResponseParameters>builder()
        .output(toInputStream(jsonObject.toString(), StandardCharsets.UTF_8))
        .attributes(responseDTO.getGenerationDetails().getParameters())
        .attributesMediaType(MediaType.APPLICATION_JAVA)
        .mediaType(MediaType.APPLICATION_JSON)
        .build();
  }

  public static Result<InputStream, ResponseParameters> createEinsteinEmbeddingResponse(InputStream response) throws IOException {
    EinsteinEmbeddingResponseDTO responseDTO = objectMapper.readValue(response, EinsteinEmbeddingResponseDTO.class);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("embeddings", responseDTO.getEmbeddings());

    return Result.<InputStream, ResponseParameters>builder()
        .output(toInputStream(jsonObject.toString(), StandardCharsets.UTF_8))
        .attributes(mapEmbeddingResponseAttributes(responseDTO))
        .attributesMediaType(MediaType.APPLICATION_JAVA)
        .mediaType(MediaType.APPLICATION_JSON)
        .build();
  }

  public static Result<InputStream, EinsteinPromptTemplateGenerationsResponseAttributes> createEinsteinPromptTemplateGenerationsResponse(InputStream response)
      throws IOException {
    EinsteinPromptTemplateGenerationsResponseDTO responseDTO =
        objectMapper.readValue(response, EinsteinPromptTemplateGenerationsResponseDTO.class);
    JSONObject jsonObject = new JSONObject(responseDTO);

    return Result.<InputStream, EinsteinPromptTemplateGenerationsResponseAttributes>builder()
        .output(toInputStream(jsonObject.toString(), StandardCharsets.UTF_8))
        .attributes(mapResponseAttributes(responseDTO))
        .attributesMediaType(MediaType.APPLICATION_JAVA)
        .mediaType(MediaType.APPLICATION_JSON)
        .build();
  }

  public static EinsteinPromptRecordCollectionDTO promptTemplates(InputStream response)
      throws IOException {
    EinsteinPromptRecordCollectionDTO responseDTO =
        objectMapper.readValue(response, EinsteinPromptRecordCollectionDTO.class);

    return responseDTO;
  }

  private static EinsteinResponseAttributes mapResponseAttributes(EinsteinGenerationResponseDTO responseDTO) {

    return new EinsteinResponseAttributes(
                                          responseDTO.getId(),
                                          responseDTO.getGeneration() != null ? responseDTO.getGeneration().getId() : null,
                                          responseDTO.getGeneration() != null ? responseDTO.getGeneration().getContentQuality()
                                              : null,
                                          responseDTO.getGeneration() != null ? responseDTO.getGeneration().getParameters()
                                              : null,
                                          responseDTO.getParameters());
  }

  private static EinsteinPromptTemplateGenerationsResponseAttributes mapResponseAttributes(EinsteinPromptTemplateGenerationsResponseDTO responseDTO) {

    return new EinsteinPromptTemplateGenerationsResponseAttributes(
                                                                   responseDTO
                                                                       .getRequestId(),
                                                                   responseDTO.getSummarized(),
                                                                   responseDTO
                                                                       .getParameters(),
                                                                   responseDTO
                                                                       .getPrompt(),
                                                                   responseDTO
                                                                       .getPromptTemplateDevName(),
                                                                   responseDTO
                                                                       .getCitations(),
                                                                   responseDTO
                                                                       .getRequestMessages(),
                                                                   responseDTO
                                                                       .getResponseMessages(),
                                                                   responseDTO
                                                                       .getSlotsMaskingInformation());
  }

  private static ResponseParameters mapEmbeddingResponseAttributes(EinsteinEmbeddingResponseDTO responseDTO) {

    return new ResponseParameters(
                                  responseDTO.getParameters() != null ? responseDTO.getParameters().getTokenUsage() : null,
                                  responseDTO.getParameters() != null ? responseDTO.getParameters().getModel() : null,
                                  null,
                                  responseDTO.getParameters() != null ? responseDTO.getParameters().getObject() : null);
  }
}
