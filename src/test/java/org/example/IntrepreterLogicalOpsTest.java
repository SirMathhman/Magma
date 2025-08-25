package org.example;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class IntrepreterLogicalOpsTest {
  @Test
  void orTrueFalse() {
    assertValid("true || false", "true");
  }

  @Test
  void andTrueFalse() {
    assertValid("true && false", "false");
  }

  @Test
  void andHasHigherPrecedenceThanOr() {
    assertValid("true || false && false", "true");
  }

  @Test
  void leftAssociativityForAnd() {
    assertValid("true && true && false", "false");
  }

  @Test
  void nonBooleanLeftIsInvalidForOr() {
    assertInvalid("1 || true");
  }

  @Test
  void nonBooleanRightIsInvalidForAnd() {
    assertInvalid("false && 1");
  }
}
