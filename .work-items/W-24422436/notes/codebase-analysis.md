# Codebase analysis — W-24422436 (mule-einstein-ai-connector)

Direct inspection; no Explore subagent was needed because the change is localized to `src/test/munit/`.

## Layout
- Single-module `mule-extension`; parent `org.mule.extensions:mule-modules-parent:1.6.9`; `muleJavaEeBomVersion=4.6.9` → JDK 8 lane by default.
- MUnit: `munit-extensions-maven-plugin` 1.7.0 / MUnit 3.7.0 (pom.xml:137-170). JaCoCo agent wired into the MUnit JVM.
- MUnit suites: `src/test/munit/config.xml` (configs), `EmbeddingOperationsTestCase.xml` (191 lines), `GenerationsOperationTestCase.xml` (213 lines).
- Credentials: `config.xml` loads `automation-credentials.properties` (Vault, `connector-credentials` engine): `config.client-id`, `config.client-secret`, `config.salesforce-org`.
- Java unit tests (`src/test/java/...`) are mock-based; not affected by the 429.

## Live-API call inventory per MUnit test (EmbeddingOperationsTestCase.xml)

| Test | Op | Embeddings calls | Generations calls |
| --- | --- | --- | --- |
| EmbeddingFromText (l.21) | embedding-generate-from-text | 1 | 0 |
| EmbeddingFromFile (l.38) | embedding-generate-from-file, optionType=PARAGRAPH | 1 per 100 paragraphs (`getBatchCorpusEmbeddings`) → 1 | 0 |
| EmbeddingAdhocFileQuery (l.55) | embedding-adhoc-file-query, default optionType=FULL | 1 query + 1 per corpus chunk (`getCorpusEmbeddings`, one HTTP call per chunk) → 2 | 0 |
| RAGAdhocLoadDocument (l.74) | rag-adhoc-load-document | 0 | 1 |
| ToolsUseAiService (l.94) | tools-use-ai-service | 0 | 2 (+1 outbound mock API GET) |
| EmbeddingFromTextWithTlS (l.113) | embedding-generate-from-text (TLS config) | 1 | 0 |
| ToolsAiServiceFailure (l.150) | expected TOOLS_OPERATION_FAILURE | 0–1 | 0–1 |
| RAGAdhocLoadDocumentFailure (l.164) | expected RAG_FAILURE | 0 | 0–1 |
| EmbeddingAdhocFileQueryFailure (l.177) | expected EMBEDDING_OPERATIONS_FAILURE | 0–1 | 0 |

Embedding total per suite run ≈ 5–7 calls within a few seconds, against an observed gatekeeper budget of `limit=5` with `reset≈51-54s` (one-minute window). The suite is at or above the budget by construction, before counting concurrent SFCI lanes (FIPS profiles / multiple runtime lanes) that share the same tenant/connected app.

`GenerationsOperationTestCase.xml` makes further generations calls (ChatWithPrompt, ChatGenerateFromMessages, AgentDefinePromptTemplate, ChatWithPromptWithTLS, error tests). If the gatekeeper budget is per connected-app feature (`feature: ai-platform-models-connected-app`) rather than per endpoint, generations calls also draw it down — this would explain ToolsUseAiService (generations-only) appearing in the failure set.

## Error surfacing path
- `RequestHelper.executeEinsteinRequest` (src/main/.../modelsapi/helpers/RequestHelper.java:317) → `handleHttpResponse(..., MODELS_API_ERROR)`.
- `HttpRequestHelper` (src/main/.../helpers/HttpRequestHelper.java:114-128): non-200/401 → `ModuleException("Error in HTTP request. ErrorCode: %d, ErrorMessage: %s")`.
- Operations wrap everything in their own catch-all: e.g. `queryEmbeddingOnFiles` rethrows as `EMBEDDING_OPERATIONS_FAILURE` with message "Error while generating the chat, for prompt …" (EinsteinEmbeddingOperations.java:117-119). The `ErrorCode: 429` text lives in the **cause** chain, not necessarily in `error.description`.
- No 429-specific error type, no retry/backoff in `src/main` (grep for `429|retry|RATE_LIMIT` returns nothing).

## Relevant uber knowledge
- `knowledge/munit-sfdc-oauth-warmup-pattern.md` — bounded `<until-successful>` deflake pattern (its ≤3000 ms delay bound does NOT fit a 60 s rate window; must be adapted).
- `knowledge/munit-mocking-and-error-testing.md`, `knowledge/munit-parameterization.md` (`-Dmunit.test` for isolating the Embedding suite locally).
