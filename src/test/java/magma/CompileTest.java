package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class CompileTest {
  @Test
  void pass() {
    assertValid("readInt()", "10", "10");
  }

  private static void assertValid(String source, String stdin, String expected) {
    // Move the intrinsic declaration into the helper so tests only pass
    // the expression under test.
    String fullSource = "intrinsic fn readInt() : I32; " + source;
    Result<String, RunError> r = Runner.run(fullSource, stdin);
    switch (r) {
      case Result.Ok(var value) -> assertEquals(expected, value);
      case Result.Err(var error) -> fail(error.display());
      default -> fail("Unknown result variant");
    }
  }

  @Test
  void add() {
    assertValid("readInt() + readInt()", "10\r\n20", "30");
  }

  @Test
  void subtract() {
    assertValid("readInt() - readInt()", "10\r\n20", "-10");
  }

  @Test
  void multiply() {
    assertValid("readInt() * readInt()", "10\r\n20", "200");
  }

  @Test
  void divide() {
    assertValid("readInt() / readInt()", "10\r\n20", "0");
  }

  @Test
  void modulo() {
    assertValid("readInt() % readInt()", "30\r\n20", "10");
  }

  @Test
  void undefined() {
    assertTrue(Runner.run("readInt", "") instanceof Result.Err);
  }
}
