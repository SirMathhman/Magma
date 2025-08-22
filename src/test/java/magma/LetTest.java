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

  @Test
  void letInvalidWithSameName() {
    assertInvalid("let x : I32 = 0; let x : I32 = 0;");
  }

  @Test
  void letWithMismatchedType() {
    assertInvalid("let x : Bool = 0;");
  }

  @Test
  void letWithMut() {
    assertValidWithPrelude("let mut x = 0; x = readInt(); x", "100", 100);
  }

  @Test
  void letWithoutMut() {
    assertInvalid("let x = 0; x = readInt();");
  }
}