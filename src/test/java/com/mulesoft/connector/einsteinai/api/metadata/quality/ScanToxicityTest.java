package com.mulesoft.connector.einsteinai.api.metadata.quality;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScanToxicityTest {

  @Test
  void gettersReturnAssignedValues() {
    Categories cat = new Categories("toxicity", "0.9");
    ScanToxicity toxicity = new ScanToxicity(true, Collections.singletonList(cat));

    assertTrue(toxicity.getIsDetected());
    assertEquals(1, toxicity.getCategories().size());
    assertEquals(cat, toxicity.getCategories().get(0));
  }

  @Test
  void equalsCoversEqualityAndInequality() {
    Categories cat1 = new Categories("toxicity", "0.9");
    Categories cat2 = new Categories("violence", "0.1");

    ScanToxicity a = new ScanToxicity(true, Arrays.asList(cat1, cat2));
    ScanToxicity b = new ScanToxicity(true, Arrays.asList(new Categories("toxicity", "0.9"), new Categories("violence", "0.1")));
    ScanToxicity c = new ScanToxicity(false, Arrays.asList(cat1, cat2));
    ScanToxicity d = new ScanToxicity(true, Collections.singletonList(cat1));

    assertEquals(a, a);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(null, a);
    assertNotEquals(new Object(), a);
    assertNotEquals(a, c);
    assertNotEquals(a, d);
  }
}


