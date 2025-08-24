package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntrepreterTest {
  @Test
  void interpretShouldEchoInput() {
    Intrepreter i = new Intrepreter();
    String input = "hello";
    String out = i.interpret(input);
    assertEquals(input, out);
  }

  @ParameterizedTest
  @ValueSource(strings = { "5U8", "5U16", "5U32", "5U64" })
  void interpretShouldStripUSuffixes(String input) {
    Intrepreter i = new Intrepreter();
    assertEquals("5", i.interpret(input));
  }

  @ParameterizedTest
  @ValueSource(strings = { "5I8", "5I16", "5I32", "5I64" })
  void interpretShouldStripISuffixes(String input) {
    Intrepreter i = new Intrepreter();
    assertEquals("5", i.interpret(input));
  }

  @Test
  void interpretShouldAddTwoIntegers() {
    Intrepreter i = new Intrepreter();
    assertEquals("12", i.interpret("5 + 7"));
  }

  @Test
  void interpretShouldSubtractTwoIntegers() {
    Intrepreter i = new Intrepreter();
    assertEquals("-2", i.interpret("5 - 7"));
  }

  @Test
  void interpretShouldMultiplyTwoIntegers() {
    Intrepreter i = new Intrepreter();
    assertEquals("35", i.interpret("5 * 7"));
  }
}
