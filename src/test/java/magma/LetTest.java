package magma;

import org.junit.jupiter.api.Test;

public class LetTest {
  @Test
  void basicLet() {
    TestHelpers.assertValid("let x = 10; x", "10");
  }

  @Test
  void multiple() {
    TestHelpers.assertValid("let x = 10; let y = 20; x", "10");
  }

  @Test
  void duplicateName() {
    TestHelpers.assertInvalid("""
        let x = 10;
        let x = 20;""", """
        'x' already defined.

        File: <virtual>

        1) let x = 10;
               ^
               
        2) let x = 20;
               ^""");

  }
}
