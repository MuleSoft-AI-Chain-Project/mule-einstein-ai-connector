package com.mulesoft.connector.einsteinai.api.metadata.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CompletionTokensDetailsTest {

  @Test
  void gettersReturnAssignedValues() {
    CompletionTokensDetails details = new CompletionTokensDetails(10, 3, 7, 2);
    assertEquals(10, details.getReasoningTokens());
    assertEquals(3, details.getAudioTokens());
    assertEquals(7, details.getAcceptedPredictionTokens());
    assertEquals(2, details.getRejectedPredictionTokens());
  }

  @Test
  void equalsCoversEqualityAndInequality() {
    CompletionTokensDetails a = new CompletionTokensDetails(10, 3, 7, 2);
    CompletionTokensDetails b = new CompletionTokensDetails(10, 3, 7, 2);
    CompletionTokensDetails c = new CompletionTokensDetails(11, 3, 7, 2);
    CompletionTokensDetails d = new CompletionTokensDetails(10, 4, 7, 2);
    CompletionTokensDetails e = new CompletionTokensDetails(10, 3, 8, 2);
    CompletionTokensDetails f = new CompletionTokensDetails(10, 3, 7, 3);

    assertEquals(a, a);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(null, a);
    assertNotEquals(new Object(), a);
    assertNotEquals(a, c);
    assertNotEquals(a, d);
    assertNotEquals(a, e);
    assertNotEquals(a, f);
  }
}


