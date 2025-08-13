package com.mulesoft.connector.einsteinai.api.metadata.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PromptTokensDetailsTest {

  @Test
  void gettersReturnAssignedValues() {
    PromptTokensDetails details = new PromptTokensDetails(5, 2);
    assertEquals(5, details.getCachedTokens());
    assertEquals(2, details.getAudioTokens());
  }

  @Test
  void equalsCoversEqualityAndInequality() {
    PromptTokensDetails a = new PromptTokensDetails(5, 2);
    PromptTokensDetails b = new PromptTokensDetails(5, 2);
    PromptTokensDetails c = new PromptTokensDetails(6, 2);
    PromptTokensDetails d = new PromptTokensDetails(5, 3);

    assertEquals(a, a);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(null, a);
    assertNotEquals(new Object(), a);
    assertNotEquals(a, c);
    assertNotEquals(a, d);
  }
}


