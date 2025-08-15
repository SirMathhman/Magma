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
}
