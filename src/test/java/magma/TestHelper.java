package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TestHelper {
  private TestHelper() {}

  public static void assertInterpretsTo(String program, String expectedValue) {
    Interpreter interpreter = new Interpreter();
    Result<String, InterpretError> expected = Result.ok(expectedValue);
    Result<String, InterpretError> actual = interpreter.interpret(program, "");
    assertEquals(expected, actual);
  }

  public static void assertInterpretsToErr(String program) {
    Interpreter interpreter = new Interpreter();
    Result<String, InterpretError> actual = interpreter.interpret(program, "");
    // Expect an Err result
    org.junit.jupiter.api.Assertions.assertTrue(actual instanceof Result.Err,
        () -> "Expected Err result for program: " + program);
  }
}
