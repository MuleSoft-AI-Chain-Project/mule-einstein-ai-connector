package com.mulesoft.connector.einsteinai.api.metadata.quality;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.CategoriesDTO;
import com.mulesoft.connector.einsteinai.internal.modelsapi.dto.ContentQualityDTO;
import com.mulesoft.connector.einsteinai.internal.modelsapi.helpers.ObjectMapperProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for Categories deserialization to ensure toxicity scores maintain precision. This test addresses the bug where small
 * decimal values like 0.0003, 0.001, 3.0E-4, or 0.0025 were being lost during JSON deserialization. Uses internal DTOs to
 * preserve precision while maintaining backward compatibility with String API.
 */
class CategoriesDeserializationTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = ObjectMapperProvider.create();
  }

  @Test
  void testDeserializeSmallDecimalScores() throws Exception {
    // Test cases for the specific values mentioned in the bug report
    String[] testValues = {
        "0.0003",
        "0.001",
        "3.0E-4",
        "0.0025",
        "1.5E-5",
        "0.0000012",
        "0.000"
    };

    String[] expectedStringValues = {
        "0.0003",
        "0.001",
        "0.00030", // 3.0E-4 converted to decimal notation
        "0.0025",
        "0.000015", // 1.5E-5 converted to decimal notation
        "0.0000012",
        "0.000"
    };

    for (int i = 0; i < testValues.length; i++) {
      String json = String.format("{\"categoryName\":\"identity\",\"score\":%s}", testValues[i]);

      CategoriesDTO category = objectMapper.readValue(json, CategoriesDTO.class);
      Categories finalCategories = category.toCategories();
      assertNotNull(finalCategories.getScore(), "Score should not be null for value: " + testValues[i]);
      // Verify the string representation preserves the same numeric value
      assertEquals(expectedStringValues[i], finalCategories.getScore(),
                   "Score should preserve precision for value: " + testValues[i]);
      assertEquals("identity", category.toCategories().getCategoryName());
    }
  }

  @Test
  void testContentQualityWithToxicityScores() throws Exception {
    String json = "{\n" +
        "  \"scanToxicity\": {\n" +
        "    \"isDetected\": true,\n" +
        "    \"categories\": [\n" +
        "      {\n" +
        "        \"categoryName\": \"identity\",\n" +
        "        \"score\": 0.0003\n" +
        "      },\n" +
        "      {\n" +
        "        \"categoryName\": \"hate\",\n" +
        "        \"score\": 3.0E-4\n" +
        "      },\n" +
        "      {\n" +
        "        \"categoryName\": \"profanity\",\n" +
        "        \"score\": 0.0025\n" +
        "      }\n" +
        "    ]\n" +
        "  }\n" +
        "}";

    ContentQualityDTO contentQualityDTO = objectMapper.readValue(json, ContentQualityDTO.class);

    assertNotNull(contentQualityDTO.getScanToxicity());
    assertEquals(3, contentQualityDTO.getScanToxicity().getCategories().size());

    ContentQuality responseContentQuality = contentQualityDTO.toContentQuality();
    // Verify each category maintains precision
    Categories identityCategory = responseContentQuality.getScanToxicity().getCategories().get(0);
    assertEquals("0.0003", identityCategory.getScore());
    assertEquals("identity", identityCategory.getCategoryName());

    Categories hateCategory = responseContentQuality.getScanToxicity().getCategories().get(1);
    assertEquals("0.00030", hateCategory.getScore()); // 3.0E-4 converted to decimal notation
    assertEquals("hate", hateCategory.getCategoryName());

    Categories profanityCategory = responseContentQuality.getScanToxicity().getCategories().get(2);
    assertEquals("0.0025", profanityCategory.getScore());
    assertEquals("profanity", profanityCategory.getCategoryName());
  }

}
