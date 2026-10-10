# Root cause: Einstein integration tests fail with HTTP 429 LLM_GATEKEEPER

## Summary
The MUnit integration suites call the live Salesforce Models API on a shared, `org_status: Free`
test tenant. LLM Gatekeeper limits that tenant to **5 embeddings requests per minute** (quota key
`api;minute;5;endpoint:/v2/models/sfdc_ai__DefaultOpenAITextEmbeddingAda_002/embeddings&feature:ai-platform-models-connected-app&core_tenant_id:...&org_status:Free`).
One run of `EmbeddingOperationsTestCase.xml` issues about **9 embeddings calls within ~10 s**, so
each SFCI lane exceeds the quota on its own, by construction. The Integration-test stage fails on
every lane. This is a test-design problem, not a connector regression.

Evidence source: SFCI console logs of master builds #155 and #156
(`managed-sfci-gec-mulesoft-emu/job/mulesoft-emu/job/mule4-einstein-ai-connector/job/master/<n>/consoleText`;
fetch with `python3 scripts/sfci_operations.py log mule4-einstein-ai-connector master --build <n>`,
which takes the SFCI job name, not the `REPO_NAME`). The logs for #153 and #154 have been purged
(HTTP 404). The GUS work item records the same 429 excerpt for all four builds.

## (a) Live-API call inventory per test

How calls fan out: `RequestHelper.embeddingFileQuery` makes 1 query embedding plus 1 embedding
per corpus chunk (`getCorpusEmbeddings`, one HTTP call per chunk; default `optionType=FULL`
gives 1 chunk). `getBatchCorpusEmbeddings` makes one call per 100 chunks. **`rag-adhoc-load-document`
and `tools-use-ai-service` both call `embeddingFileQuery` before they call generations**
(`EinsteinEmbeddingOperations.ragOnFiles` / `executeTools`).

### EmbeddingOperationsTestCase.xml (SFCI execution order)
| Test | Operation | Embeddings | Generations |
| --- | --- | --- | --- |
| RAGAdhocLoadDocumentFailure (negative) | expects RAG_FAILURE (empty prompt) | 0–2 | 0 |
| ToolsAiServiceFailure (negative) | expects TOOLS_OPERATION_FAILURE (empty prompt) | 0–2 | 0 |
| RAGAdhocLoadDocument | rag-adhoc-load-document | 2 | 1 |
| EmbeddingFromTextWithTlS | embedding-generate-from-text (TLS) | 1 | 0 |
| EmbeddingFromText | embedding-generate-from-text | 1 | 0 |
| EmbeddingAdhocFileQuery | embedding-adhoc-file-query (FULL) | 2 | 0 |
| EmbeddingAdhocFileQueryFailure (negative) | expects EMBEDDING_OPERATIONS_FAILURE | 0–1 | 0 |
| EmbeddingFromFile | embedding-generate-from-file (PARAGRAPH, batched) | 1 | 0 |
| ToolsUseAiService | tools-use-ai-service | 2 | 2 (+1 outbound GET to the tool API) |

Positive tests alone: **9 embeddings calls** per lane. In build #156 the 5-call budget ran out
at the second embeddings call of EmbeddingAdhocFileQuery (2 RAG + 1 TLS + 1 text + 1 query = 5).

### GenerationsOperationTestCase.xml
| Test | Operation | Embeddings | Generations |
| --- | --- | --- | --- |
| ChatWithPrompt | chat-answer-prompt | 0 | 1 |
| ChatGenerateFromMessages | chat-generate-from-messages | 0 | 1 |
| AgentDefinePromptTemplate | agent-define-prompt-template | 0 | 1 |
| ChatWithPromptWithTLS | chat-answer-prompt (TLS) | 0 | 1 |
| EinsteinWithError / EinsteinWithTLSError (negative) | invalid client credentials, fail at OAuth | 0 | 0 |

No generations call has ever returned 429 in the inspected builds.

## (b) Observed gatekeeper limit and window
From the #156 console log (default lane, 09:45:03Z):
`Error in HTTP request. ErrorCode: 429 , ErrorMessage: {"errorCode":"LLM_GATEKEEPER","messageCode":"E90000","message":"You have exceeded your rate limit. Please try again later. {limit=5, remaining=0, reset=53, key=api;minute;5;endpoint:/v2/models/sfdc_ai__DefaultOpenAITextEmbeddingAda_002/embeddings&feature:ai-platform-models-connected-app&core_tenant_id:core/prod/00Dj0000001pwxKEAQ&org_status:Free}" ...}`
So the limit is 5 requests in a one-minute window (`api;minute;5`), and `reset` (51–54 s across
the builds) gives the seconds left until the window resets.

