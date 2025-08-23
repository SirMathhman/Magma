package magma.block;

import org.junit.jupiter.api.Test;

import static magma.infra.TestUtils.*;

public class IfStatementTest {

  @Test
  void ifExpressionAloneTrue() {
    assertValid("if (true) { 5 } else { 6 }", "", 5);
  }

  @Test
  void ifExpressionAloneFalse() {
    assertValid("if (false) { 5 } else { 6 }", "", 6);
  }

  @Test
  void ifExpressionWithReadIntUsesPrelude() {
    assertValidWithPrelude("if (true) { readInt() } else { readInt() }", "33", 33);
  }
}
