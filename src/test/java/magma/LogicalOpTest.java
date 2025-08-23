package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertValid;

public class LogicalOpTest {

  @Test
  void andBothTrue() {
    assertValid("true && true", "", 1);
  }

  @Test
  void andOneFalse() {
    assertValid("true && false", "", 0);
  }

  @Test
  void orBothFalse() {
    assertValid("false || false", "", 0);
  }

  @Test
  void orOneTrue() {
    assertValid("false || true", "", 1);
  }
}
