package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertInvalid;

public class LetInvalidTest {

  @Test
  void letShadowingInvalid() {
    // duplicate names at same scope
    assertInvalid("let x = 0; let x = 1; x");
  }

  @Test
  void assignUnknownNameInvalid() {
    assertInvalid("x = 1;");
  }

  @Test
  void pointerTypeMismatchInvalid() {
    assertInvalid("let x : I32 = 0; let y : *I32 = x; ");
  }
}
