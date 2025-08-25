package org.example;

import static org.example.TestUtils.assertInvalid;
import static org.example.TestUtils.assertValid;

import org.junit.jupiter.api.Test;

public class InterpreterFunctionSemanticsTest {
  @Test
  void callingUndefinedFunctionIsInvalid() {
    assertInvalid("let x : I32; x = 1; add(3, 4)");
  }

  @Test
  void wrongArityTooFewIsInvalid() {
    assertInvalid("let x : I32; x = 0; fn add(a : I32, b : I32) : I32 => 5; add(1)");
  }

  @Test
  void wrongArityTooManyIsInvalid() {
    assertInvalid("let x : I32; x = 0; fn add(a : I32, b : I32) : I32 => 5; add(1, 2, 3)");
  }

  @Test
  void duplicateFunctionNameIsInvalid() {
    assertInvalid("let x : I32; fn add(a : I32) : I32 => 0; fn add(b : I32) : I32 => 0; x");
  }

  @Test
  void duplicateParameterNamesAreInvalid() {
    assertInvalid("let x : I32; fn bad(a : I32, a : I32) : I32 => 0; x");
  }

  @Test
  void validFunctionCallReturnsBodyValue() {
    assertValid("let x : I32; fn fortyTwo() : I32 => 42; fortyTwo()", "42");
  }

  @Test
  void thisInsideFunctionExposesParametersAsFields() {
    // fn accept(x : I32) => {this}; accept(100).x should yield 100
    assertValid("fn accept(x : I32) => {this}; accept(100).x", "100");
  }

  @Test
  void thisInsideFunctionWithoutParamsExposesLocalsAsFields() {
    // fn accept() => {let x = 100; this}; accept().x should yield 100
    assertValid("fn accept() => {let x = 100; this}; accept().x", "100");
  }
}
