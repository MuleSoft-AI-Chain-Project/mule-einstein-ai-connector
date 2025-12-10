package com.mulesoft.connector.einsteinai.api.metadata;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class GenerationParametersTest {

  @Test
  void fourArgConstructor_setsFieldsAndNullAnnotations() {
    GenerationParameters params = new GenerationParameters("stop", "", 1, "logprobs");

    assertEquals("stop", params.getFinishReason());
    assertEquals("", params.getRefusal());
    assertEquals(1, params.getIndex());
    assertEquals("logprobs", params.getLogprobs());
    assertNull(params.getAnnotations());
  }

  @Test
  void fiveArgConstructor_setsAllFieldsIncludingAnnotations() {
    List<String> annotations = Collections.singletonList("annotation");

    GenerationParameters params =
        new GenerationParameters("stop", "", 1, "logprobs", annotations);

    assertEquals("stop", params.getFinishReason());
    assertEquals("", params.getRefusal());
    assertEquals(1, params.getIndex());
    assertEquals("logprobs", params.getLogprobs());
    assertSame(annotations, params.getAnnotations());
  }

  @Test
  void equalsAndHashCode_reflectAllFields() {
    List<String> annotations1 = Collections.singletonList("annotation");
    List<String> annotations2 = Collections.singletonList("annotation");

    GenerationParameters a = new GenerationParameters("stop", "", 1, "logprobs", annotations1);
    GenerationParameters b = new GenerationParameters("stop", "", 1, "logprobs", annotations2);

    GenerationParameters diffFinishReason =
        new GenerationParameters("length", "", 1, "logprobs", annotations1);
    GenerationParameters diffRefusal =
        new GenerationParameters("stop", "refusal", 1, "logprobs", annotations1);
    GenerationParameters diffIndex =
        new GenerationParameters("stop", "", 2, "logprobs", annotations1);
    GenerationParameters diffLogprobs =
        new GenerationParameters("stop", "", 1, "other-logprobs", annotations1);
    GenerationParameters diffAnnotations =
        new GenerationParameters("stop", "", 1, "logprobs", Collections.singletonList("other"));

    assertEquals(a, a);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());

    assertNotEquals(null, a);
    assertNotEquals(new Object(), a);

    assertNotEquals(a, diffFinishReason);
    assertNotEquals(a, diffRefusal);
    assertNotEquals(a, diffIndex);
    assertNotEquals(a, diffLogprobs);
    assertNotEquals(a, diffAnnotations);
  }
}
