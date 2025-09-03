package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class CompileTest {
  @Test
  void test() {
    switch (Runner.run("intrinsic fn readInt() : I32; readInt()", "10")) {
      case Result.Ok(var value) -> {
        assertEquals("10", value);
      }
      case Result.Err(var error) -> {
        // Handle the error case
        fail(error.display());
      }
    }
  }
}
