# MUnit lazy init does not follow dynamic flow-refs

## Context
Found while routing the live Einstein MUnit tests through a shared retry sub-flow
(`einstein-call-with-rate-limit-retry` in `src/test/munit/rate-limit-helpers.xml`). That sub-flow
calls the target flow with `<flow-ref name="#[vars.rateLimitedFlow]"/>`.

## Details
- MUnit initializes only the components a test references statically. A flow reached through
  a dynamic `flow-ref` name is never initialized, so the test fails with
  `MULE:ROUTING No flow/sub-flow with name '<x>' found`.
- Fix: in every test that reaches a flow this way, declare it with
  `<munit:enable-flow-sources><munit:enable-flow-source value="<flow-name>"/></munit:enable-flow-sources>`.
- In DataWeave, `input` is a reserved word. A stub that returns `{input: payload}` does not
  compile. Use another key, e.g. `echoedInput`.
- `error.description` holds the message of the root `MuleException`. For this connector that is
  `Error in HTTP request. ErrorCode: 429 ...`. Match `/ErrorCode:\s*429\b/` or `LLM_GATEKEEPER` to
  detect throttling.

## Examples
See `RateLimitRetryHelperTestCase.xml`, where each test enables `simulated-einstein-call`.
