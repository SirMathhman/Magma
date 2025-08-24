package org.example;

import static org.example.TestUtils.assertValid;
import static org.example.TestUtils.assertInvalid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class IntrepreterBasicTest {
  @Test
  void interpretShouldErrorOnBareWords() {
    assertInvalid("hello");
  }

  @ParameterizedTest
  @ValueSource(strings = { "5U8", "5U16", "5U32", "5U64" })
  void interpretShouldStripUSuffixes(String input) {
    assertValid(input, "5");
  }

  @ParameterizedTest
  @ValueSource(strings = { "5I8", "5I16", "5I32", "5I64" })
  void interpretShouldStripISuffixes(String input) {
    assertValid(input, "5");
  }

  @Test
  void interpretShouldAddTwoIntegers() {
    assertValid("5 + 7", "12");
  }

  @Test
  void interpretShouldSubtractTwoIntegers() {
    assertValid("5 - 7", "-2");
  }

  @Test
  void interpretShouldMultiplyTwoIntegers() {
    assertValid("5 * 7", "35");
  }
}
