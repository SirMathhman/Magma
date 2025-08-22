package magma;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.*;

public class LetTest {
  @Test
  void let() {
    assertValidWithPrelude("let x : I32 = readInt(); x", "100", 100);
  }
}