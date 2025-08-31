package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import magma.result.Err;
import magma.result.Ok;

public class CompilerTest {
  private static final String PRELUDE = "extern fn readInt() : I32; ";

  @Test
  void valid() {
    assertValid(PRELUDE + "readInt()", "5", "5");
  }

  @Test
  void invalid() {
    assertInvalid(PRELUDE + "test");
  }

  static void assertValid(String source, String stdIn, String stdOut) {
    var result = Runner.run(source, stdIn);
    if (result instanceof Ok<String, CompileError> ok) {
      assertEquals(stdOut, ok.value());
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
