package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
  @Test
  void emptyStringReturnsEmptyForNonNumeric() {
    assertEquals("", App.emptyString(""));
    assertEquals("", App.emptyString("abc"));
  }

  @Test
  void emptyStringReturnsSameForIntegerString() {
    assertEquals("5", App.emptyString("5"));
    assertEquals("+42", App.emptyString("+42"));
    assertEquals("-7", App.emptyString("-7"));
  }

  @Test
  void emptyStringReturnsSameForDecimalString() {
    assertEquals("3.14", App.emptyString("3.14"));
    assertEquals("-0.5", App.emptyString(" -0.5 ".trim()));
  }

  @Test
  void emptyStringHandlesNull() {
    assertEquals("", App.emptyString(null));
  }

  @Test
  void additionOfIntegers() {
    assertEquals("7", App.emptyString("3 + 4"));
    assertEquals("0", App.emptyString("5 + -5"));
  }

  @Test
  void additionWithDecimals() {
    assertEquals("7.5", App.emptyString("3.5 + 4"));
    assertEquals("-1.1", App.emptyString("-2.1 + 1"));
  }

  @Test
  void subtractionTests() {
    assertEquals("1", App.emptyString("5 - 4"));
    assertEquals("-9", App.emptyString("-5 - 4"));
  }

  @Test
  void multiplicationTests() {
    assertEquals("12", App.emptyString("3 * 4"));
    assertEquals("-6", App.emptyString("-2 * 3"));
  }

  @Test
  void divisionTests() {
    assertEquals("2", App.emptyString("8 / 4"));
    assertEquals("0.5", App.emptyString("1 / 2"));
  }

  @Test
  void divisionByZeroReturnsEmpty() {
    assertEquals("", App.emptyString("1 / 0"));
  }
}
