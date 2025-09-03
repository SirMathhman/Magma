package magma;

import org.junit.jupiter.api.Test;

public class IfTest {
  @Test
  void simpleIfRuntimeEqTrue() {
    TestHelpers.assertValid("if (readInt() == 1) 3 else 5", "1", "3");
  }
}
