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
    // let mut x : I32 = 0; let y : *mut I32 = &mut x; *y = readInt(); x
    assertAllValidWithPrelude("let mut x : I32 = 0; let y : *mut I32 = &mut x; *y = readInt(); x", "42", "42");
  }

  @Test
  void writeThroughMutPointerDecl() {
    // let mut x : I32 = 0; let y : *mut I32 = &mut x; *y = readInt(); x
    assertAllValidWithPrelude("let mut x : I32 = 0; let y : *mut I32 = &mut x; *y = readInt(); x", "55", "55");
  }

  @Test
  void writeThroughMutPointerDeclInvalidWhenPointeeNotMutable() {
    // let x : I32 = 0; let y : *mut I32 = &x; *y = readInt(); x -- should be
    // invalid
    assertAllInvalidWithPrelude("let x : I32 = 0; let y : *mut I32 = &x; *y = readInt(); x");
  }

  @Test
  void multipleMutableBorrowsInvalid() {
    // let mut x = 0; let y = &mut x; let z = &mut x;  -- invalid
    assertAllInvalidWithPrelude("let mut x = 0; let y = &mut x; let z = &mut x;");
  }

  @Test
  void sharedRefsToMutableAllowed() {
    // let mut x = readInt(); let y = &x; let z = &x; *y + *z
    // x is read once, result should be 2*x
    assertAllValidWithPrelude("let mut x = readInt(); let y = &x; let z = &x; *y + *z", "3", "6");
  }

  @Test
  void sharedRefsToImmutableAllowed() {
    // let x = readInt(); let y = &x; let z = &x; *y + *z
    // x is read once, result should be 2*x
    assertAllValidWithPrelude("let x = readInt(); let y = &x; let z = &x; *y + *z", "2", "4");
  }

  @Test
  void arrayLiteralAndIndexing() {
    assertAllValidWithPrelude("let x : [I32; 1] = [readInt()]; x[0];", "5", "5");
  }
}
