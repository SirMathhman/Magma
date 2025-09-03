package magma;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InvalidBoolLetTest {
  @Test
  void letBoolInvalidNumber() {
    String src = "intrinsic fn readInt() : I32; let x : Bool = 0; x";
    assertTrue(Runner.run(src, "") instanceof Result.Err);
  }

  @Test
  void letI32InitWithBool() {
    String src = "intrinsic fn readInt() : I32; let x : I32 = true; x";
    assertTrue(Runner.run(src, "") instanceof Result.Err);
  }

  @Test
  void intPlusBoolInvalid() {
    String src = "intrinsic fn readInt() : I32; 5 + true";
    assertTrue(Runner.run(src, "") instanceof Result.Err);
  }+

  @Test
  void boolPlusIntInvalid() {
    String src = "intrinsic fn readInt() : I32; true + 5";
    assertTrue(Runner.run(src, "") instanceof Result.Err);
  }
}
