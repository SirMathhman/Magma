package org.example;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class MatchTest {
  @Test
  void matchSelectsExactArm() {
  assertValid("let x = match 5 { 10 => 1; 5 => 2; _ => 3; }; x", "2");
  }

  @Test
  void matchFallsBackToWildcard() {
  assertValid("let x = match 7 { 10 => 1; 5 => 2; _ => 3; }; x", "3");
  }

  @Test
  void matchRequiresBracesAndSemicolons() {
    assertInvalid("let x = match 5 10 => 1; _ => 0; ");
  }
}
