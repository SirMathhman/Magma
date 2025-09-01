import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CompileTest {
  @Test
  void empty() {
    assertAllValidWithPrelude("", "", "");
  }

  private static final String PRELUDE = "extern fn readInt() : I32;";

  @Test
  void readInt() {
    assertAllValidWithPrelude("readInt()", "10", "10");
  }

  @Test
  void add() {
    assertAllValidWithPrelude("readInt() + readInt()", "10\r\n30", "40");
  }

  @Test
  void subtract() {
    assertAllValidWithPrelude("readInt() - readInt()", "10\r\n30", "-20");
  }

  @Test
  void multiply() {
    assertAllValidWithPrelude("readInt() * readInt()", "10\r\n30", "300");
  }

  @Test
  void let() {
    assertAllValidWithPrelude("let x = readInt(); x", "10", "10");
  }

  private void assertAllValidWithPrelude(String source, String stdIn, String stdOut) {
    assertAllValid(PRELUDE + " " + source, stdIn, stdOut);
  }

  @Test
  void assign() {
    assertAllValidWithPrelude("let mut x = 20; x = readInt(); x", "10", "10");
  }

  @Test
  void assignInvalidWithoutMut() {
    assertAllInvalid("let x = 20; x = readInt(); x");
  }

  private void assertAllInvalid(String source) {
    assertInvalid(new TSExecutor(), source);
    assertInvalid(new CExecutor(), source);
  }

  private void assertInvalid(Executor executor, String source) {
    assertTrue(new Runner(executor).run(PRELUDE + " " + source, "") instanceof Err,
        "LANG --- " + executor.getTargetLanguage() + ": Invalid code produced.");
  }

  private void assertValid(Executor executor, String source, String stdIn, String stdOut) {
    Runner runner = new Runner(executor);
    Result<String, RunError> result = runner.run(source, stdIn);
    switch (result) {
      case Err(var error) ->
        org.junit.jupiter.api.Assertions.fail("Lang --- " + executor.getTargetLanguage() + ": " + error.toString());
      case Ok(var value) -> org.junit.jupiter.api.Assertions.assertEquals(
          stdOut,
          value,
          "LANG " + executor.getTargetLanguage() + ": output mismatch");
    }
  }

  private void assertAllValid(String source, String stdIn, String stdOut) {
    assertValid(new TSExecutor(), source, stdIn, stdOut);
    assertValid(new CExecutor(), source, stdIn, stdOut);
  }
}
