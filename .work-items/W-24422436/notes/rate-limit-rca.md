# Root cause: Einstein integration tests fail with HTTP 429 LLM_GATEKEEPER

## Summary
The MUnit integration suites call the live Salesforce Models API on a shared, `org_status: Free`
test tenant. LLM Gatekeeper limits that tenant to **5 requests per ~60 s window**. One run of the
suites issues roughly 13–17 live model calls within a few seconds, and every SFCI lane (default,
`fips140-2`, `fips140-3`, runtime-version lanes) uses the same Vault credentials. The suites exceed
the quota by construction, so the Integration-test stage fails whenever the window is exhausted.
This is a test-design problem, not a connector regression.

## (a) Live-API call inventory per test

Source of the fan-out: `RequestHelper` (`getCorpusEmbeddings` = one HTTP call per chunk;
`getBatchCorpusEmbeddings` = one per 100 chunks; tools = one generation for tool selection plus
one for the final answer).

### EmbeddingOperationsTestCase.xml
| Test | Operation | Embeddings | Generations |
| --- | --- | --- | --- |
| EmbeddingFromText | embedding-generate-from-text | 1 | 0 |
| EmbeddingFromFile | embedding-generate-from-file (PARAGRAPH, batched) | 1 | 0 |
| EmbeddingAdhocFileQuery | embedding-adhoc-file-query (FULL) | 2 (query + 1 per chunk) | 0 |
| RAGAdhocLoadDocument | rag-adhoc-load-document | 0 | 1 |
| ToolsUseAiService | tools-use-ai-service | 0 | 2 (+1 outbound GET to the tool API) |
| EmbeddingFromTextWithTlS | embedding-generate-from-text (TLS) | 1 | 0 |
| ToolsAiServiceFailure (negative) | expects TOOLS_OPERATION_FAILURE | 0–1 | 0–1 |
| RAGAdhocLoadDocumentFailure (negative) | expects RAG_FAILURE | 0 | 0–1 |
| EmbeddingAdhocFileQueryFailure (negative) | expects EMBEDDING_OPERATIONS_FAILURE | 0–1 | 0 |

### GenerationsOperationTestCase.xml
| Test | Operation | Embeddings | Generations |
| --- | --- | --- | --- |
| ChatWithPrompt | chat-answer-prompt | 0 | 1 |
| ChatGenerateFromMessages | chat-generate-from-messages | 0 | 1 |
| AgentDefinePromptTemplate | agent-define-prompt-template | 0 | 1 |
| ChatWithPromptWithTLS | chat-answer-prompt (TLS) | 0 | 1 |
| EinsteinWithError / EinsteinWithTLSError (negative) | invalid client credentials, fail at OAuth | 0 | 0 |

Totals per lane: about 5–7 embedding calls and 8–10 generation calls, all within seconds.

## (b) Observed gatekeeper limit and window
From the SFCI master builds #153–#156 console output (as captured on the GUS work item):
`ErrorCode: 429`, `errorCode: LLM_GATEKEEPER`, `messageCode: E90000`,
`You have exceeded your rate limit. Please try again later.`,
`limit=5, remaining=0, reset=51-54`, `feature: ai-platform-models-connected-app`, `org_status: Free`.
So the budget is 5 requests and the window resets 51–54 s after it is exhausted (a one-minute window).

## (c) Per-endpoint or per-feature budget
**The budget is per connected-app feature, not per endpoint.** Evidence:
- The gatekeeper response names the quota by `feature: ai-platform-models-connected-app`, not by
  model or path.
- ToolsUseAiService fails with the same 429 in the same builds although it never calls the
  embeddings endpoint: `RequestHelper.executeTools` only posts to `URI_MODELS_API_GENERATIONS`
  (`/v2/models/<model>/generations`). The 429 endpoint seen for ToolsUseAiService is therefore the
  generations endpoint, while the GUS evidence line names
  `/v2/models/sfdc_ai__DefaultOpenAITextEmbeddingAda_002/embeddings` for the embedding tests.
  A per-endpoint embeddings quota could not throttle a generations-only test.
- Consequence: generation calls draw down the same 5-per-minute budget, so both suites need the
  same treatment.

Limitation: the raw SFCI console logs for #153–#156 were not re-downloaded for this note (the
partner SFCI job path is not resolvable through `scripts/sfci_operations.py`); the evidence above is
the console excerpt recorded on the work item plus the code path proving ToolsUseAiService is
generations-only.

## (d) Do concurrent SFCI lanes share the tenant?
Yes. All lanes load the same `automation-credentials.properties` from Vault
(`config.client-id`, `config.client-secret`, `config.salesforce-org`), so they authenticate as the
same connected app and spend one per-minute budget. Pacing inside one JVM cannot prevent another
lane from using the window, which is why a 429-aware retry is required rather than sleeps between
tests.

## (e) Remediation options (ranked)
1. **Test-side, implemented here — bounded 429-aware retry.** Positive live tests run their call
   through the shared `einstein-call-with-rate-limit-retry` sub-flow (`src/test/munit/rate-limit-helpers.xml`).
   It retries only when the error description carries `ErrorCode: 429` or `LLM_GATEKEEPER`
   (Mule sets `error.description` to the message of the root `MuleException`, which is the
   connector's `ModuleException("Error in HTTP request. ErrorCode: 429 ...")`). Other errors
   propagate on the first attempt with their original type. Defaults: 3 retries, delay = the
   gatekeeper's `reset` + 2 s, capped at 60 s; worst case +3 min per throttled call. Both are
   overridable with `-Deinstein.ratelimit.maxRetries` / `-Deinstein.ratelimit.retryDelayMillis`.
   Negative (`expectedErrorType`) tests are not wrapped.
2. **Test-side, optional — fewer live calls.** E.g. use `optionType="PARAGRAPH"` batching in
   EmbeddingAdhocFileQuery or share one embedding result across assertions. Not done, because it
   changes which code paths are exercised; the retry alone keeps the suites green.
3. **Connector-side (follow-up story only, not implemented).** Map HTTP 429 to a dedicated
   retryable error type and/or honour `reset` with backoff inside the connector. This changes
   runtime behaviour and the ExtensionModel, so it needs its own story and a MINOR bump.
4. **Tenant-side (plan only).** Move the test org off `org_status: Free`, or ask LLM Gatekeeper for
   a higher quota for the test connected app, or give each SFCI lane its own connected app. This is
   the durable fix for build time, but it is infrastructure outside this repository.
