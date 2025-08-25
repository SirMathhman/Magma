package org.example;

import org.junit.jupiter.api.Test;

class InterpreterStructSemanticsTest {
  @Test
  void duplicateFieldNamesAreInvalid() {
    TestUtils.assertInvalid(
        "let x : I32; x = 1; struct S { a : I32, a : I32 }; x");
  }

  @Test
  void duplicateStructNamesAreInvalid() {
    TestUtils.assertInvalid(
        "let x : I32; x = 1; struct S { a : I32 }; struct S { b : I32 }; x");
  }

  @Test
  void differentStructNamesAreValid() {
    TestUtils.assertValid(
        "let x : I32; x = 42; struct A { a : I32 }; struct B { b : I32 }; x",
        "42");
  }
}
