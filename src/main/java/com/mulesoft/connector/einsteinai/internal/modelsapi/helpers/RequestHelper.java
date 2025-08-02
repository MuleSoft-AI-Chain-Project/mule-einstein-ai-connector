package com.mulesoft.connector.einsteinai.internal.modelsapi.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulesoft.connector.einsteinai.api.metadata.EinsteinPromptTemplateGenerationsResponseAttributes;
import com.mulesoft.connector.einsteinai.api.metadata.EinsteinResponseAttributes;
import com.mulesoft.connector.einsteinai.api.metadata.ResponseParameters;
import com.mulesoft.connector.einsteinai.internal.connection.EinsteinConnection;
import com.mulesoft.connector.einsteinai.internal.error.EinsteinErrorType;
import com.mulesoft.connector.einsteinai.internal.helpers.HttpRequestHelper;
import com.mulesoft.connector.einsteinai.internal.helpers.ThrowingFunction;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.EinsteinEmbeddingResponseDTO;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.promptTemplate.EinsteinPromptRecordCollectionDTO;
import com.mulesoft.connector.einsteinai.internal.modelsapi.models.*;
import com.mulesoft.connector.einsteinai.internal.params.ReadTimeoutParams;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.sax.BodyContentHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.http.api.client.HttpRequestOptions;
import org.mule.runtime.http.api.domain.entity.EmptyHttpEntity;
import org.mule.runtime.http.api.domain.entity.HttpEntity;
import org.mule.runtime.http.api.domain.entity.InputStreamHttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mulesoft.connector.einsteinai.internal.helpers.EinsteinConstantUtil.*;
import static com.mulesoft.connector.einsteinai.internal.helpers.HttpRequestHelper.handleHttpResponse;
import static com.mulesoft.connector.einsteinai.internal.helpers.HttpRequestHelper.handleHttpResponseForTools;
import static com.mulesoft.connector.einsteinai.internal.helpers.HttpRequestHelper.readResponseStream;
import static com.mulesoft.connector.einsteinai.internal.modelsapi.helpers.ConstantUtil.*;

public class RequestHelper {

  private static final Logger log = LoggerFactory.getLogger(RequestHelper.class);
  private static final ObjectMapper objectMapper = ObjectMapperProvider.create();

  private final EinsteinConnection einsteinConnection;

  public RequestHelper(EinsteinConnection einsteinConnection) {
    this.einsteinConnection = einsteinConnection;
  }

  public void executeGenerateText(String prompt, ParamsModelDetails paramDetails,
                                  ReadTimeoutParams readTimeout,
                                  CompletionCallback<InputStream, EinsteinResponseAttributes> callback) {

    String payload = constructPayload(prompt, paramDetails.getLocale(), paramDetails.getProbability());
    InputStreamHttpEntity payloadStream = new InputStreamHttpEntity(new ByteArrayInputStream(payload.getBytes()));

    executeEinsteinRequest(payloadStream, paramDetails.getModelApiName(),
                           HTTP_METHOD_POST, URI_MODELS_API_GENERATIONS, readTimeout, callback,
                           ResponseHelper::createEinsteinFormattedResponse);
  }

  public void generateChatFromMessages(String messages, ParamsModelDetails paramDetails,
                                       ReadTimeoutParams readTimeout,
                                       CompletionCallback<InputStream, ResponseParameters> callback) {

    String payload = constructPayloadWithMessages(messages, paramDetails);
    InputStreamHttpEntity payloadStream = new InputStreamHttpEntity(new ByteArrayInputStream(payload.getBytes()));

    executeEinsteinRequest(payloadStream, paramDetails.getModelApiName(), HTTP_METHOD_POST,
                           URI_MODELS_API_CHAT_GENERATIONS, readTimeout, callback,
                           ResponseHelper::createEinsteinChatFromMessagesResponse);
  }

