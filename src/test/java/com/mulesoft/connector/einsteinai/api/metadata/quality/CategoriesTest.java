package com.mulesoft.connector.einsteinai.api.metadata.quality;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CategoriesTest {

  @Test
  void gettersReturnAssignedValues() {
    Categories categories = new Categories("toxicity", "0.9");

    assertEquals("toxicity", categories.getCategoryName());
    assertEquals("0.9", categories.getScore());
  }

  @Test
  void equalsCoversEqualityAndInequality() {
    Categories a = new Categories("toxicity", "0.9");
    Categories b = new Categories("toxicity", "0.9");
    Categories differentName = new Categories("violence", "0.9");
    Categories differentScore = new Categories("toxicity", "0.8");

    assertEquals(a, a); // reflexive
    assertEquals(a, b); // same data
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(null, a);
    assertNotEquals(new Object(), a);
    assertNotEquals(a, differentName);
    assertNotEquals(a, differentScore);
  }
}


