---
work_item_id: W-24422436
type: User Story
subject: Investigate Einstein AI embedding API rate limiting in integration tests
product_tag: MS MuleSoft EinsteinAI Connector
size: S
in_scope_repos: [mule-einstein-ai-connector]
mule_version_target: 4.6.27
parent_work_item: null
related_work_items: []
generated_at: 2026-10-10T17:00:00Z
generator: tech-spec-writer
---

# SPEC — W-24422436: Einstein AI embedding API rate limiting in integration tests

## 1. Goal & Non-Goals

### Goal
Find out why the Einstein AI integration tests keep failing with HTTP 429 `LLM_GATEKEEPER` / `E90000` ("You have exceeded your rate limit") on the shared test tenant, and ship a lasting test-side fix so the SFCI Integration-test stage no longer fails on the per-minute embedding quota `[from GUS]`. The investigation part ends in a written root cause and a call-budget inventory. The remediation part is a change limited to `src/test/` that keeps the MUnit suites within the tenant quota, or absorbs throttling when it happens, without weakening any assertion `[inferred from GUS Purpose: "identify a durable remediation for the shared test tenant / test execution approach"]`.

### Success signals (measurable)
- An SFCI master build of `mule4-einstein-ai-connector` after the merge passes the Integration test stage with no `ErrorCode: 429` / `LLM_GATEKEEPER` in the console log. Builds #153–#156 failed this way `[from GUS]`.
- `mvn clean verify -DruntimeProduct=MULE_EE -DruntimeVersion=4.6.27` passes locally twice in a row (back-to-back, inside one rate window) against the Vault test tenant.
- A root-cause write-up with a per-test live-API call inventory is committed under `.work-items/W-24422436/notes/`.

### Non-Goals (explicit out-of-scope)
- **No connector production-code change** (`src/main/**`). Adding 429-aware retry/backoff or a new `RATE_LIMITED` error type to the connector would change behavior and the ExtensionModel. If the investigation recommends it, it goes into a follow-up story proposal only.
- **No tenant/org upgrade work.** Moving the test org off `org_status: Free` or asking LLM Gatekeeper for a higher quota is infrastructure outside the local environment. It is documented as a plan-only recommendation (CLAUDE.md General Constraints).
- No deleting, `@Ignore`-ing, or `ignore="true"`-ing any of the three affected tests (EmbeddingFromFile, ToolsUseAiService, EmbeddingAdhocFileQuery) or any other test. No loosening of assertions (General Constraints: do not remove functionality; [`knowledge/munit-sfdc-oauth-warmup-pattern.md`](../../../../knowledge/munit-sfdc-oauth-warmup-pattern.md) "Do not weaken assertions").
- No mocking of the Einstein Models API in the MUnit suites. They are the connector's only live integration coverage. Mocked coverage already exists in the Java unit tests.
- No changes to SFCI pipeline definitions (`connectors-cicd-sfci-build`) or lane matrix.
- Re-enabling the commented-out proxy tests is out of scope.

## 2. Context

### Users & flows
Not user-facing. The people affected are connector maintainers and the autonomous build-stability tooling (`/check-builds`, `/stabilize-build`, CU PR monitor). They currently get a red master build that has nothing to do with code changes.

### Repos & modules touched
- **mule-einstein-ai-connector** (independent; GitHub `MuleSoft-AI-Chain-Project`, SFCI folder `release/einstein`)
  - Modules: single `mule-extension` module. Changes are expected only under `src/test/munit/` (and possibly `src/test/resources/` and test-only `pom.xml` MUnit plugin config).
  - Interface delta: **no**. Nothing in the ExtensionModel changes, so revapi should report no delta.