  public void generateEmbeddingFromText(String text, ParamsEmbeddingModelDetails paramDetails,
                                        ReadTimeoutParams readTimeout,
                                        CompletionCallback<InputStream, ResponseParameters> callback) {
    String payload = constructEmbeddingJsonPayload(text);
    InputStreamHttpEntity payloadStream = new InputStreamHttpEntity(new ByteArrayInputStream(payload.getBytes()));
    executeEinsteinRequest(payloadStream, paramDetails.getModelApiName(), HTTP_METHOD_POST, URI_MODELS_API_EMBEDDINGS,
                           readTimeout, callback,
                           ResponseHelper::createEinsteinEmbeddingResponse);
  }

  public void executePromptTemplateGenerations(Map<String, WrappedValue> promptInputParams, String promptTemplateDevName,
                                               PromptParamsDetails paramPromptDetails,
                                               EinsteinLlmAdditionalConfigInputRepresentation additionalConfigInputRepresentation,
                                               ReadTimeoutParams readTimeout,
                                               CompletionCallback<InputStream, EinsteinPromptTemplateGenerationsResponseAttributes> callback) {

    String payload =
        constructPayloadForPromptTemplate(promptInputParams, paramPromptDetails, additionalConfigInputRepresentation);
    log.debug("Einstein Request Body: {}", payload);
    InputStreamHttpEntity payloadStream = new InputStreamHttpEntity(new ByteArrayInputStream(payload.getBytes()));

    executeEinsteinSalesforceRequest(payloadStream, HTTP_METHOD_POST,
                                     URI_PROMPT_TEMPLATE + promptTemplateDevName + URI_MODELS_API_GENERATIONS, readTimeout,
                                     callback,
                                     ResponseHelper::createEinsteinPromptTemplateGenerationsResponse);
  }

  private <A> void executeEinsteinRequest(InputStreamHttpEntity payload, String modelApiName, String httpMethod,
                                          String uriModelsApiEmbeddings,
                                          ReadTimeoutParams readTimeout,
                                          CompletionCallback<InputStream, A> callback,
                                          ThrowingFunction<InputStream, Result<InputStream, A>> responseConverter) {

    String urlString = einsteinConnection.getApiInstanceUrl() + URI_MODELS_API + modelApiName + uriModelsApiEmbeddings;
    log.debug("Einstein Request URL: {}", urlString);

    HttpRequestOptions httpRequestOptions = HttpRequestOptions.builder()
        .responseTimeout((int) readTimeout.getReadTimeoutUnit().toMillis(readTimeout.getReadTimeout())).build();

    CompletableFuture<HttpResponse> completableFuture = einsteinConnection.getHttpClient().sendAsync(
                                                                                                     buildRequest(urlString,
                                                                                                                  einsteinConnection
                                                                                                                      .getAccessToken(),
                                                                                                                  httpMethod,
                                                                                                                  payload != null
                                                                                                                      ? payload
                                                                                                                      : new EmptyHttpEntity()),
                                                                                                     httpRequestOptions);

    completableFuture
        .whenComplete((response, exception) -> handleHttpResponse(response, exception, EinsteinErrorType.MODELS_API_ERROR,
                                                                  callback, responseConverter));
  }

  private <A> void executeEinsteinSalesforceRequest(InputStreamHttpEntity payload, String httpMethod, String uriEinsteinPath,
                                                    ReadTimeoutParams readTimeout,
                                                    CompletionCallback<InputStream, A> callback,
                                                    ThrowingFunction<InputStream, Result<InputStream, A>> responseConverter) {

    String urlString = einsteinConnection.getInstanceUrl() + "/services/data/v63.0" + URI_EINSTEIN + uriEinsteinPath;
    log.debug("Einstein Request URL: {}", urlString);

    HttpRequestOptions httpRequestOptions = HttpRequestOptions.builder()
        .responseTimeout((int) readTimeout.getReadTimeoutUnit().toMillis(readTimeout.getReadTimeout())).build();

    CompletableFuture<HttpResponse> completableFuture = einsteinConnection.getHttpClient().sendAsync(
                                                                                                     buildRequest(urlString,
                                                                                                                  einsteinConnection
                                                                                                                      .getAccessToken(),
                                                                                                                  httpMethod,
                                                                                                                  payload != null
                                                                                                                      ? payload
                                                                                                                      : new EmptyHttpEntity()),
                                                                                                     httpRequestOptions);

    completableFuture
        .whenComplete((response, exception) -> handleHttpResponse(response, exception,
                                                                  EinsteinErrorType.PROMPT_TEMPLATE_GENERATIONS,
                                                                  callback, responseConverter));
  }

