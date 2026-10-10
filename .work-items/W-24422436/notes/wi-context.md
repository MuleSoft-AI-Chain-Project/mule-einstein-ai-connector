# W-24422436 — WI context (imported from GUS by u2-spec node)

- Type: User Story · Status: In Progress
- Subject: Investigate Einstein AI embedding API rate limiting in integration tests
- Intake completeness verdict (u1): gaps
- Connector branch: feature/W-24422436

## Purpose
Investigate the recurring Einstein AI integration-test failures caused by LLM Gatekeeper
embedding API rate limiting, and identify a durable remediation for the shared test
tenant / test execution approach.

## Evidence (SFCI master builds #153, #154, #155, #156 — mule4-einstein-ai-connector)
```
ErrorCode: 429
errorCode: LLM_GATEKEEPER
messageCode: E90000
message: You have exceeded your rate limit. Please try again later.
limit=5, remaining=0, reset=51-54
endpoint: /v2/models/sfdc_ai__DefaultOpenAITextEmbeddingAda_002/embeddings
feature: ai-platform-models-connected-app
org_status: Free
```
Recurring affected tests: EmbeddingFromFile, ToolsUseAiService, EmbeddingAdhocFileQuery.
Setup, Precheck, Build, Package pass; only the Integration test stage fails.