### Prior Art
- **Selected reuse target**: `none — not applicable` `[from notes/prior-art.md]`. The sweep (run as `user-story-new-feature`) picked `mule-a2a-connector` from metadata alone (0 strong / 13 weak / 128 none). This story is test-infrastructure deflake work, not a new connector surface, so no sibling's operation surface needs mirroring.
- **Sibling connectors examined**: mule-a2a / agentforce / inference / mcp / vectors (ai-chain, weak, metadata-only: they may hit similar LLM-provider quotas in their own MUnit suites, but nothing is known to borrow); mule4-salesforce-* (salesforce, weak, metadata-only).
- **Why this sibling**: no sibling anchor. The closest prior art is the uber knowledge entry [`knowledge/munit-sfdc-oauth-warmup-pattern.md`](../../../../knowledge/munit-sfdc-oauth-warmup-pattern.md): a bounded `<until-successful>` deflake around a live SFDC call. Its `millisBetweenRetries ≤ 3000` bound does **not** fit a 60 s quota window, so the architect has to adapt it, not copy it.
- **Degradation notes**: "141 metadata-only (siblings not cloned)" `[from notes/prior-art.md]`.

### Root-cause hypothesis (from codebase analysis — to be confirmed by AC 1)
Detail: [`notes/codebase-analysis.md`](notes/codebase-analysis.md).
- The tenant quota is **limit=5 per ~60 s** (`reset=51-54`) on `/v2/models/sfdc_ai__DefaultOpenAITextEmbeddingAda_002/embeddings`, `feature: ai-platform-models-connected-app`, `org_status: Free` `[from GUS]`.
- One run of `EmbeddingOperationsTestCase.xml` makes about **5–7 embedding calls within seconds**: FromText 1, FromFile 1 (batched), AdhocFileQuery 2 (query + one per chunk with `optionType=FULL`), FromTextWithTlS 1, plus 0–1 from each negative test. That is at or over the quota by construction.
- SFCI runs several lanes (default, `fips140-2`, `fips140-3` profiles / runtime versions) against **the same Vault tenant/connected app**. Concurrent or back-to-back lanes therefore share one per-minute budget. A suite that sits right at the budget fails whenever lanes overlap.
- `ToolsUseAiService` makes **no embedding calls**; it makes generation calls only. It failing alongside the others suggests the gatekeeper budget is per connected-app feature, not per endpoint. AC 1 must confirm this from the build #153–#156 console logs.

### Data model
N/A — no data model changes.

### Runtime considerations
- `mule_version_target: 4.6.27` (uber default verification runtime; pom `muleJavaEeBomVersion=4.6.9` < 4.9.0 → **JDK 8**). SFCI also runs FIPS lanes; the fix must work on all of them.
- Wall-clock budget: any retry/pacing design adds up to roughly one rate window (~60 s) per throttled call. The total added suite time must stay bounded (see AC 4).
- Docker is not required by this connector's tests. Vault credentials (`automation-credentials.properties`) are required for the MUnit suites.

## 3. Requirements & Interfaces

### Business rules
- R1. A test that hits a live Einstein endpoint must not fail **only** because of HTTP 429 `LLM_GATEKEEPER` when the quota resets within a bounded wait.
- R2. Any **non-429** error from a positive test (auth failure, 4xx/5xx other than 429, assertion mismatch) must still fail the test. Rate-limit handling must not hide real regressions.
- R3. Negative tests (`expectedErrorType=...`: ToolsAiServiceFailure, RAGAdhocLoadDocumentFailure, EmbeddingAdhocFileQueryFailure, the Generations error tests) keep their current expected error types and must not be wrapped in retry logic that changes the propagated error type (e.g. into `MULE:RETRY_EXHAUSTED`).
- R4. Retry/pacing is bounded: a permanently throttled or broken tenant fails the build in finite time with a diagnostic message naming the 429 cause.
- R5. Lowering the suite's live-call count where it costs no coverage is allowed and encouraged. Example: reuse one embedding result across assertions, or avoid duplicate calls that test the same code path. Every operation still covered today must still be exercised against the live API at least once.

