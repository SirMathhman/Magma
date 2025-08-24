package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntrepreterTest {
  @Test
  void interpretShouldEchoInput() {
    Intrepreter i = new Intrepreter();
    String input = "hello";
    String out = i.interpret(input);
    assertEquals(input, out);
  }

  @Test
  void interpretShouldStripI32Suffix() {
    Intrepreter i = new Intrepreter();
    String input = "5I32";
    String out = i.interpret(input);
    assertEquals("5", out);
  }

  @Test
  void interpretShouldStripVariousNumericSuffixes() {
    // kept for historical grouping; individual tests below assert single behaviors
  }

  @Test
  void interpretShouldStripU8() {
    Intrepreter i = new Intrepreter();
    assertEquals("5", i.interpret("5U8"));
  }

  @Test
  void interpretShouldStripU16() {
    Intrepreter i = new Intrepreter();
    assertEquals("5", i.interpret("5U16"));
  }

  @Test
  void interpretShouldStripU32() {
    Intrepreter i = new Intrepreter();
    assertEquals("5", i.interpret("5U32"));
  }

  @Test
  void interpretShouldStripU64() {
    Intrepreter i = new Intrepreter();
    assertEquals("5", i.interpret("5U64"));
  }

  @Test
  void interpretShouldStripI8() {
    Intrepreter i = new Intrepreter();
    assertEquals("5", i.interpret("5I8"));
  }

  @Test
  void interpretShouldStripI16() {
    Intrepreter i = new Intrepreter();
    assertEquals("5", i.interpret("5I16"));
  }

  @Test
  void interpretShouldStripI64() {
    Intrepreter i = new Intrepreter();
    assertEquals("5", i.interpret("5I64"));
  }
}
