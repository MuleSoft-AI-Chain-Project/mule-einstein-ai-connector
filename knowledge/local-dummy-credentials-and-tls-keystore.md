# Local dummy credentials break suite deployment on the TLS keystore

## Context
Found during QA of the 429-retry change to the MUnit suites, on a machine where Vault was not
configured (`connector-maven-plugin:get-configuration-files` failed in `VaultConfig.address`, no
`VAULT_ADDR` and no `~/.vault-token`). A git-ignored placeholder
`src/test/resources/automation-credentials.properties` (header `# LOCAL DUMMY`) was present instead.

## Details
- With the placeholder, `EmbeddingOperationsTestCase.xml` and `GenerationsOperationTestCase.xml` do
  not get as far as running tests. Each suite fails while it is being built: `Einstein_Config_TLS`
  and `Einstein_Config_Error_TLS` load `${m4-mutual-tls.keyStore}.${keystore.ks.type}`
  (`mutualtls.jks`) with `m4-mutual-tls.keyStorePassword=dummy`, and the JDK throws
  `Keystore was tampered with, or password was incorrect`. Maven reports
  `Tests: 1, Errors: 1` for each of the two suites. That is an environment failure, not a regression.
- Only the self-contained suites (for example `RateLimitRetryHelperTestCase.xml`) give a real
  signal without Vault.
- To check the XML wiring of the live suites without Vault, generate a throwaway keystore
  (`keytool -genkeypair ... -keystore src/test/resources/qatls.jks -storepass <pw> -storetype jks`)
  and temporarily point `m4-mutual-tls.keyStore` / `m4-mutual-tls.keyStorePassword` at it. The suites
  then deploy. Positive tests fail with `MS-EINSTEIN-AI:CONNECTIVITY` ("Could not obtain access
  token"). The three Embedding negative tests fail because they get CONNECTIVITY instead of their
  expected type. The two Generations `*WithError` tests pass. Delete the keystore and restore the
  placeholder afterwards.
- A green local `mvn clean verify` therefore requires real Vault credentials. Without them the live
  gate has to come from the SFCI PR build.

## Examples
`grep -c "No flow/sub-flow with name"` returned 0 and positive tests failed at once with their
original error type, so the dynamic flow-ref wiring and non-429 propagation were confirmed without Vault.