## (c) Per-endpoint or per-feature budget
**The budget is per endpoint (scoped to the feature and tenant), not one shared per-feature
budget for all Models API calls.** Evidence:
- The quota key itself names the endpoint:
  `key=api;minute;5;endpoint:/v2/models/sfdc_ai__DefaultOpenAITextEmbeddingAda_002/embeddings&feature:ai-platform-models-connected-app&...`.
- **ToolsUseAiService** fails on the **embeddings** endpoint, not on generations. #156 log line:
  `Error [MS-EINSTEIN-AI:TOOLS_OPERATION_FAILURE] while running test 'ToolsUseAiService':Error in HTTP request. ErrorCode: 429 , ErrorMessage: {..."reset=51, key=api;minute;5;endpoint:/v2/models/sfdc_ai__DefaultOpenAITextEmbeddingAda_002/embeddings&feature:ai-platform-models-connected-app&..."}`
  (default lane 09:45:05Z; the fips140-3 lane fails the same way at 09:47:46Z). The reason is that
  `tools-use-ai-service` first runs `embeddingFileQuery` over the tools config (2 embeddings calls).
  An earlier hypothesis said ToolsUseAiService was generations-only. That was wrong.
- Generation calls do not hit the quota: RAGAdhocLoadDocument's generation call and all
  GenerationsOperationTestCase tests passed in the same builds.
- Consequence: the embeddings suite is the one that needs treatment. The generations suite is
  wrapped too as a cheap safeguard, in case a generations quota on the shared tenant ever bites.

## (d) Do concurrent SFCI lanes share the tenant?
They share it (all lanes load the same Vault `automation-credentials.properties`, so the same
connected app and tenant), but in #156 they ran **one after another**, not at the same time:
default lane (runtime 4.12.3) suite at 09:44:54–09:45:05Z, fips140-3 lane (runtime 4.9.19) suite
at 09:47:38–09:47:46Z. A fresh window had started before the second lane, and it still failed. So
the overrun comes from each suite on its own (9 calls against a quota of 5). Lane overlap, or other
users of the tenant, can only make it worse. Pacing inside one JVM cannot protect against outside
consumers, so the remediation retries on 429 instead of only adding sleeps between tests.

## (e) Remediation options (ranked)
1. **Test-side, implemented: bounded 429-aware retry.** Positive live tests run their call
   through the shared `einstein-call-with-rate-limit-retry` sub-flow (`src/test/munit/rate-limit-helpers.xml`).
   It retries only when the error description carries `ErrorCode: 429` or `LLM_GATEKEEPER`. The
   console log shows the connector's `ModuleException("Error in HTTP request. ErrorCode: 429 ...")`
   message as the error description. Other errors propagate on the first attempt with their
   original type. Defaults: 3 retries; delay = the gatekeeper's `reset` + 2 s, capped at 60 s;
   worst case +3 min per throttled call. With a quota of 5 against 9 calls, a lane needs about one
   wait (~1 min) per run. Both values can be overridden with `-Deinstein.ratelimit.maxRetries` /
   `-Deinstein.ratelimit.retryDelayMillis`. Negative (`expectedErrorType`) tests are not wrapped.
2. **Test-side, optional: fewer live calls.** For example, share one embedding result across
   assertions. Not done, because it changes which code paths are exercised, and the retry alone
   keeps the suites green.
3. **Connector-side (follow-up story only, not implemented).** Map HTTP 429 to a dedicated
   retryable error type and/or honour `reset` with backoff inside the connector. Batch the
   per-chunk embeddings calls in `embeddingFileQuery` (it calls once per chunk, unlike
   `getBatchCorpusEmbeddings`). This changes runtime behaviour (and, for a new error type, the
   ExtensionModel), so it needs its own story.
4. **Tenant-side (plan only).** Move the test org off `org_status: Free`, ask LLM Gatekeeper for
   a higher embeddings quota for the test connected app, or give each SFCI lane its own connected
   app. This is the durable fix for build time, but it is infrastructure outside this repository.
