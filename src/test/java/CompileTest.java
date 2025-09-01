import org.junit.jupiter.api.Test;

public class CompileTest {
  @Test
  void empty() {
    assertAllValid("", "", "");
  }

  private static final String PRELUDE = "extern fn readInt() : I32;";

  @Test
  void readInt() {
    assertAllValid(PRELUDE + " readInt()", "10", "10");
  }

  @Test
  void add() {
    assertAllValid(PRELUDE + " readInt() + readInt()", "10\r\n30", "40");
  }

  @Test
  void subtract() {
    assertAllValid(PRELUDE + " readInt() - readInt()", "10\r\n30", "-20");
  }

  @Test
  void multiply() {
    assertAllValid(PRELUDE + " readInt() * readInt()", "10\r\n30", "300");
  }

  private void assertValid(Executor executor, String source, String stdIn, String stdOut) {
    Runner runner = new Runner(executor);
    Result<String, RunError> result = runner.run(source, stdIn);
    // Use pattern-matching switch (requires Java 20+ preview or Java 21+ depending
    // on features)
    switch (result) {
      // Err(error) -> bind the error component (RunError)
      case Err(var error) ->
        org.junit.jupiter.api.Assertions.fail("Lang --- " + executor.getTargetLanguage() + ": " + error.toString());
      // Ok(value) -> bind the value component (String)
      case Ok(var value) -> org.junit.jupiter.api.Assertions.assertEquals(
          stdOut,
          value,
          "Lang " + executor.getTargetLanguage() + ": output mismatch");
    }
  }

  private void assertAllValid(String source, String stdIn, String stdOut) {
    assertValid(new TSExecutor(), source, stdIn, stdOut);
    assertValid(new CExecutor(), source, stdIn, stdOut);
  }
}
