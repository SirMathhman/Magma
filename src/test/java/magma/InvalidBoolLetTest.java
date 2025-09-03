package magma;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InvalidBoolLetTest {
  @Test
  void letBoolInvalidNumber() {
    String src = "intrinsic fn readInt() : I32; let x : Bool = 0; x";
    assertTrue(Runner.run(src, "") instanceof Result.Err);
  }
}