### Edge cases
- The quota window resets at `reset=51-54 s`: a fixed retry delay shorter than the remaining window just burns another attempt. The delay must cover the reset (≥ 60 s, or derived from the response's reset value if the architect can read it).
- The 429 detail sits in the **cause chain**: operations rethrow as `EMBEDDING_OPERATIONS_FAILURE` / `TOOLS_OPERATION_FAILURE` / `RAG_FAILURE` with a generic description (e.g. EinsteinEmbeddingOperations.java:117-119), while `ErrorCode: 429` comes from `HttpRequestHelper.java:128`. A 429 check that only reads `error.description` may never match. The architect must verify which DataWeave error field carries the text (`error.description`, `error.detailedDescription`, `error.cause.message`, `error.errorMessage`).
- Concurrent SFCI lanes: pacing inside one JVM does not stop another lane from spending the shared budget. The design must tolerate this, which means retry-on-429 is needed, not just inter-test sleeps.
- The OAuth token call (`tokenUrl`) is not rate-limited by the gatekeeper and must not be retried by the new logic.

### External surface (changes)
None. No operation, source, connection provider, value provider, metadata, or error-type change.

### Internal surface (changes)
- `src/test/munit/EmbeddingOperationsTestCase.xml`: rate-limit-aware wrapping and/or pacing of positive live-API tests; possible call-count reduction.
- `src/test/munit/GenerationsOperationTestCase.xml`: same treatment, **only if** AC 1 shows generation calls share the gatekeeper budget.
- `src/test/munit/config.xml` (or a new `src/test/munit/rate-limit-helpers.xml`): optional shared helper flow / sub-flow for the 429-aware retry, so the logic is not copy-pasted.
- `pom.xml`: test-plugin configuration only, if the architect chooses suite ordering or serialization via MUnit plugin config. No dependency changes without `/versions`.

## 4. Acceptance Criteria

> GUS carries **zero** `ADM_Acceptance_Criterion__c` records for this WI (intake verdict u1 = `gaps`). The ACs below are **inferred** from the GUS subject, Purpose, and Evidence, and are surfaced as FYI confirmations in the agent report.

### AC 1: Root cause documented with evidence
- **Given** SFCI master builds #153–#156 of `mule4-einstein-ai-connector` and the current MUnit suites
- **When** the investigation is complete
- **Then** `.work-items/W-24422436/notes/rate-limit-rca.md` exists and contains: (a) the per-test live-API call inventory (embeddings vs generations); (b) the observed gatekeeper limit/window; (c) whether the budget is per endpoint or per connected-app feature, with the console-log line(s) proving it, including the 429 endpoint seen for ToolsUseAiService; (d) whether concurrent SFCI lanes share the tenant; (e) a ranked list of remediation options covering test-side, connector-side (follow-up only), and tenant-side (plan only)
- **Machine check**: `test -s .work-items/W-24422436/notes/rate-limit-rca.md && grep -qiE 'per[- ](endpoint|feature)' .work-items/W-24422436/notes/rate-limit-rca.md && grep -q 'ToolsUseAiService' .work-items/W-24422436/notes/rate-limit-rca.md`
- **Source**: inferred (GUS Purpose: "Investigate …")

### AC 2: Positive embedding/tools tests tolerate a bounded 429
- **Given** the Einstein test tenant returns HTTP 429 `LLM_GATEKEEPER` for a positive test's live call
- **When** the quota resets within the bounded retry budget
- **Then** EmbeddingFromFile, EmbeddingAdhocFileQuery, and ToolsUseAiService (and every other positive live-API test the RCA identifies as budget-consuming) pass with their **unchanged** assertions
- **Machine check**: MUnit. The architect picks one of: (i) a deterministic MUnit test of the shared rate-limit helper flow that uses `munit-tools:mock-when` to raise an error whose cause carries `ErrorCode: 429` on the first N attempts and then succeeds, asserting the helper retried and returned the success payload; or (ii) an equivalent deterministic check. Plus the live gate: `mvn clean verify -DruntimeProduct=MULE_EE -DruntimeVersion=4.6.27` green.
- **Source**: GUS (Evidence: recurring affected tests) + inferred

### AC 3: Non-429 errors and negative tests are unaffected
- **Given** a positive test's live call fails with a non-429 error, or a negative test that expects a specific error type
- **When** the suite runs
- **Then** the non-429 error fails the test right away with no retry, and every `expectedErrorType` test still passes with its original error type
- **Machine check**: MUnit test of the helper flow mocking a non-429 error (e.g. `ErrorCode: 500`) and asserting a single invocation (`munit-tools:verify-call times="1"`) and propagation; existing `ToolsAiServiceFailure`, `RAGAdhocLoadDocumentFailure`, `EmbeddingAdhocFileQueryFailure`, `EinsteinWithError`, `EinsteinWithTLSError` pass unchanged (`git diff` shows their `expectedErrorType` attributes untouched)
- **Source**: inferred (R2/R3; CLAUDE.md backward-compat / do-not-weaken)

### AC 4: Bounded wall-clock and diagnostic failure
- **Given** a tenant that stays throttled
- **When** retries are exhausted
- **Then** the test fails within a documented upper bound (recommended: ≤ 3 retries × ≥ 60 s per throttled call, with the total budget stated in PLAN.md) and the failure output or log names the 429 / `LLM_GATEKEEPER` cause
- **Machine check**: the AC 2 helper MUnit test variant where the mock always raises 429 asserts the error propagates after exactly `maxRetries+1` invocations (`verify-call times`). Retry delay is made property-driven (e.g. `${einstein.ratelimit.retryDelayMillis}`) so this test can run with a small delay.
- **Source**: inferred (R4)

### AC 5: No production-code or API change
- **Given** the feature branch
- **When** compared to the base
- **Then** nothing under `src/main/` changed and the revapi semver check passes as PATCH
- **Machine check**: `git diff --stat origin/master...HEAD -- src/main | wc -l` → `0`; `java -jar ../../tools/mule-extensions-revapi-maven-plugin/revapi-extension-cli/target/mule-extensions-revapi-cli.jar check --pom ./pom.xml` exits `0`
- **Source**: inferred (Non-Goals)

### AC 6: Back-to-back runs stay green
- **Given** valid Vault credentials for the shared tenant
- **When** the MUnit suites run twice in a row within one rate window (simulating overlapping SFCI lanes)
- **Then** both runs pass
- **Machine check**: `mvn clean verify -DruntimeProduct=MULE_EE -DruntimeVersion=4.6.27 && mvn verify -DruntimeProduct=MULE_EE -DruntimeVersion=4.6.27` (QA records both EXIT codes); then an SFCI PR build green on `continuous-integration/sfci/build`
- **Source**: GUS Success signal (Integration stage failures on #153–#156)

### Definition of Done
- [ ] All ACs above have a passing check (MUnit for AC 2–4; shell/grep for AC 1/5; live mvn for AC 6)
- [ ] `mvn clean verify -DruntimeProduct=MULE_EE -DruntimeVersion=4.6.27` passes on mule-einstein-ai-connector
- [ ] Spec drift check (qa-engineer) reports no unmet requirement
- [ ] Knowledge base updated (uber + connector classification verified)
- [ ] PR created per U10 rules (`feature/W-24422436`, label `MULE4-CONNECTORS-UBER`)
- [ ] Revapi semver check passes (no API delta)
- [ ] Follow-up proposals (connector-side 429 handling; tenant quota upgrade) captured in the RCA note and PR body, not implemented

## 5. AI Context Pack

### Reference files (read these first)
- `src/test/munit/EmbeddingOperationsTestCase.xml:21-191`: the suite that exhausts the embeddings quota. Lines 38 (EmbeddingFromFile), 55 (EmbeddingAdhocFileQuery), 94 (ToolsUseAiService) are the reported failures; 150/164/177 are negative tests that must keep their `expectedErrorType`.
- `src/test/munit/GenerationsOperationTestCase.xml:16-213`: generations suite; in scope only if AC 1 proves a shared budget.
- `src/test/munit/config.xml:13-36`: `Einstein_Config`, `Einstein_Config_TLS` (live tenant via `automation-credentials.properties`) and `*_Error` configs.
- `src/main/java/com/mulesoft/connector/einsteinai/internal/helpers/HttpRequestHelper.java:114-130`: where `ErrorCode: <status>` text is built (read-only for this story).
- `src/main/java/com/mulesoft/connector/einsteinai/internal/modelsapi/helpers/RequestHelper.java:145-282`: embedding fan-out (`getCorpusEmbeddings` makes one HTTP call per chunk; `getBatchCorpusEmbeddings` makes one per 100 chunks). Read-only.
- `src/main/java/com/mulesoft/connector/einsteinai/internal/operation/EinsteinEmbeddingOperations.java:95-120`: catch-all rethrow that buries the 429 in the cause. Read-only.
- `pom.xml:137-170`: MUnit plugin configuration.
- `.work-items/W-24422436/notes/codebase-analysis.md`: call inventory and error path.
- Uber: `knowledge/munit-sfdc-oauth-warmup-pattern.md`, `knowledge/munit-mocking-and-error-testing.md`, `knowledge/munit-parameterization.md` (`-Dmunit.test=.*EmbeddingOperationsTestCase.*` to isolate locally), `knowledge/munit-test-filter-suite-regex-and-jdk25-unsafe.md`.

### Constraints (what NOT to do)
- Do not modify anything under `src/main/`. No ExtensionModel change, no new error type.
- Do not remove, ignore, or weaken any existing MUnit test or assertion.
- Do not wrap `expectedErrorType` tests in `until-successful`, since that would change the propagated error type.
- Do not retry on non-429 errors.
- Do not mock the Einstein API in the existing live tests; mocks are allowed only in the new helper-flow tests (AC 2–4).
- Do not add dependencies without `/versions` confirmation.
- Do not extend `MuleAbstractFunctionalTestCase`.
- No workflow-artifact references (W-ids, `.work-items/` paths) in committed test code or comments ([`knowledge/sdd/no-workflow-artifact-refs-in-code.md`](../../../../knowledge/sdd/no-workflow-artifact-refs-in-code.md)).

### Conventions to follow
- MUnit XML under `src/test/munit/`; suite-per-operation-family naming (`<Family>OperationsTestCase.xml`); shared configs in `config.xml`.
- Test names stay as they are (SFCI history and the GUS evidence reference them).
- Build args: none connector-specific beyond `-DruntimeProduct=MULE_EE -DruntimeVersion=<v>`. There is no `connectors/mule-einstein-ai-connector/knowledge/` build-args entry yet; FIPS lanes use profiles `fips140-2` / `fips140-3` (pom.xml:242-265).
- Vault credentials: `automation-credentials.properties` via `connector-maven-plugin:3.0.1:get-configuration-files -Dvault.secret.engine=connector-credentials`.

### Open questions / risks
- **Error-field location of the 429 text** (Section 3 edge cases): if no DataWeave-reachable error field carries `429`, a test-side 429-only retry is impossible without a `src/main` change. Fallback within scope: pacing plus fewer calls (R5) with a retry on the operation's error type, guarded by a second assertion that the cause text matches the gatekeeper signature. If even that fails, escalate to the EM rather than touching `src/main`.
- **Cross-lane contention** cannot be fully removed test-side; the retry budget must cover one or two extra windows. Residual risk goes in the PR body.
- **Build-time increase** of up to a few minutes in the worst case, which is acceptable compared with a red stage.
- **Tenant upgrade** (`org_status: Free` → paid/higher quota) is the real durable fix and is recommended as a plan-only follow-up.
