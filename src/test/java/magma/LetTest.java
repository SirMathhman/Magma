package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.*;

public class LetTest {
  @Test
  void let() {
    assertValidWithPrelude("let x : I32 = readInt(); x", "100", 100);
  }

  @Test
  void letWithImplicitType() {
    assertValidWithPrelude("let x = readInt(); x", "100", 100);
  }

  @Test
  void letMultiple() {
    assertValidWithPrelude("let x : I32 = readInt(); let y : I32 = readInt(); x + y", "100\r\n200", 300);
  }
}