package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InterpreterTest {
  @Test
  void empty() {
    assertValid("", "");
  }

  @Test
  void readInt() {
    assertValidWithPrelude("readInt()", "100", "100");
  }

  @Test
  void add() {
    assertValidWithPrelude("readInt() + readInt()", "10\r\n20", "30");
  }

  @Test
  void letReadIntVariants() {
    assertValidWithPrelude("let x = readInt(); x", "42", "42");
    assertValidWithPrelude("let mut x = 0; x = readInt(); x", "7", "7");
  }

  @Test
  void twoLetsReadInt() {
    assertValidWithPrelude("let x = readInt(); let y = readInt(); x + y", "3\r\n4", "7");
  }

  @Test 
  void boolTrue() {
    assertValid("true", "true");
  }

  @Test
  void readIntEquals() {
    assertValidWithPrelude("readInt() == readInt()", "5\r\n5", "true");
    assertValidWithPrelude("readInt() == readInt()", "5\r\n6", "false");
  }

  private static void assertValid(String source, String expected) {
    assertValid(source, "", expected);
  }

  private static void assertValid(String source, String externalInput, String expected) {
    var interpreter = new Interpreter();
    var result = interpreter.interpret(source, externalInput);
    switch (result) {
      case Ok<String, InterpretError>(var actual) -> assertEquals(expected, actual);
      case Err<String, InterpretError>(var err) -> fail(err.display());
    }
  }

  private static void assertValidWithPrelude(String afterPrelude, String externalInput, String expected) {
    final String PRELUDE = "intrinsic fn readInt() : I32; ";
    assertValid(PRELUDE + afterPrelude, externalInput, expected);
  }
}
