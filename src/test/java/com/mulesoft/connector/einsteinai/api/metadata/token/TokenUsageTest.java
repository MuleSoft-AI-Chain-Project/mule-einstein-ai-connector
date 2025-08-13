package com.mulesoft.connector.einsteinai.api.metadata.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class TokenUsageTest {

  @Test
  void gettersReturnAssignedValues() {
    PromptTokensDetails prompt = new PromptTokensDetails(5, 1);
    CompletionTokensDetails completion = new CompletionTokensDetails(2, 0, 1, 1);
    TokenUsage usage = new TokenUsage(10, 20, 30, prompt, completion);

    assertEquals(10, usage.getInputCount());
    assertEquals(20, usage.getOutputCount());
    assertEquals(30, usage.getTotalCount());
    assertSame(prompt, usage.getPromptTokenDetails());
    assertSame(completion, usage.getCompletionTokenDetails());
  }

  @Test
  void equalsCoversEqualityAndInequality() {
    PromptTokensDetails prompt1 = new PromptTokensDetails(5, 1);
    CompletionTokensDetails completion1 = new CompletionTokensDetails(2, 0, 1, 1);
    TokenUsage a = new TokenUsage(10, 20, 30, prompt1, completion1);

    PromptTokensDetails prompt2 = new PromptTokensDetails(5, 1);
    CompletionTokensDetails completion2 = new CompletionTokensDetails(2, 0, 1, 1);
    TokenUsage b = new TokenUsage(10, 20, 30, prompt2, completion2);

    TokenUsage diffInput = new TokenUsage(11, 20, 30, prompt1, completion1);
    TokenUsage diffOutput = new TokenUsage(10, 21, 30, prompt1, completion1);
    TokenUsage diffTotal = new TokenUsage(10, 20, 31, prompt1, completion1);
    TokenUsage diffPrompt = new TokenUsage(10, 20, 30, new PromptTokensDetails(6, 1), completion1);
    TokenUsage diffCompletion = new TokenUsage(10, 20, 30, prompt1, new CompletionTokensDetails(3, 0, 1, 1));

    assertEquals(a, a);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(null, a);
    assertNotEquals(new Object(), a);
    assertNotEquals(a, diffInput);
    assertNotEquals(a, diffOutput);
    assertNotEquals(a, diffTotal);
    assertNotEquals(a, diffPrompt);
    assertNotEquals(a, diffCompletion);
  }
}


