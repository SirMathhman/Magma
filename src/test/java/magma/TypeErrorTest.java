package magma;

import org.junit.jupiter.api.Test;

public class TypeErrorTest {
  @Test
  void letBoolInvalidNumber() {
    TestHelpers.assertInvalid("let x : Bool = 0; x");
  }

  @Test
  void letI32InitWithBool() {
    TestHelpers.assertInvalid("let x : I32 = true; x");
  }

  @Test
  void intPlusBoolInvalid() {
    TestHelpers.assertInvalid("5 + true");
  }

  @Test
  void boolPlusIntInvalid() {
    TestHelpers.assertInvalid("true + 5");
  }
}
