package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import magma.result.Err;
import magma.result.Ok;

public class CompilerTest {
  static void assertValid(String source, String expectedOutput) {
    var result = Runner.run(source);
    if (result instanceof Ok<String, CompileError> ok) {
      assertEquals(expectedOutput, ok.value());
    } else if (result instanceof Err<String, CompileError> err) {
      fail(err.error().getMessage());
    } else {
      fail("Unknown result type");
    }
  }

  static void assertInvalid(String source) {
    var result = Runner.run(source);
    assertTrue(result.isErr());
  }
}
