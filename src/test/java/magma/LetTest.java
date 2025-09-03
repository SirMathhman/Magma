package magma;

import org.junit.jupiter.api.Test;

public class LetTest {
  @Test
  void basicLet() {
    TestHelpers.assertValid("let x = 10; x", "10");
  }
}
