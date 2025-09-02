package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllValidWithPrelude;
import static magma.TestUtils.assertAllInvalidWithPrelude;

public class PointerTest {
  @Test
  void basicPointerDereference() {
    // let x : I32 = 0; let y : *I32 = &x; let z : I32 = *y; z
    assertAllValidWithPrelude("let x : I32 = 0; let y : *I32 = &x; let z : I32 = *y; z", "", "0");
  }

  @Test
  void writeThroughPointer() {
    // let mut x : I32 = 0; let y : *I32 = &x; *y = readInt(); x
    assertAllValidWithPrelude("let mut x : I32 = 0; let y : *I32 = &x; *y = readInt(); x", "42", "42");
  }

  @Test
  void writeThroughMutPointerDecl() {
    // let mut x : I32 = 0; let y : *mut I32 = &x; *y = readInt(); x
    assertAllValidWithPrelude("let mut x : I32 = 0; let y : *mut I32 = &x; *y = readInt(); x", "55", "55");
  }

  @Test
  void writeThroughMutPointerDeclInvalidWhenPointeeNotMutable() {
    // let x : I32 = 0; let y : *mut I32 = &x; *y = readInt(); x -- should be
    // invalid
    assertAllInvalidWithPrelude("let x : I32 = 0; let y : *mut I32 = &x; *y = readInt(); x");
  }
}
