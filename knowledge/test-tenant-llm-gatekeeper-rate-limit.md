# Einstein test tenant: LLM Gatekeeper rate limit and MUnit call budget

## Context
The MUnit suites run against a live Salesforce Models API tenant (Vault `automation-credentials.properties`, `config.client-id` / `config.salesforce-org`). That org is `org_status: Free`, and LLM Gatekeeper limits it to **5 requests per ~60 s** (`limit=5, reset=51-54`, feature `ai-platform-models-connected-app`). SFCI builds #153–#156 failed the Integration stage with `ErrorCode: 429 / LLM_GATEKEEPER / E90000`.

## Details
- Embedding calls per operation: `embedding-generate-from-text` = 1; `embedding-generate-from-file` = 1 per 100 chunks with PARAGRAPH (batched), 1 per chunk otherwise; `embedding-adhoc-file-query` = 1 query + **1 per chunk** (not batched; default optionType FULL → 2). `rag-adhoc-load-document` and `tools-use-ai-service` call generations only (tools makes 2).
- `EmbeddingOperationsTestCase.xml` spends about 5–7 embedding calls per run, which is at or over the quota before counting the parallel FIPS/runtime lanes that share the tenant.
- The `ErrorCode: 429` text is built in `HttpRequestHelper` and then buried in the cause chain: operations rethrow as `EMBEDDING_OPERATIONS_FAILURE` / `TOOLS_OPERATION_FAILURE` / `RAG_FAILURE` with a generic description.
- There is no retry/backoff in `src/main`.

## Examples
Isolate the suite locally: `mvn clean verify -DruntimeProduct=MULE_EE -DruntimeVersion=4.6.27 -Dmunit.test='.*EmbeddingOperationsTestCase.*'`.
