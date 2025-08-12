package magma;

import static magma.TestUtils.assertInvalid;
import static magma.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

class CompilerBasicTest {
  @Test
  void valid() {
    assertValid("", "");
  }

  @Test
  void let() {
    assertValid("let x = 100;", "int32_t x = 100;");
  }

  @Test
  void letName() {
    assertValid("let y = 100;", "int32_t y = 100;");
  }

  @Test
  void letValue() {
    assertValid("let z = 100;", "int32_t z = 100;");
  }

  @Test
  void letExplicitType() {
    assertValid("let a : I32 = 100;", "int32_t a = 100;");
  }

  @Test
  void letAnnotatedTyped() {
    assertValid("let x = 100I32;", "int32_t x = 100;");
  }

  @Test
  void invalidTypeAnnotation() {
    assertInvalid("let x : I32 = 0U64;");
  }

  @Test
  void invalid() {
    assertInvalid("not empty");
  }

  @Test
  void validMut() {
    assertValid("let mut x = 100; x = 200;", "int32_t x = 100; x = 200;");
  }

  @Test
  void invalidMut() {
    assertInvalid("let x = 100; x = 200;");
  }
}