  public JSONArray generateEmbeddingFromFileInputStream(InputStream inputStream,
                                                        ParamsEmbeddingDocumentDetails embeddingDocumentDetails,
                                                        ReadTimeoutParams readTimeout)
      throws IOException, TikaException, SAXException, TimeoutException {
    if (inputStream == null) {
      throw new IllegalArgumentException("Input stream is null.");
    }
    List<List<Double>> allEmbeddings;
    try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
      List<String> corpus = createCorpusListFromStream(bufferedInputStream, embeddingDocumentDetails.getFileType(),
                                                       embeddingDocumentDetails.getOptionType());

      if ("PARAGRAPH".equalsIgnoreCase(embeddingDocumentDetails.getOptionType())) {
        allEmbeddings = getBatchCorpusEmbeddings(embeddingDocumentDetails.getModelApiName(), corpus, readTimeout);
      } else {
        allEmbeddings = getCorpusEmbeddings(embeddingDocumentDetails.getModelApiName(), corpus, readTimeout);
      }
    }
    return new JSONArray(allEmbeddings);
  }

  public InputStream executeRAG(String text, RAGParamsModelDetails paramDetails, ReadTimeoutParams readTimeout)
      throws IOException, TimeoutException {
    String payload = constructPayload(text, paramDetails.getLocale(), paramDetails.getProbability());
    return executeEinsteinRequest(payload, paramDetails.getModelApiName(), URI_MODELS_API_GENERATIONS, readTimeout);
  }

  public InputStream executeTools(String originalPrompt, String prompt, InputStream inputStream, ParamsModelDetails paramDetails,
                                  ReadTimeoutParams readTimeout)
      throws IOException, TimeoutException {
    String payload = constructPayload(prompt, paramDetails.getLocale(), paramDetails.getProbability());
    String payloadOptional = constructPayload(originalPrompt, paramDetails.getLocale(), paramDetails.getProbability());

    String intermediateAnswer =
        readResponseStream(executeEinsteinRequest(payload, paramDetails.getModelApiName(),
                                                  URI_MODELS_API_GENERATIONS, readTimeout));

    List<String> urls = extractUrls(intermediateAnswer);

    if (urls != null) {
      JSONObject jsonObject = new JSONObject(intermediateAnswer);
      String generatedText = jsonObject.getJSONObject("generation").getString("generatedText");

      String ePayload = buildPayload(generatedText);

      String response = getAttributes(urls.get(0), inputStream, extractPayload(ePayload));
      String finalPayload = constructPayload("data: " + response + ", question: " + originalPrompt, paramDetails.getLocale(),
                                             paramDetails.getProbability());
      return executeEinsteinRequest(finalPayload, paramDetails.getModelApiName(), URI_MODELS_API_GENERATIONS, readTimeout);

    } else {
      return executeEinsteinRequest(payloadOptional, paramDetails.getModelApiName(), URI_MODELS_API_GENERATIONS, readTimeout);
    }
  }

  public JSONArray embeddingFileQuery(String prompt, InputStream inputStream, String modelName, String fileType,
                                      String optionType, ReadTimeoutParams readTimeout)
      throws IOException, TikaException, SAXException, TimeoutException {

    String body = constructEmbeddingJsonPayload(prompt);
    List<Double> embeddings = getQueryEmbedding(body, modelName, readTimeout);

    List<String> corpus = createCorpusListFromStream(inputStream, fileType, optionType);
    List<List<Double>> corpusEmbeddings = getCorpusEmbeddings(modelName, corpus, readTimeout);

    // Compare embeddings and rank results
    List<Double> similarityScores = new ArrayList<>();
    corpusEmbeddings
        .forEach(corpusEmbedding -> similarityScores.add(
                                                         calculateCosineSimilarity(embeddings, corpusEmbedding)));
    // Rank and print results
    List<String> results = rankAndPrintResults(corpus, similarityScores);
    // Convert results list to a JSONArray
    return new JSONArray(results);
  }

  public List<String> getPromptTemplates(ReadTimeoutParams readTimeout) throws IOException, TimeoutException {
    String urlString = einsteinConnection.getInstanceUrl() + "/services/data/v63.0" + URI_EINSTEIN + URI_PROMPT_TEMPLATE;
    HttpRequestOptions httpRequestOptions = HttpRequestOptions.builder()
        .responseTimeout((int) readTimeout.getReadTimeoutUnit().toMillis(readTimeout.getReadTimeout())).build();
    List<String> result = new ArrayList<>();
    MultiMap<String, String> queryParams = new MultiMap<>();
    Boolean hasMoreRecords;
    Integer offset = 0;
    queryParams.put("isActive", "true");
    queryParams.put("pageLimit", "50");
    queryParams.put("offset", offset.toString());

    do {
      EinsteinPromptRecordCollectionDTO response;
      HttpResponse httpResponse = this.einsteinConnection.getHttpClient().send(
                                                                               buildRequest(urlString,
                                                                                            einsteinConnection
                                                                                                .getAccessToken(),
                                                                                            HTTP_METHOD_GET,

                                                                                            new EmptyHttpEntity()),
                                                                               httpRequestOptions);

      if (httpResponse.getStatusCode() >= HttpURLConnection.HTTP_OK
          && httpResponse.getStatusCode() < HttpURLConnection.HTTP_MULT_CHOICE) {
        response = ResponseHelper.promptTemplates(httpResponse.getEntity().getContent());
        response.getPromptRecords()
            .stream()
            .forEach(einsteinPromptRecordDTO -> result
                .add((String) einsteinPromptRecordDTO.getFields().get("DeveloperName").getValue()));
        hasMoreRecords = response.isHasMoreRecords();
        if (hasMoreRecords) {
          offset += 50;
          queryParams.replace("offset", offset.toString());
          log.debug("The request has more records, offset: {}", offset);
        }
      } else {
        String errorMessage = HttpRequestHelper.readErrorStream(httpResponse.getEntity().getContent());
        log.error("Error in HTTP request. Response code: {}, message: {}", httpResponse.getStatusCode(), errorMessage);
        break;
      }
    } while (hasMoreRecords);

    return result;
  }

  private List<String> createCorpusListFromStream(InputStream inputStream, String fileType, String splitOption)
      throws TikaException, IOException, SAXException {
    List<String> corpus;
    if ("FULL".equalsIgnoreCase(splitOption)) {
      corpus = Collections.singletonList(splitFullDocument(inputStream, fileType));
    } else {
      corpus = Arrays.asList(splitByType(inputStream, fileType, splitOption));
    }
    return corpus;
  }

  private List<List<Double>> getCorpusEmbeddings(String modelName, List<String> corpus, ReadTimeoutParams readTimeout)
      throws IOException, TimeoutException {
    List<List<Double>> corpusEmbeddings = new ArrayList<>();

    for (String text : corpus) {
      if (text != null && !text.isEmpty()) {
        String corpusBody = constructEmbeddingJsonPayload(text);
        try (InputStream embeddingResponse =
            executeEinsteinRequest(corpusBody, modelName, URI_MODELS_API_EMBEDDINGS, readTimeout)) {
          EinsteinEmbeddingResponseDTO embeddingResponseDTO =
              objectMapper.readValue(embeddingResponse, EinsteinEmbeddingResponseDTO.class);
          corpusEmbeddings.add(embeddingResponseDTO.getEmbeddings().get(0).getEmbeddings());
        }
      }
    }
    return corpusEmbeddings;
  }

  private List<List<Double>> getBatchCorpusEmbeddings(String modelName, List<String> corpus, ReadTimeoutParams readTimeout) {
    List<List<Double>> allEmbeddings = new ArrayList<>();
    for (int i = 0; i < corpus.size(); i += 100) {
      // Create the batch from the corpus
      List<String> batch = corpus.subList(i, Math.min(i + 100, corpus.size()));

      // Construct JSON payload for this batch
      String batchJsonPayload = constructEmbeddingJsonPayload(batch);

      // Execute the request and process the response
      try (InputStream embeddingResponse =
          executeEinsteinRequest(batchJsonPayload, modelName, URI_MODELS_API_EMBEDDINGS, readTimeout)) {
        // Parse the embedding response and add it to allEmbeddings
        EinsteinEmbeddingResponseDTO embeddingResponseDTO =
            objectMapper.readValue(embeddingResponse, EinsteinEmbeddingResponseDTO.class);
        allEmbeddings.add(embeddingResponseDTO.getEmbeddings().get(0).getEmbeddings());
      } catch (IOException | TimeoutException e) {
        throw new ModuleException("Error fetching embeddings", EinsteinErrorType.MODELS_API_ERROR, e);
      }
    }
    return allEmbeddings;
  }

  private List<Double> getQueryEmbedding(String body, String modelName, ReadTimeoutParams readTimeout)
      throws IOException, TimeoutException {

    InputStream embeddingResponse = executeEinsteinRequest(body, modelName, URI_MODELS_API_EMBEDDINGS, readTimeout);

    EinsteinEmbeddingResponseDTO embeddingResponseDTO =
        objectMapper.readValue(embeddingResponse, EinsteinEmbeddingResponseDTO.class);

    return embeddingResponseDTO.getEmbeddings().get(0).getEmbeddings();
  }

  private double calculateCosineSimilarity(List<Double> embeddingList, List<Double> corpusEmbedding) {

    double dotProduct = 0.0;
    double normA = 0.0;
    double normB = 0.0;
    for (int i = 0; i < embeddingList.size(); i++) {
      double a = embeddingList.get(i);
      double b = corpusEmbedding.get(i);
      dotProduct += a * b;
      normA += Math.pow(a, 2);
      normB += Math.pow(b, 2);
    }
    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
  }

  private List<String> rankAndPrintResults(List<String> corpus, List<Double> similarityScores) {

    if (!similarityScores.isEmpty() && !corpus.isEmpty()) {

      List<Integer> indices = IntStream
          .range(0, corpus.size())
          .boxed()
          .sorted((i, j) -> Double.compare(similarityScores.get(j),
                                           similarityScores.get(i)))
          .collect(Collectors.toList());

      return indices.stream()
          .map(index -> similarityScores.get(index) + " - " + corpus.get(index))
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  private InputStream executeEinsteinRequest(String payload, String modelName, String resource, ReadTimeoutParams readTimeout)
      throws IOException, TimeoutException {

    String urlString = einsteinConnection.getApiInstanceUrl() + URI_MODELS_API + modelName + resource;
    log.debug("Einstein Request URL: {}", urlString);

    HttpRequestOptions httpRequestOptions = HttpRequestOptions.builder()
        .responseTimeout((int) readTimeout.getReadTimeoutUnit().toMillis(readTimeout.getReadTimeout())).build();

    InputStream payloadStream = new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8));

    HttpResponse httpResponse = einsteinConnection.getHttpClient().send(buildRequest(urlString, einsteinConnection
        .getAccessToken(), HTTP_METHOD_POST, new InputStreamHttpEntity(payloadStream)), httpRequestOptions);

    return handleHttpResponse(httpResponse, EinsteinErrorType.MODELS_API_ERROR);
  }

  private String getFileTypeContextFromFile(InputStream inputStream, String fileType)
      throws IOException, SAXException, TikaException {

    BodyContentHandler handler = new BodyContentHandler(-1);
    Metadata metadata = new Metadata();
    ParseContext pcontext = new ParseContext();

    Parser parser = "PDF".equalsIgnoreCase(fileType) ? new AutoDetectParser() : new TXTParser();
    parser.parse(inputStream, handler, metadata, pcontext);
    String content = handler.toString();
    // Replace non-breaking space (0xA0) with a regular space
    content = content
        .replace("\u00A0", " ") // Non-breaking space
        .replace("\u200B", "") // Zero-width space
        .replace("\uFEFF", "") // BOM
        .replaceAll("[\\p{Cc}&&[^\\t\\n\\r]]", ""); // Control characters except tab, newline, and carriage return

    return content.trim();
  }

  private String splitFullDocument(InputStream inputStream, String fileType) throws TikaException, IOException, SAXException {
    String content = getFileTypeContextFromFile(inputStream, fileType);
    // will match one or more occurrences of either \r\n (Windows line break) or \n (Unix line break).
    content = content.replaceAll("(\\r?\\n)+", "\n");
    return content.trim();
  }

  private String[] splitByType(InputStream inputStream, String fileType, String splitOption)
      throws IOException, SAXException, TikaException {
    String content = getFileTypeContextFromFile(inputStream, fileType);
    return splitContentByParagraph(content, splitOption);
  }

  private String[] splitContentByParagraph(String text, String option) {
    if ("PARAGRAPH".equalsIgnoreCase(option)) {
      return splitByParagraphs(text);
    }
    throw new IllegalArgumentException("Unknown split option: " + option);
  }

  private String[] splitByParagraphs(String text) {
    // it detects and collapses any sequence of one or more line breaks into a single split point.
    return removeEmptyStrings(text.split("\\r?\\n+"));
  }

  /*
   * out any empty or whitespace-only strings from the array of paragraphs that results after splitting the content. This is
   * important because when text is split by newlines, you may end up with empty strings if there are extra or multiple newlines
   * between paragraphs, especially when there are sections with only blank lines.
   */
  private String[] removeEmptyStrings(String[] array) {
    // Convert array to list
    return Arrays.stream(array).filter(Objects::nonNull)
        .map(String::trim)
        .filter(trim -> !trim.isEmpty())
        .toArray(String[]::new);
  }

  private List<String> extractUrls(String input) {
    // Define the URL pattern
    String urlPattern = "(https?://[\\w\\-\\.]+(?:\\.[\\w\\-]+)+(?:[\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])?)";

    // Compile the pattern
    Pattern pattern = Pattern.compile(urlPattern);

    // Create a matcher from the input string
    Matcher matcher = pattern.matcher(input);

    // Find and collect all matches
    List<String> urls = new ArrayList<>();
    while (matcher.find()) {
      // Group 1 contains the URL without trailing whitespace
      urls.add(matcher.group(1));
    }

    // Return null if no URLs are found
    return urls.isEmpty() ? null : urls;
  }

  private String constructPayload(String prompt, String locale, Number probability) {
    JSONObject localization = new JSONObject();
    localization.put("defaultLocale", locale);

    JSONArray inputLocales = new JSONArray();
    JSONObject inputLocale = new JSONObject();
    inputLocale.put("locale", locale);
    inputLocale.put("probability", probability);
    inputLocales.put(inputLocale);
    localization.put("inputLocales", inputLocales);

    JSONArray expectedLocales = new JSONArray();
    expectedLocales.put(locale);
    localization.put("expectedLocales", expectedLocales);

    JSONObject jsonPayload = new JSONObject();
    jsonPayload.put("prompt", prompt);
    jsonPayload.put("localization", localization);
    jsonPayload.put("tags", new JSONObject());

    return jsonPayload.toString();
  }

  private String constructPayloadWithMessages(String message, ParamsModelDetails paramsModelDetails) {
    JSONArray messages = new JSONArray(message);

    JSONObject locale = new JSONObject();
    locale.put("locale", paramsModelDetails.getLocale());
    locale.put("probability", paramsModelDetails.getProbability());

    JSONArray inputLocales = new JSONArray();
    inputLocales.put(locale);

    JSONArray expectedLocales = new JSONArray();
    expectedLocales.put(paramsModelDetails.getLocale());

    JSONObject localization = new JSONObject();
    localization.put("defaultLocale", paramsModelDetails.getLocale());
    localization.put("inputLocales", inputLocales);
    localization.put("expectedLocales", expectedLocales);

    JSONObject tags = new JSONObject();

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("messages", messages);
    jsonObject.put("localization", localization);
    jsonObject.put("tags", tags);

    return jsonObject.toString();
  }

  private String constructPayloadForPromptTemplate(Map<String, WrappedValue> promptInputParams,
                                                   PromptParamsDetails parmaPromptDetails,
                                                   EinsteinLlmAdditionalConfigInputRepresentation additionalConfigInputRepresentation) {
    JSONObject root = new JSONObject(parmaPromptDetails);
    JSONObject valueMap = new JSONObject();
    JSONObject valueMapInputParams = new JSONObject(promptInputParams);
    JSONObject additionalConfig = new JSONObject(additionalConfigInputRepresentation);

    root.put("inputParams", valueMap);
    valueMap.put("valueMap", valueMapInputParams);
    root.put("additionalConfig", additionalConfig);

    return root.toString();
  }

  private String constructEmbeddingJsonPayload(String text) {
    JSONArray input = new JSONArray();
    input.put(text);

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("input", input);

    return jsonObject.toString();
  }

  private String constructEmbeddingJsonPayload(List<String> texts) {
    JSONObject jsonPayload = new JSONObject();
    jsonPayload.put("input", new JSONArray(texts));
    return jsonPayload.toString();
  }

  private String getAttributes(String url, InputStream toolsConfigInputStream, String payload)
      throws IOException, TimeoutException {

    try (InputStream inputStream = toolsConfigInputStream) {

      JSONTokener tokener = new JSONTokener(inputStream);
      JSONArray rootArray = new JSONArray(tokener);
      String responseString = "";

      for (int i = 0; i < rootArray.length(); i++) {

        JSONObject node = rootArray.getJSONObject(i);

        if (node.getString("url").trim().equals(url)) {
          String method = node.getString("method");
          String headers = node.getString("headers");
          HttpEntity httpEntity = "POST".equalsIgnoreCase(method)
              ? new InputStreamHttpEntity(new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)))
              : new EmptyHttpEntity();
          MultiMap<String, String> requestHeaders = new MultiMap<>();
          if (headers != null && !headers.isEmpty()) {
            requestHeaders.put(AUTHORIZATION, headers);
          }
          requestHeaders.put(CONTENT_TYPE_STRING, CONTENT_TYPE_APPLICATION_JSON);

          HttpRequest request = HttpRequest.builder()
              .uri(url)
              .headers(requestHeaders)
              .method(method)
              .entity(httpEntity)
              .build();

          HttpResponse httpResponse = einsteinConnection.getHttpClient().send(request);
          responseString = handleHttpResponseForTools(httpResponse);
          break;
        }
      }
      return responseString;
    }
  }

  private String extractPayload(String payload) {

    Pattern pattern = Pattern.compile("\\{.*\\}");
    Matcher matcher = pattern.matcher(payload);
    String response;
    if (matcher.find()) {
      response = matcher.group();
    } else {
      response = "Payload not found!";
    }
    return response;
  }

  private String buildPayload(String payload) {

    String findPayload = extractPayload(payload);
    if (findPayload.equals("Payload not found!")) {
      return extractPayload(payload);
    }
    return findPayload;
  }

  private HttpRequest buildRequest(String url, String accessToken, String httpMethod, HttpEntity httpEntity) {
    return HttpRequest.builder()
        .uri(url)
        .headers(addConnectionHeaders(accessToken))
        .method(httpMethod)
        .entity(httpEntity)
        .build();
  }

  private HttpRequest buildRequest(String url, String accessToken, String httpMethod, MultiMap<String, String> queryParams,
                                   HttpEntity httpEntity) {
    return HttpRequest.builder()
        .uri(url)
        .headers(addConnectionHeaders(accessToken))
        .method(httpMethod)
        .queryParams(queryParams)
        .entity(httpEntity)
        .build();
  }

  private MultiMap<String, String> addConnectionHeaders(String accessToken) {
    MultiMap<String, String> multiMap = new MultiMap<>();
    multiMap.put(AUTHORIZATION, "Bearer " + accessToken);
    multiMap.put(X_SFDC_APP_CONTEXT, EINSTEIN_GPT);
    multiMap.put(X_CLIENT_FEATURE_ID, AI_PLATFORM_MODELS_CONNECTED_APP);
    multiMap.put(CONTENT_TYPE_STRING, CONTENT_TYPE_APPLICATION_JSON);
    return multiMap;
  }
}
