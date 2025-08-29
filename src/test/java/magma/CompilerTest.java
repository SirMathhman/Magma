package magma;

import magma.result.Result;
import magma.result.Ok;
import magma.result.Err;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Compiler class.
 */
public class CompilerTest {
  @Test
  void compileShouldReturnOkForNonEmptySource() {
    Compiler compiler = new Compiler();
    Result<String, CompileError> result = compiler.compile("print('Hello')");
    assertTrue(result instanceof Ok);
    assertEquals("Compiled: print('Hello')", ((Ok<String, CompileError>) result).value());
  }

  @Test
  void compileShouldReturnErrForEmptySource() {
    Compiler compiler = new Compiler();
    Result<String, CompileError> result = compiler.compile("");
    assertTrue(result instanceof Err);
    assertEquals("Source code is empty.", ((Err<String, CompileError>) result).error().getMessage());
  }
}
