package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class CompileTest {
  @Test
  void readInt() {
    assertValid(Runner.run("intrinsic fn readInt() : I32; readInt()", "10"), "10");
  }

  private static void assertValid(Result<String, RunError> r, String expected) {
    switch (r) {
      case Result.Ok(var value) -> assertEquals(expected, value);
      case Result.Err(var error) -> fail(error.display());
      default -> fail("Unknown result variant");
    }
  }

  @Test
  void add() {
    assertValid(Runner.run("intrinsic fn readInt() : I32; readInt() + readInt()", "10\r\n20"), "30");
  }

  @Test
  void subtract() {
    assertValid(Runner.run("intrinsic fn readInt() : I32; readInt() - readInt()", "10\r\n20"), "-10");
  }
}
