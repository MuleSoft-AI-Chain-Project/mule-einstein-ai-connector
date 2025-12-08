package com.mulesoft.connector.einsteinai.api.metadata;

import com.mulesoft.connector.einsteinai.api.metadata.quality.Categories;
import com.mulesoft.connector.einsteinai.api.metadata.quality.ContentQuality;
import com.mulesoft.connector.einsteinai.api.metadata.quality.ScanToxicity;
import com.mulesoft.connector.einsteinai.api.metadata.token.TokenUsage;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class EinsteinResponseAttributesTest {

  @Test
  void constructorAndGetters_returnProvidedValues() {
    ContentQuality contentQuality = new ContentQuality(new ScanToxicity(true, Collections.emptyList()));
    GenerationParameters generationParameters =
        new GenerationParameters("stop", "", 1, "logprobs", Collections.singletonList("annotation"));
    ResponseParameters responseParameters =
        new ResponseParameters(new TokenUsage(10, 20, 30, null, null), "gpt-test", "fingerprint", "objectType");

    EinsteinResponseAttributes attributes =
        new EinsteinResponseAttributes("resp-1", "gen-1", contentQuality, generationParameters, responseParameters);

    assertEquals("resp-1", attributes.getResponseId());
    assertEquals("gen-1", attributes.getGenerationId());
    assertEquals(contentQuality, attributes.getContentQuality());
    assertEquals(generationParameters, attributes.getGenerationParameters());
    assertEquals(responseParameters, attributes.getResponseParameters());
  }

  @Test
  void equalsAndHashCode_reflectAllFields() {
    ContentQuality cq1 =
        new ContentQuality(new ScanToxicity(true, Collections.singletonList(new Categories("Some Category", "0.09"))));
    ContentQuality cq2 =
        new ContentQuality(new ScanToxicity(true, Collections.singletonList(new Categories("Some Category", "0.09"))));
    ContentQuality cqDifferent =
        new ContentQuality(new ScanToxicity(false, Collections.singletonList(new Categories("Some Category", "0.1"))));

    GenerationParameters gp1 = new GenerationParameters("stop", "", 1, "logprobs",
                                                        Collections.singletonList("annotation"));
    GenerationParameters gp2 = new GenerationParameters("stop", "", 1, "logprobs",
                                                        Collections.singletonList("annotation"));
    GenerationParameters gpDifferent = new GenerationParameters("length", "refusal", 2, "other",
                                                                Collections.singletonList("other"));

    ResponseParameters rp1 =
        new ResponseParameters(new TokenUsage(10, 20, 30, null, null), "gpt-test", "fingerprint", "objectType");
    ResponseParameters rp2 =
        new ResponseParameters(new TokenUsage(10, 20, 30, null, null), "gpt-test", "fingerprint", "objectType");
    ResponseParameters rpDifferent =
        new ResponseParameters(new TokenUsage(1, 2, 3, null, null), "other-model", "other-fp", "otherObject");

    EinsteinResponseAttributes a1 = new EinsteinResponseAttributes("resp-1", "gen-1", cq1, gp1, rp1);
    EinsteinResponseAttributes a2 = new EinsteinResponseAttributes("resp-1", "gen-1", cq2, gp2, rp2);

    // same values -> equals and same hashCode
    assertEquals(a1, a2);
    assertEquals(a1.hashCode(), a2.hashCode());

    // self, null, different type
    assertEquals(a1, a1);
    assertNotEquals(a1, null);
    assertNotEquals(a1, "someString");

    // differences in each field break equality
    EinsteinResponseAttributes diffResponseId = new EinsteinResponseAttributes("resp-2", "gen-1", cq1, gp1, rp1);
    EinsteinResponseAttributes diffGenerationId = new EinsteinResponseAttributes("resp-1", "gen-2", cq1, gp1, rp1);
    EinsteinResponseAttributes diffContentQuality = new EinsteinResponseAttributes("resp-1", "gen-1", cqDifferent, gp1, rp1);
    EinsteinResponseAttributes diffGenerationParameters =
        new EinsteinResponseAttributes("resp-1", "gen-1", cq1, gpDifferent, rp1);
    EinsteinResponseAttributes diffResponseParameters =
        new EinsteinResponseAttributes("resp-1", "gen-1", cq1, gp1, rpDifferent);

    assertNotEquals(a1, diffResponseId);
    assertNotEquals(a1, diffGenerationId);
    assertNotEquals(a1, diffContentQuality);
    assertNotEquals(a1, diffGenerationParameters);
    assertNotEquals(a1, diffResponseParameters);
  }

  @Test
  void equals_handlesNullFieldsCorrectly() {
    EinsteinResponseAttributes a1 = new EinsteinResponseAttributes(null, null, null, null, null);
    EinsteinResponseAttributes a2 = new EinsteinResponseAttributes(null, null, null, null, null);
    EinsteinResponseAttributes aWithIdsOnly = new EinsteinResponseAttributes("resp-1", "gen-1", null, null, null);

    assertEquals(a1, a2);
    assertEquals(a1.hashCode(), a2.hashCode());
    assertNotEquals(a1, aWithIdsOnly);
  }
}
