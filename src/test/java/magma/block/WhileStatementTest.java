package magma.block;

import org.junit.jupiter.api.Test;

import static magma.infra.TestUtils.*;

public class WhileStatementTest {

  @Test
  void simpleWhileLoopConsumesNothingWhenFalse() {
    // loop should not execute and final expression should be 0 (we return 0 by
    // default)
    // We'll write a small program that uses while(false) { let x = readInt(); } 5
    assertValidWithPrelude("let x = 0; while (false) { let x = readInt(); } x", "", 0);
  }

  @Test
  void whileLoopWithBodyMutation() {
    // increment a counter using readInt and exit via assignment; use provided input
    assertValidWithPrelude("let mut x = 0; let mut i = 0; while (i < 1) { x = readInt(); i = i + 1; } x", "42",
        42);
  }
}
