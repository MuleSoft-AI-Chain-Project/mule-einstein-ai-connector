# mule-einstein-ai-connector — knowledge index

- [Test tenant LLM Gatekeeper rate limit](test-tenant-llm-gatekeeper-rate-limit.md) | `#munit` `#rate-limit` `#429` `#test-tenant` `#embeddings` — Free-tier tenant is limited to 5 req/60 s; per-operation embedding call fan-out; 429 is buried in the cause chain; Embedding suite is at budget by construction
- [MUnit lazy init does not follow dynamic flow-refs](munit-dynamic-flow-ref-lazy-init.md) | `#munit` `#flow-ref` `#lazy-init` `#dataweave` — Dynamic flow-ref targets need munit:enable-flow-source; `input` is reserved in DW; 429 match on error.description
