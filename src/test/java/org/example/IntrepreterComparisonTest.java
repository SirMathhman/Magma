package org.example;

import org.junit.jupiter.api.Test;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

public class IntrepreterComparisonTest {

  @Test
  void intEqualityTrue() {
    assertValid("1 == 1", "true");
  }

  @Test
  void intLessFalse() {
    assertValid("2 < 1", "false");
  }

  @Test
  void floatLessTrue() {
    assertValid("1.0 < 2.0", "true");
  }

  @Test
  void floatEqFalse() {
    assertValid("1.5 == 1.4", "false");
  }

  @Test
  void mixedIntFloatInvalid() {
    assertInvalid("1 == 1.0");
  }

  @Test
  void mismatchedTypedIntsInvalid() {
    assertInvalid("1I16 == 1I32");
  }

  @Test
  void typedVsUntypedInvalid() {
    assertInvalid("1I32 < 2");
  }

  @Test
  void matchingTypedIntsValid() {
    assertValid("2I64 >= 1I64", "true");
  }
}
