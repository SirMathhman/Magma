package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestHelpers {
  public static void assertValid(String source, String stdin, String expected) {
    String sourceWithPrelude = "intrinsic fn readInt() : I32; " + source;
    Result<String, RunError> r = Runner.run(sourceWithPrelude, stdin);
    switch (r) {
      case Result.Ok(var value) -> assertEquals(expected, value);
      case Result.Err(var error) -> fail(error.display());
      default -> fail("Unknown result variant");
    }
  }

  public static void assertInvalid(String source) {
    String sourceWithPrelude = "intrinsic fn readInt() : I32; " + source;
    Result<String, RunError> r = Runner.run(sourceWithPrelude, "");
    assertTrue(r instanceof Result.Err);
  }
}
