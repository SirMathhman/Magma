package magma.logic;

import org.junit.jupiter.api.Test;

import static magma.infra.TestUtils.*;

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

  @Test
  void invalidTrailingOperator() {
    assertInvalid("true &&");
  }

  @Test
  void invalidLeadingOperator() {
    assertInvalid("&& true");
  }

  @Test
  void singleAmpersandInvalid() {
    assertInvalid("true & true");
  }
}
