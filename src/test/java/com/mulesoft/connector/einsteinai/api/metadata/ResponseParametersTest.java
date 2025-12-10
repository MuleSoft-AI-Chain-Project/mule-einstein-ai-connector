package com.mulesoft.connector.einsteinai.api.metadata;

import com.mulesoft.connector.einsteinai.api.metadata.token.TokenUsage;
import com.mulesoft.connector.einsteinai.api.metadata.token.CompletionTokensDetails;
import com.mulesoft.connector.einsteinai.api.metadata.token.PromptTokensDetails;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ResponseParametersTest {

  @Test
  void constructorAndGetters_returnProvidedValues() {
    PromptTokensDetails prompt = new PromptTokensDetails(5, 1);
    CompletionTokensDetails completion = new CompletionTokensDetails(2, 0, 1, 1);
    TokenUsage tokenUsage = new TokenUsage(10, 20, 30, prompt, completion);

    ResponseParameters params = new ResponseParameters(tokenUsage, "gpt-test", "fingerprint", "objectType");

    assertSame(tokenUsage, params.getTokenUsage());
    assertEquals("gpt-test", params.getModel());
    assertEquals("fingerprint", params.getSystemFingerprint());
    assertEquals("objectType", params.getObject());
  }

  @Test
  void equalsAndHashCode_reflectAllFields() {
    PromptTokensDetails prompt1 = new PromptTokensDetails(5, 1);
    CompletionTokensDetails completion1 = new CompletionTokensDetails(2, 0, 1, 1);
    TokenUsage usage1 = new TokenUsage(10, 20, 30, prompt1, completion1);

    PromptTokensDetails prompt2 = new PromptTokensDetails(5, 1);
    CompletionTokensDetails completion2 = new CompletionTokensDetails(2, 0, 1, 1);
    TokenUsage usage2 = new TokenUsage(10, 20, 30, prompt2, completion2);

    ResponseParameters a = new ResponseParameters(usage1, "gpt-test", "fingerprint", "objectType");
    ResponseParameters b = new ResponseParameters(usage2, "gpt-test", "fingerprint", "objectType");

    ResponseParameters diffUsage = new ResponseParameters(
                                                          new TokenUsage(11, 20, 30, prompt1, completion1), "gpt-test",
                                                          "fingerprint", "objectType");
    ResponseParameters diffModel = new ResponseParameters(usage1, "other-model", "fingerprint", "objectType");
    ResponseParameters diffFingerprint = new ResponseParameters(usage1, "gpt-test", "other-fp", "objectType");
    ResponseParameters diffObject = new ResponseParameters(usage1, "gpt-test", "fingerprint", "otherObject");

    assertEquals(a, a);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());

    assertNotEquals(null, a);
    assertNotEquals(new Object(), a);

    assertNotEquals(a, diffUsage);
    assertNotEquals(a, diffModel);
    assertNotEquals(a, diffFingerprint);
    assertNotEquals(a, diffObject);
  }
}
