import org.junit.jupiter.api.Test;

public class CompileTest {
  @Test
  void empty() {
    assertAllValid("", "", "");
  }

  @Test
  void readInt() {
    assertAllValid("extern fn readInt() : I32; readInt()", "10", "10");
  }

  private void assertValid(Executor executor, String source, String stdIn, String stdOut) {
    Runner runner = new Runner(executor);
    Result<String, RunError> result = runner.run(source, stdIn);
    org.junit.jupiter.api.Assertions.assertTrue(result instanceof Ok);
    org.junit.jupiter.api.Assertions.assertEquals(stdOut, ((Ok<String, RunError>) result).value());
  }

  private void assertAllValid(String source, String stdIn, String stdOut) {
    assertValid(new TSExecutor(), source, stdIn, stdOut);
    assertValid(new CExecutor(), source, stdIn, stdOut);
  }
}
