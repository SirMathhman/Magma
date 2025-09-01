import org.junit.jupiter.api.Test;

public class CompileTest {
  @Test
  void emptyTest() {
    TSExecutor tsExecutor = new TSExecutor();
    Runner tsRunner = new Runner(tsExecutor);
    Result<String, RunError> tsResult = tsRunner.run("", "");
    org.junit.jupiter.api.Assertions.assertTrue(tsResult instanceof Ok);
    org.junit.jupiter.api.Assertions.assertEquals("", ((Ok<String, RunError>) tsResult).value());

    CExecutor cExecutor = new CExecutor();
    Runner cRunner = new Runner(cExecutor);
    Result<String, RunError> cResult = cRunner.run("", "");
    org.junit.jupiter.api.Assertions.assertTrue(cResult instanceof Ok);
    org.junit.jupiter.api.Assertions.assertEquals("", ((Ok<String, RunError>) cResult).value());
  }
}
