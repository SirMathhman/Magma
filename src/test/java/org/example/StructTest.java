package org.example;

import org.junit.jupiter.api.Test;

class StructTest {

  @Test
  void structThenExprIsValid() {
    TestUtils.assertValid(
        "let x : I32; x = 5; struct MyStruct { field0 : I32, field1 : I32 }; x",
        "5");
  }

  @Test
  void structMissingBracesIsInvalid() {
    TestUtils.assertInvalid(
        "let x : I32; struct MyStruct field0 : I32, field1 : I32; x");
  }

  @Test
  void structMissingSemicolonIsInvalid() {
    TestUtils.assertInvalid(
        "let x : I32; x = 1; struct MyStruct { field0 : I32 } x");
  }

  @Test
  void emptyStructIsValid() {
    TestUtils.assertValid(
        "let x : I32; x = 7; struct Empty { }; x",
        "7");
  }

  @Test
  void structWithTrailingCommaIsInvalid() {
    TestUtils.assertInvalid(
        "let x : I32; x = 9; struct S { a : I32, }; x");
  }
}
