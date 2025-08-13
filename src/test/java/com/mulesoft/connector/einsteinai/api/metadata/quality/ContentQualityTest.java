package com.mulesoft.connector.einsteinai.api.metadata.quality;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ContentQualityTest {

  @Test
  void gettersReturnAssignedValues() {
    ScanToxicity toxicity = new ScanToxicity(false, null);
    ContentQuality quality = new ContentQuality(toxicity);

    assertSame(toxicity, quality.getScanToxicity());
  }

  @Test
  void equalsCoversEqualityAndInequality() {
    ScanToxicity toxicity1 = new ScanToxicity(false, null);
    ScanToxicity toxicity2 = new ScanToxicity(false, null);
    ScanToxicity toxicityDifferent = new ScanToxicity(true, null);

    ContentQuality a = new ContentQuality(toxicity1);
    ContentQuality b = new ContentQuality(toxicity2);
    ContentQuality c = new ContentQuality(toxicityDifferent);

    assertEquals(a, a);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(null, a);
    assertNotEquals(new Object(), a);
    assertNotEquals(a, c);
  }
}


